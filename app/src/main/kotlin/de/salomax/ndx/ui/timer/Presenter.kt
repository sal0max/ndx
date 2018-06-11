package de.salomax.ndx.ui.timer

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.joaquimverges.helium.presenter.BasePresenter

class Presenter : BasePresenter<State, Event>() {

    //TODO remove
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun start() {
        pushState(State.PopulateTimer(10_000 * 2 * 60, 0))
    }

    override fun onViewEvent(event: Event) {
        when (event) {
            is Event.PopulateTimer ->
                pushState(State.PopulateTimer(event.millisTotal, event.millisOffset))
            is Event.RunTimer ->
                pushState(State.RunTimer)
            is Event.PauseTimer ->
                pushState(State.PauseTimer)
            is Event.Stop ->
                pushState(State.Finish)
        }
    }

}
