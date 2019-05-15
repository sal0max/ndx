package de.salomax.ndx.ui.filtereditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.salomax.ndx.data.Filter
import de.salomax.ndx.data.FilterDao
import de.salomax.ndx.data.NdxDatabase
import java.util.concurrent.Executors

class FilterEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val filterDao: FilterDao

    init {
        val ndxDatabase = NdxDatabase.getInstance(application)
        filterDao = ndxDatabase.filterDao()
    }

    fun insert(filter: Filter) {
        Executors.newSingleThreadScheduledExecutor().execute { filterDao.insert(filter) }
    }

    fun delete(filter: Filter) {
        Executors.newSingleThreadScheduledExecutor().execute { filterDao.delete(filter.id!!) }
    }

}
