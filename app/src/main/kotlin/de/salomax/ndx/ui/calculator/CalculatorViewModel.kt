package de.salomax.ndx.ui.calculator

import android.app.Application
import androidx.lifecycle.*
import de.salomax.ndx.data.Filter
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.ShutterSpeeds
import de.salomax.ndx.util.MathUtils

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    // from Room
    internal val filters: LiveData<List<Filter>?>
    internal val speeds: LiveData<ShutterSpeeds?>
    internal val isWarningEnabled: LiveData<Boolean?>

    // live calculated
    internal val calculatedSpeed: LiveData<Long>
    internal val selectedSpeed: MutableLiveData<Long> = MutableLiveData()
    internal val filterFactor: MutableLiveData<Long> = MutableLiveData() // 1

    init {
        val ndxDatabase = NdxDatabase.getInstance(application)
        val filterDao = ndxDatabase.filterDao()
        val prefDao = ndxDatabase.prefDao()

        filters = filterDao.getAll()
        speeds = prefDao.getEvSteps()
        isWarningEnabled = prefDao.isWarningEnabled()

        calculatedSpeed = MicroLiveData()
    }

    internal inner class MicroLiveData : MediatorLiveData<Long>() {
        init {
            addSource(selectedSpeed) {
                value = MathUtils.multiply(it, filterFactor.value)
            }
            addSource(filterFactor) {
                value = MathUtils.multiply(it, selectedSpeed.value)
            }
        }
    }

}
