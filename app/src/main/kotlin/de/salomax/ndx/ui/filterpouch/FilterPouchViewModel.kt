package de.salomax.ndx.ui.filterpouch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.salomax.ndx.data.Filter
import de.salomax.ndx.data.FilterDao
import de.salomax.ndx.data.NdxDatabase
import java.util.concurrent.Executors

class FilterPouchViewModel(application: Application) : AndroidViewModel(application) {

    internal val filters: LiveData<List<Filter>?>
    private val filterDao: FilterDao

    init {
        val ndxDatabase = NdxDatabase.getInstance(application)
        filterDao = ndxDatabase.filterDao()

        filters = filterDao.getAll()
    }

    fun insert(filter: Filter) {
        Executors.newSingleThreadScheduledExecutor().execute { filterDao.insert(filter) }
    }

}
