package de.salomax.ndx.ui.timer

import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import com.github.guilhe.circularprogressview.CircularProgressView
import com.joaquimverges.helium.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.Pref
import de.salomax.ndx.util.ManagedAlarmPlayer
import de.salomax.ndx.util.ManagedVibrator
import de.salomax.ndx.widget.BlinkAnimation
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ViewDelegate(inflater: LayoutInflater) : BaseViewDelegate<State, Event>(R.layout.activity_timer, inflater) {

    // all the views
    private val tvColon = view.findViewById<TextView>(R.id.colon)
    private val tvMinus = view.findViewById<TextView>(R.id.minus)
    private val tvMinutes = view.findViewById<TextView>(R.id.textMin)
    private val tvSeconds = view.findViewById<TextView>(R.id.textSec)
    private val tvHours = view.findViewById<TextView>(R.id.textHour)
    private val tvMillis = view.findViewById<TextView>(R.id.textMilli)
    private val circularProgress = view.findViewById<CircularProgressView>(R.id.progress)
    private val btnControl = view.findViewById<FloatingActionButton>(R.id.fab_pause)
    private val btnReset = view.findViewById<Button>(R.id.btn_reset)

    // animation, alarms
    private val blinkAnimation: Animation = BlinkAnimation()
    private val vibratorPattern = longArrayOf(0, 100, 100, 200, 500)
    private val vibrator: ManagedVibrator = ManagedVibrator.getInstance(context)
    private val alarmPlayer: ManagedAlarmPlayer = ManagedAlarmPlayer.getInstance(context)

    init {
        btnReset.setOnClickListener { pushEvent(Event.ResetTimer) }
    }

    override fun render(viewState: State) {
        when (viewState) {
            is State.InitOrReset -> { // stopped
                // update Text
                updateText(viewState.millisTotal, viewState.millisOffset)
                // play-button
                btnControl.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.RunCountdown) }
                // no blinking
                blinkText(false)
                circularProgress.clearAnimation()
                // hide reset
                btnReset.visibility = View.GONE
            }
            is State.CountdownRunning -> { // running
                // update Text
                updateText(viewState.millisTotal, viewState.millisOffset)
                // pause-button
                btnControl.setImageResource(R.drawable.ic_pause_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.PauseCountdown) }
                // no blinking
                blinkText(false)
                circularProgress.clearAnimation()
                // hide reset
                btnReset.visibility = View.GONE
            }
            is State.CountdownPaused -> { // stopped
                updateText(viewState.millisTotal, viewState.millisOffset)
                // play-button
                btnControl.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.RunCountdown) }
                // blink text
                if (tvColon.animation == null) blinkText(true)
                // show reset
                btnReset.visibility = View.VISIBLE
            }
            is State.CountdownFinished -> {// running
                // update Text
                updateText(viewState.millisTotal, viewState.millisOffset)
                // stop-button
                btnControl.setImageResource(R.drawable.ic_stop_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.Finish) }
                // blink progress
                if (circularProgress.animation == null)
                    circularProgress.startAnimation(blinkAnimation)
                // hide reset
                btnReset.visibility = View.GONE
                // alarm: check whether to beep and/or to vibrate
                Observable.just(NdxDatabase.getInstance(context))
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            for (pref in it.prefDao().getTimerAlarms()) {
                                when (pref.key) {
                                    Pref.ALARM_BEEP -> if (pref.value == "1") {
                                        alarmPlayer.play(R.raw.timer_expire_short)
                                    }
                                    Pref.ALARM_VIBRATE -> if (pref.value == "1") {
                                        vibrator.vibrate(vibratorPattern)
                                    }
                                }
                            }
                        }
            }
            is State.Finish -> {
                // stop alarm
                alarmPlayer.stop()
                // stop vibration
                vibrator.stop()
                // exit
                (context as AppCompatActivity).finish()
            }
        }
    }

    private fun blinkText(enabled: Boolean) {
        if (enabled && tvColon.animation == null) {
            tvColon.startAnimation(blinkAnimation)
            tvMinus.startAnimation(blinkAnimation)
            tvMinutes.startAnimation(blinkAnimation)
            tvSeconds.startAnimation(blinkAnimation)
            tvHours.startAnimation(blinkAnimation)
            tvMillis.startAnimation(blinkAnimation)
        } else {
            tvColon.clearAnimation()
            tvMinus.clearAnimation()
            tvMinutes.clearAnimation()
            tvSeconds.clearAnimation()
            tvHours.clearAnimation()
            tvMillis.clearAnimation()
        }
    }

    private fun updateText(totalMillis: Long, currentMillis: Long) {
        val remainingTime = totalMillis - currentMillis

        // progress bar
        if (totalMillis - currentMillis >= 0) {
            if (circularProgress.max != totalMillis.toInt())
                circularProgress.max = totalMillis.toInt()
            circularProgress.setProgress(currentMillis.toFloat(), true, 100)
        } else if (circularProgress.max != 1) {
            circularProgress.progress = 1f
            circularProgress.max = 1
        }

        // negative
        tvMinus.visibility = if (remainingTime < 0) View.VISIBLE else View.GONE

        // tenths of a second
        val tenth = Math.abs(remainingTime / 100 % 10)
        tvMillis.text = String.format(".%d", tenth)

        // seconds
        val sec = Math.abs(TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60)
        val sSec = String.format("%02d", sec)
        if (tvSeconds.text != sSec)
            tvSeconds.text = sSec

        // minus
        val min = Math.abs(TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60)
        val sMin = String.format("%02d", min)
        if (tvMinutes.text != sMin)
            tvMinutes.text = sMin

        // hours
        val h = Math.abs(TimeUnit.MILLISECONDS.toHours(remainingTime))
        if (h > 0) {
            val sH = h.toString() + context.getString(R.string.unit_hours)
            if (tvHours.text != sH)
                tvHours.text = sH
        } else if (tvHours.text != null) {
            tvHours.text = null
        }
    }

}
