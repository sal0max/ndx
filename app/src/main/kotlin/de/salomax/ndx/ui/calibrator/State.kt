package de.salomax.ndx.ui.calibrator

import com.joaquimverges.helium.core.state.ViewState
import de.salomax.ndx.data.Filter

sealed class State : ViewState {
    // data class Finish(val filter: Filter) : State()
    object ToggleManual : State()
}
