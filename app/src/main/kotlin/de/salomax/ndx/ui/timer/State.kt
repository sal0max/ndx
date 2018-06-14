package de.salomax.ndx.ui.timer

import com.joaquimverges.helium.state.ViewState

sealed class State : ViewState {
    object Init : State()
    object TimerRunning : State()
    object TimerPaused : State()

    data class UpdateText(val millisTotal: Long, val millisOffset: Long) : State()

    object Alarm : State()
    object Finish : State()
}
