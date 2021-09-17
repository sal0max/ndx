package de.salomax.ndx.ui.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.salomax.ndx.data.*
import de.salomax.ndx.data.model.ShutterSpeeds

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    private val prefDao: PrefDao = PrefDao.getInstance(application)

    internal val filterSortOrder: SharedPreferenceLiveData<Int> =
        prefDao.getFilterSortOrder()
    internal val evSteps: SharedPreferenceLiveData<ShutterSpeeds> =
        prefDao.getEvSteps()
    internal val showWarning: SharedPreferenceLiveData<Boolean> =
        prefDao.isWarningEnabled()
    internal val alarmBeepEnabled: SharedPreferenceLiveData<Boolean> =
        prefDao.shouldAlarmBeep()
    internal val alarmVibrateEnabled: SharedPreferenceLiveData<Boolean> =
        prefDao.shouldAlarmVibrate()
    internal val compensationDialEnabled: SharedPreferenceLiveData<Boolean> =
        prefDao.isCompensationDialEnabled()
    internal val hasPremium: SharedPreferenceLiveData<Boolean> =
        prefDao.hasPremium()
    internal val theme: SharedPreferenceLiveData<Int> =
        prefDao.getTheme()

    fun setEvSteps(evSteps: Int) {
        prefDao.setEvSteps(evSteps)
    }
    fun setWarning(enabled: Boolean) {
        prefDao.setWarning(enabled)
    }
    fun setAlarmBeep(enabled: Boolean) {
        prefDao.setAlarmBeep(enabled)
    }
    fun setAlarmVibrate(enabled: Boolean) {
        prefDao.setAlarmVibrate(enabled)
    }
    fun setCompensationDialEnabled(enabled: Boolean) {
        prefDao.setCompensationDialEnabled(enabled)
    }
    fun setFilterSortOrder(sortOrder: Int) {
        prefDao.setFilterSortOrder(sortOrder)
    }
    fun setTheme(theme: Int) {
        prefDao.setTheme(theme)
    }

}
