package de.salomax.ndx.ui.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import de.salomax.ndx.data.*
import de.salomax.ndx.data.model.IsoSteps
import de.salomax.ndx.data.model.ShutterSpeeds
import de.salomax.ndx.util.MathUtils
import java.security.acl.Owner

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    // from Room
    private val filtersUnsorted: LiveData<List<Filter>?>

    // from SharedPrefs
    internal val speeds: SharedPreferenceLiveData<ShutterSpeeds>
    internal val isWarningEnabled: SharedPreferenceLiveData<Boolean>
    internal val hasPremium: SharedPreferenceLiveData<Boolean>
    private val filterSortOrder: LiveData<Int>

    // live calculated
    internal val filters: LiveData<List<Filter>?>
    internal val calculatedSpeed: LiveData<Long>
    internal val selectedSpeed: MutableLiveData<Long> = MutableLiveData()
    internal val filterFactor: MutableLiveData<Long> = MutableLiveData() // 1

    init {
        val filterDao = NdxDatabase.getInstance(application).filterDao()
        val prefDao = PrefDao.getInstance(application)

        filtersUnsorted = filterDao.getAll()

        speeds = prefDao.getEvSteps()
        isWarningEnabled = prefDao.isWarningEnabled()
        hasPremium = prefDao.hasPremium()
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
