package de.salomax.ndx.ui.calibrator

import com.joaquimverges.helium.core.state.ViewState
import de.salomax.ndx.data.ISOs

sealed class State : ViewState {
    data class ISOsReady(val isos: ISOs) : State()
    //data class Calibrated(val factor: Int) : State()
    object ShowManual : State()
    object Finish : State()
}
