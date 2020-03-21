package de.salomax.ndx.ui.filterpouch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import de.salomax.ndx.data.Filter
import de.salomax.ndx.data.FilterDao
import de.salomax.ndx.data.NdxDatabase
import java.util.concurrent.Executors

class FilterPouchViewModel(application: Application) : AndroidViewModel(application) {

    private val filterDao: FilterDao

    internal val filters: LiveData<List<Filter>?>
    internal var hasPremium: Boolean = false
        private set

    private val hasPremiumLive: LiveData<Boolean?>
    private val premiumObserver = Observer<Boolean?> { hasPremium = it ?: false }

    init {
        val ndxDatabase = NdxDatabase.getInstance(application)
        filterDao = ndxDatabase.filterDao()
        filters = filterDao.getAll()

        val prefDao = ndxDatabase.prefDao()
        hasPremiumLive = prefDao.hasPremium()
        hasPremiumLive.observeForever { premiumObserver }
    }

    override fun onCleared() {
        super.onCleared()
        hasPremiumLive.removeObserver(premiumObserver)
    }

    fun insert(filter: Filter) {
        Executors.newSingleThreadScheduledExecutor().execute { filterDao.insert(filter) }
    }

}
