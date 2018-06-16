package de.salomax.ndx.ui.timer

import com.joaquimverges.helium.event.ViewEvent

sealed class Event : ViewEvent {
    data class PopulateTimer(val millisTotal: Long, val millisCurrent: Long) : Event()
    object RunCountdown : Event()
    object PauseCountdown : Event()
    object ResetTimer : Event()

    object Finish : Event()
}
