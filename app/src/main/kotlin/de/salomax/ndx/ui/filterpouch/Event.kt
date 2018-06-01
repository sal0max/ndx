package de.salomax.ndx.ui.filterpouch

import com.joaquimverges.helium.event.ViewEvent
import de.salomax.ndx.data.Filter

sealed class Event : ViewEvent {
    data class FilterClicked(val filter: Filter) : Event()
    data class ReceivedDeletionResult(val filter: Filter) : Event()
    data class RestoreFilter(val filter: Filter) : Event()
    object AddNewClicked : Event()
}
