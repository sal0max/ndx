package de.salomax.ndx.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import de.salomax.ndx.data.PrefDao
import de.salomax.ndx.data.SharedPreferenceLiveData
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val prefDao: PrefDao = PrefDao.getInstance(application)

    internal fun shouldAlarmVibrate(): Boolean {
        return prefDao.shouldAlarmVibrateSync()
    }

    internal fun shouldAlarmBeep(): Boolean {
        return prefDao.shouldAlarmBeepSync()
    }

    // current state
    internal var millisTotal: Long? = null
    internal var millisCurrent: MutableLiveData<Long> = MutableLiveData()
    internal var state: MutableLiveData<State> = MutableLiveData()

    //
    private var executor = Executors.newSingleThreadScheduledExecutor()

    init {
        millisCurrent.value = 0L
    }

    internal fun startTimer() {
        executor.shutdownNow()
        executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate({
            millisCurrent.postValue(millisCurrent.value!! + 100)
            state.postValue(
                  when {
                     millisCurrent.value!! < millisTotal!! -> State.RUNNING_POSITIVE
                     millisCurrent.value!! == millisTotal!! -> State.FINISHED
                     else -> State.RUNNING_NEGATIVE
                  })
        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    internal fun stopTimer() {
        state.value = State.PAUSED
        executor.shutdownNow()
    }

    enum class State {
        RUNNING_POSITIVE,
        RUNNING_NEGATIVE,
        FINISHED,
        PAUSED
    }

    override fun onCleared() {
        executor.shutdownNow()
    }
}
