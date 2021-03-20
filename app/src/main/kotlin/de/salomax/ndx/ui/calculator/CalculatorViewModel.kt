package de.salomax.ndx.ui.calculator

import android.app.Application
import androidx.lifecycle.*
import de.salomax.ndx.data.*
import de.salomax.ndx.data.model.Compensation
import de.salomax.ndx.data.model.ShutterSpeeds
import de.salomax.ndx.util.MathUtils
import kotlin.math.pow

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val filterDao = NdxDatabase.getInstance(application).filterDao()
    private val prefDao = PrefDao.getInstance(application)

    // from Room
    private val filtersUnsorted: LiveData<List<Filter>?> = filterDao.getAll()

    // from SharedPrefs
    internal val speeds: SharedPreferenceLiveData<ShutterSpeeds> = prefDao.getEvSteps()
    internal val compensation: SharedPreferenceLiveData<Compensation> = prefDao.getCompensationSteps()
    internal val isWarningEnabled: SharedPreferenceLiveData<Boolean> = prefDao.isWarningEnabled()
    internal val isCompensationDialEnabled: SharedPreferenceLiveData<Boolean> = prefDao.isCompensationDialEnabled()
    internal val hasPremium: Boolean
        get() = prefDao.hasPremiumSync()
    private val filterSortOrder: LiveData<Int> = prefDao.getFilterSortOrder()

    // set by activity
    internal val selectedSpeed: MutableLiveData<Long> = MutableLiveData()
    internal val filterFactor: MutableLiveData<Long> = MutableLiveData() // 1
    internal val selectedOffset: MutableLiveData<Int> = MutableLiveData()

    // live calculated
    internal val filters: LiveData<List<Filter>?> = FilterLiveData()
    internal val calculatedSpeed: LiveData<Long> = MicroLiveData()

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
            addSource(selectedOffset) { calc() }
        }

        private fun calc() {
            var speed : Long?

            if (selectedOffset.value == null) {
                speed = selectedSpeed.value
            } else {
                // offset
                val selectedIndex = selectedSpeed.value?.let { speeds.value?.doubleValues?.indexOf(it) }
                val newIndex = selectedOffset.value?.let { selectedIndex?.plus(it) }
                speed = newIndex?.let { speeds.value?.doubleValues?.getOrNull(it) }

                // offset outside of enum data -> manually extrapolate
                if (speed == null && selectedOffset.value?.isLessThan(0) == true) {
                    val stopSize = when (compensation.value) {
                        Compensation.THIRD -> 0.3333333333333333333333333333333333333333333333333333333f
                        Compensation.HALF -> 0.5f
                        Compensation.FULL -> 1f
                        else -> 0F
                    }
                    val offset = selectedOffset.value?.times(stopSize)?.times(-1)
                    // compensatedSpeed = speed * 2^offset
                    speed = offset?.let { selectedSpeed.value?.times(2f.pow(it))?.toLong() }
                }
            }
            value = MathUtils.multiply(speed, filterFactor.value)
        }

        private fun Int?.isLessThan(other: Int?) =
              this != null && other != null && this < other
    }

}
