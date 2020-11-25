package de.salomax.ndx.ui.calibrator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.salomax.ndx.data.model.IsoSteps
import de.salomax.ndx.data.PrefDao
import de.salomax.ndx.data.SharedPreferenceLiveData
import de.salomax.ndx.data.model.ShutterSpeeds

class CalibratorViewModel(application: Application) : AndroidViewModel(application) {

    internal val speeds: SharedPreferenceLiveData<ShutterSpeeds>
    internal val isoSteps: SharedPreferenceLiveData<IsoSteps>

    init {
        val prefDao = PrefDao.getInstance(application)
        speeds = prefDao.getEvSteps()
        isoSteps = prefDao.getIsoSteps()
    }

}
