package de.salomax.ndx.ui.timer

import com.joaquimverges.helium.state.ViewState

sealed class State : ViewState {
    data class PopulateTimer(val millisTotal: Long, val millisOffset: Long) : State()
    object RunTimer : State()
    object PauseTimer : State()

    object Alarm : State()
    object Finish : State()
}
