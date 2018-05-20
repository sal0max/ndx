package de.salomax.ndx.ui.filtereditor

import com.joaquimverges.helium.state.ViewState
import de.salomax.ndx.data.Filter

sealed class State : ViewState {
    data class Prepopulate(val data: Filter) : State()
    object Finish : State()
    object DeleteAndFinish : State()
    object InsertOrUpdate : State()
}
