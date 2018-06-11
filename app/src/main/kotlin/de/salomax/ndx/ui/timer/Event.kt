package de.salomax.ndx.ui.timer

import com.joaquimverges.helium.event.ViewEvent

sealed class Event : ViewEvent {
    data class PopulateTimer(val millisTotal: Long, val millisOffset: Long) : Event()
    object RunTimer : Event()
    object PauseTimer : Event()

    object Alarm : Event()
    object Stop : Event()
}
