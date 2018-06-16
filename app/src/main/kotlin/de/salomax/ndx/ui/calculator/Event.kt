package de.salomax.ndx.ui.calculator

import com.joaquimverges.helium.event.ViewEvent

sealed class Event : ViewEvent {
    data class StartTimer(val micro: Long) : Event()
}
