package de.salomax.ndx.ui.filterpouch

import com.joaquimverges.helium.state.ViewState
import de.salomax.ndx.data.Filter

sealed class State : ViewState {
    data class FiltersReady(val data: List<Filter>) : State()
    data class EditFilter(val data: Filter) : State()
    data class ShowUndoDeletionSnackbar(val filter: Filter) : State()
    object AddNewFilter : State()
}
