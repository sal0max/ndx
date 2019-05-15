package de.salomax.ndx.ui.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.salomax.ndx.data.*
import java.util.concurrent.Executors

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    internal val prefs: LiveData<List<Pref>>

    private val prefDao: PrefDao

    init {
        val ndxDatabase = NdxDatabase.getInstance(application)
        prefDao = ndxDatabase.prefDao()
        prefs = prefDao.getAll()
    }

    fun insert(pref: Pref) {
        Executors.newSingleThreadScheduledExecutor().execute { prefDao.insert(pref) }
    }

}