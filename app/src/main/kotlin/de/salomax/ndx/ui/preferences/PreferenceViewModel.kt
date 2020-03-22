package de.salomax.ndx.ui.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.salomax.ndx.data.*
import de.salomax.ndx.data.model.ShutterSpeeds
import java.util.concurrent.Executors

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    private val prefDao: PrefDao = PrefDao.getInstance(application)

    internal val filterSortOrder: SharedPreferenceLiveData<Int>
    internal val evSteps: SharedPreferenceLiveData<ShutterSpeeds>
    internal val showWarning: SharedPreferenceLiveData<Boolean>
    internal val alarmBeepEnabled: SharedPreferenceLiveData<Boolean>
    internal val alarmVibrateEnabled: SharedPreferenceLiveData<Boolean>
    internal val hasPremium: SharedPreferenceLiveData<Boolean>
    internal val theme: SharedPreferenceLiveData<Int>

    init {
        filterSortOrder = prefDao.getFilterSortOrder()
        evSteps = prefDao.getEvSteps()
        showWarning = prefDao.isWarningEnabled()
        alarmBeepEnabled = prefDao.shouldAlarmBeep()
        alarmVibrateEnabled = prefDao.shouldAlarmVibrate()
        hasPremium = prefDao.hasPremium()
        theme = prefDao.getTheme()
    }

    fun setEvSteps(evSteps: Int) { prefDao.setEvSteps(evSteps) }
    fun setWarning(enabled: Boolean) { prefDao.setWarning(enabled) }
    fun setAlarmBeep(enabled: Boolean) { prefDao.setAlarmBeep(enabled) }
    fun setAlarmVibrate(enabled: Boolean) { prefDao.setAlarmVibrate(enabled) }
    fun setFilterSortOrder(sortOrder: Int) {prefDao.setFilterSortOrder(sortOrder)}
    fun setTheme(theme: Int) {prefDao.setTheme(theme)}

}
