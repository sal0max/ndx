package de.salomax.ndx.ui.filtereditor

import com.joaquimverges.helium.core.event.ViewEvent
import de.salomax.ndx.data.Filter

sealed class Event : ViewEvent {
    data class InsertOrUpdate(val filter: Filter) : Event()
    data class Delete(val id: Long) : Event()
    data class Calibrated(val factor: Int) : Event()
    object Cancel : Event()
}
