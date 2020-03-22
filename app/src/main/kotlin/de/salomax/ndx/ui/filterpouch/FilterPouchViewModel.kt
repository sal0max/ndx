package de.salomax.ndx.ui.filterpouch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.salomax.ndx.data.*
import java.util.concurrent.Executors

class FilterPouchViewModel(application: Application) : AndroidViewModel(application) {

   private val filterDao: FilterDao = NdxDatabase.getInstance(application).filterDao()
   private val prefDao = PrefDao.getInstance(application)

   // from Room
   private val filtersUnsorted: LiveData<List<Filter>?>

   // from SharedPrefs
   internal val hasPremium: LiveData<Boolean>
   private val filterSortOrder: LiveData<Int>

   // live calculated
   internal val filters: LiveData<List<Filter>?>

   init {
      filtersUnsorted = filterDao.getAll()

      hasPremium = prefDao.hasPremium()
      filterSortOrder = prefDao.getFilterSortOrder()

      filters = FilterLiveData()
   }

   fun insert(filter: Filter) {
      Executors.newSingleThreadScheduledExecutor().execute { filterDao.insert(filter) }
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

}
