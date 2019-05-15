package de.salomax.ndx.ui.timer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.Pref
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    internal val prefs: LiveData<List<Pref>?>

    // current state
    internal var millisTotal: Long? = null
    internal var millisCurrent: MutableLiveData<Long> = MutableLiveData()
    internal var state: MutableLiveData<State> = MutableLiveData()

    //
    private var executor = Executors.newSingleThreadScheduledExecutor()

    init {
        // db
        val ndxDatabase = NdxDatabase.getInstance(application)
        val prefDao = ndxDatabase.prefDao()
        prefs = prefDao.getTimerAlarms()

        millisCurrent.value = 0L
    }

    internal fun startTimer() {
        executor?.shutdownNow()
        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleAtFixedRate({
            millisCurrent.postValue(millisCurrent.value!! + 100)
            state.postValue(
                    if (millisCurrent.value!! < millisTotal!!)
                        State.RUNNING_POSITIVE
                    else if (millisCurrent.value!! == millisTotal!!)
                        State.FINISHED
                    else
                        State.RUNNING_NEGATIVE)
        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    internal fun stopTimer() {
        state.value = State.PAUSED
        executor?.shutdownNow()
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
