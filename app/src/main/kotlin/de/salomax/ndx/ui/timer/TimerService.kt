package de.salomax.ndx.ui.timer

import android.app.Service
import android.content.Intent
import android.os.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.salomax.ndx.R
import de.salomax.ndx.data.PrefDao
import de.salomax.ndx.util.ManagedAlarmPlayer
import de.salomax.ndx.util.ManagedVibrator
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TimerService : Service() {

   private lateinit var alarmPlayer: ManagedAlarmPlayer
   private lateinit var vibrator: ManagedVibrator

   // Binder given to clients
   private var binder: LocalBinder? = null

   inner class LocalBinder : Binder() {
      // Return this instance of LocalService so clients can call public methods
      fun getService(): TimerService = this@TimerService
   }

   // target number
   internal var millisTotal: Long = 0
      private set

   // counting from 0 upwards
   internal var millisCurrent: MutableLiveData<Long> = MutableLiveData(0)
      private set

   // paused, running, etc.
   internal var state: MutableLiveData<State> = MutableLiveData(State.WAITING)
      private set

   // livecycle ====================================================================================

   override fun onCreate() {
      super.onCreate()
      binder = LocalBinder()
      alarmPlayer = ManagedAlarmPlayer(this)
      vibrator = ManagedVibrator(this)
   }

   override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
      firstStart()
      showNotification()
      return START_STICKY
   }

   override fun onUnbind(intent: Intent?): Boolean {
      showNotification()
      return true
   }

   override fun onBind(intent: Intent): IBinder? {
      hideNotification()
      return binder
   }

   override fun onRebind(intent: Intent?) {
      hideNotification()
      super.onRebind(intent)
   }

   override fun onDestroy() {
      hideNotification()
      executor.shutdownNow()
   }

   // notification =================================================================================

   private fun hideNotification() {
      millisCurrent.removeObserver(observer)
      stopForeground(STOP_FOREGROUND_REMOVE)
   }

   private fun showNotification() {
      if (state.value != State.WAITING)
         millisCurrent.observeForever(observer)
   }

   // needed to prevent ANR: "Context.startForegroundService() did not then call Service.startForeground()"
   private fun firstStart() {
      millisCurrent.observeForever(observer)
      hideNotification()
   }

   private var lastUpdate = 0L
   private val observer = Observer<Long> { current ->
      // max 4 notification updates/second - more than 10/second will be blocked
      if (System.currentTimeMillis() - lastUpdate > 250) {
         lastUpdate = System.currentTimeMillis()
         startForeground(1, TimerNotification(this).getTimerNotification(current, millisTotal))
      }
   }

   // countdown ====================================================================================

   private var executor = Executors.newSingleThreadScheduledExecutor()

   internal fun initCountdown(total: Long) {
      millisTotal = total
   }

   internal fun runCountdown() {
      executor.shutdownNow()
      executor = Executors.newSingleThreadScheduledExecutor()
      executor.scheduleAtFixedRate({
         // count current millis
         millisCurrent.postValue(millisCurrent.value?.plus(100))
         // state
         when {
            millisCurrent.value!! < millisTotal -> State.RUNNING_POSITIVE
            else -> State.RUNNING_NEGATIVE
         }.let { currentState ->
            // update state
            if (currentState != state.value)
               state.postValue(currentState)
            // play alarm/vibrate
            if (currentState == State.RUNNING_NEGATIVE) {
               playAlarm()
               vibrate()
            }
         }
      }, 0, 100, TimeUnit.MILLISECONDS)
   }

   internal fun pauseCountdown() {
      state.value = State.PAUSED
      executor.shutdownNow()
   }

   internal fun resetCountdown() {
      pauseCountdown()
      state.value = State.WAITING
      millisCurrent.value = 0
   }

   internal fun stopCountdown() {
      pauseCountdown()
      stopAlarm()
      stopVibrator()
      stopSelf()
   }

   // misc functions ===============================================================================

   private fun playAlarm() {
      if (PrefDao.getInstance(application).shouldAlarmBeepSync() && !alarmPlayer.isPlaying)
         alarmPlayer.play(R.raw.timer_expire_short)
   }

   private fun vibrate() {
      if (PrefDao.getInstance(application).shouldAlarmVibrateSync() && !vibrator.isVibrating) {
         val vibratorPattern = longArrayOf(0, 100, 100, 200, 500)
         vibrator.vibrate(vibratorPattern)
      }
   }

   private fun stopAlarm() {
      alarmPlayer.stop()
   }

   private fun stopVibrator() {
      vibrator.stop()
   }

   // ==============================================================================================

   enum class State {
      RUNNING_POSITIVE,
      RUNNING_NEGATIVE,
      PAUSED,
      WAITING
   }


}
