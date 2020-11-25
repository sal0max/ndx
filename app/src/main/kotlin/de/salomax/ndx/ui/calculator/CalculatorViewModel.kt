package de.salomax.ndx.ui.calculator

import android.app.Application
import androidx.lifecycle.*
import de.salomax.ndx.data.*
import de.salomax.ndx.data.model.ShutterSpeeds
import de.salomax.ndx.util.MathUtils

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val filterDao = NdxDatabase.getInstance(application).filterDao()
    private val prefDao = PrefDao.getInstance(application)

    // from Room
    private val filtersUnsorted: LiveData<List<Filter>?>

    // from SharedPrefs
    internal val speeds: SharedPreferenceLiveData<ShutterSpeeds>
    internal val isWarningEnabled: SharedPreferenceLiveData<Boolean>
    internal val hasPremium: Boolean
        get() = prefDao.hasPremiumSync()
    private val filterSortOrder: LiveData<Int>

    // live calculated
    internal val filters: LiveData<List<Filter>?>
    internal val calculatedSpeed: LiveData<Long>
    internal val selectedSpeed: MutableLiveData<Long> = MutableLiveData()
    internal val filterFactor: MutableLiveData<Long> = MutableLiveData() // 1

    init {

        filtersUnsorted = filterDao.getAll()

        speeds = prefDao.getEvSteps()
        isWarningEnabled = prefDao.isWarningEnabled()
        filterSortOrder = prefDao.getFilterSortOrder()

        filters = FilterLiveData()
        calculatedSpeed = MicroLiveData()
    }

    internal inner class FilterLiveData : MediatorLiveData<List<Filter>?>() {
        init {
            addSource(filtersUnsorted) { calc() }
            addSource(filterSortOrder) { calc() }
        }

        private fun calc() {
            value = filtersUnsorted.value?.sortedWith(
                  when (filterSortOrder.value) {
                      0 -> compareBy { it.factor }
                      else -> compareBy { it.name } // 1
                  }
            )
        }
    }

    internal inner class MicroLiveData : MediatorLiveData<Long>() {
        init {
            addSource(selectedSpeed) { calc() }
            addSource(filterFactor) { calc() }
        }

        private fun calc() {
            value = MathUtils.multiply(selectedSpeed.value, filterFactor.value)
        }
    }

}
