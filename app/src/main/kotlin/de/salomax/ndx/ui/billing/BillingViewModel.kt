package de.salomax.ndx.ui.billing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.salomax.ndx.data.*
import java.util.concurrent.Executors

class BillingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefDao: PrefDao

    init {
        val ndxDatabase = NdxDatabase.getInstance(application)
        prefDao = ndxDatabase.prefDao()
    }

    fun enablePremium() {
//        Executors.newSingleThreadScheduledExecutor().execute { prefDao.enablePremium() }
        Executors.newSingleThreadScheduledExecutor().execute {
            prefDao.insert(Pref(Pref.HAS_PREMIUM, "1"))
        }
    }

}
