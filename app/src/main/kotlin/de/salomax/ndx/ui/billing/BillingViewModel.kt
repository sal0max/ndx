package de.salomax.ndx.ui.billing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.salomax.ndx.data.PrefDao

class BillingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefDao = PrefDao.getInstance(application)

    fun enablePremium() {
        prefDao.enablePremium(true)
    }

}
