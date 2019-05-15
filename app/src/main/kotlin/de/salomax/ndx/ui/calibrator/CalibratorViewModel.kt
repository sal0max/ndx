package de.salomax.ndx.ui.calibrator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.salomax.ndx.data.IsoSteps
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.ShutterSpeeds

class CalibratorViewModel(application: Application) : AndroidViewModel(application) {

    internal val speeds: LiveData<ShutterSpeeds?>
    internal val isoSteps: LiveData<IsoSteps?>

    init {
        val ndxDatabase = NdxDatabase.getInstance(application)
        val prefDao = ndxDatabase.prefDao()
        speeds = prefDao.getEvSteps()
        isoSteps = prefDao.getIsoSteps()
    }

}
