package de.salomax.ndx.ui.timer

import com.joaquimverges.helium.core.state.ViewState

sealed class State : ViewState {
    data class InitOrReset(val millisTotal: Long, val millisOffset: Long) : State()
    data class CountdownRunning(val millisTotal: Long, val millisOffset: Long) : State()
    data class CountdownPaused(val millisTotal: Long, val millisOffset: Long) : State()
    data class CountdownFinished(val millisTotal: Long, val millisOffset: Long) : State()

    object Finish : State()
}
