package de.salomax.ndx.ui.timer

import com.joaquimverges.helium.presenter.BasePresenter

class Presenter : BasePresenter<State, Event>() {

    override fun onViewEvent(event: Event) {
        when (event) {
            is Event.PopulateTimer ->
                pushState(State.PopulateTimer(event.millisTotal, event.millisOffset))
            is Event.RunTimer ->
                pushState(State.RunTimer)
            is Event.PauseTimer ->
                pushState(State.PauseTimer)
            is Event.Alarm ->
                pushState(State.Alarm)
            is Event.Finish ->
                pushState(State.Finish)
        }
    }

}
