package de.salomax.ndx.ui.timer

import com.joaquimverges.helium.presenter.BasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Presenter : BasePresenter<State, Event>() {

    // current state
    companion object {
        private var intervalSubscription: Disposable? = null
        private var millisTotal = 0L
        private var millisCurrent = 0L
    }

    override fun onViewEvent(event: Event) {
        when (event) {
            is Event.PopulateTimer -> {
                // init state (only if timer isn't running or paused)
                if (intervalSubscription == null) {
                    millisTotal = event.millisTotal
                    millisCurrent = event.millisCurrent
                    pushState(State.InitOrReset(millisTotal, millisCurrent))
                }
            }
            is Event.RunCountdown -> {
                intervalSubscription = Observable.interval(10, TimeUnit.MILLISECONDS)
                        .map { 10L }
                        .subscribeOn(Schedulers.io()) // Run on a background thread
                        .observeOn(AndroidSchedulers.mainThread()) // Be notified on the main thread
                        .subscribe {
                            if (millisTotal > millisCurrent)
                                pushState(State.CountdownRunning(millisTotal, millisCurrent))
                            else
                                pushState(State.CountdownFinished(millisTotal, millisCurrent))
                            millisCurrent += it
                        }
            }
            is Event.PauseCountdown -> {
                intervalSubscription?.dispose()
                pushState(State.CountdownPaused(millisTotal, millisCurrent))
            }
            is Event.ResetTimer -> {
                intervalSubscription?.dispose()
                millisCurrent = 0L
                pushState(State.InitOrReset(millisTotal, millisCurrent))
            }
            is Event.Finish -> {
                intervalSubscription?.dispose()
                intervalSubscription = null
                pushState(State.Finish)
            }
        }
    }

}
