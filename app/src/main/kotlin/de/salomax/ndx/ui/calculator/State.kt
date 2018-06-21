package de.salomax.ndx.ui.calculator

import com.joaquimverges.helium.state.ViewState
import de.salomax.ndx.data.Filter
import de.salomax.ndx.data.ShutterSpeeds

sealed class State : ViewState {
    data class ShutterSpeedsReady(val speeds: ShutterSpeeds) : State()
    data class FiltersReady(val filters: List<Filter>) : State()
    data class ShowWarning(val enabled: Boolean) : State()
}
