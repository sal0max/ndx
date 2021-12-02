package de.salomax.ndx.ui.preferences

import android.app.Application
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import de.salomax.ndx.R
import de.salomax.ndx.data.*
import de.salomax.ndx.data.model.ShutterSpeeds
import de.salomax.ndx.ui.calculator.CalculatorActivity

class PreferenceViewModel(private val app: Application) : AndroidViewModel(app) {

    private val prefDao: PrefDao = PrefDao.getInstance(app)

    internal val filterSortOrder: SharedPreferenceLiveData<Int> =
        prefDao.getFilterSortOrder()
    internal val filterGroupBySize: SharedPreferenceLiveData<Boolean> =
        prefDao.getFilterGroupBySize()
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
    fun setFilterGroupBySize(group: Boolean) {
        prefDao.setFilterGroupBySize(group)
    }
    fun setTheme(theme: Int) {
        prefDao.setTheme(theme)
        // switch theme
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                0 -> AppCompatDelegate.MODE_NIGHT_NO
                1 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
    fun setPitchBlackEnabled(enabled: Boolean) {
        prefDao.setPitchBlackEnabled(enabled)
        // switch theme
        app.setTheme(
            if (enabled)
                R.style.AppTheme_Black
            else
                R.style.AppTheme
        )
        // re-create all open activities, when we're in night mode
        if (isDarkThemeActive()) {
            TaskStackBuilder.create(app)
                // PreferencesActivity is always called from MainActivity
                .addNextIntent(Intent(app, CalculatorActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                })
                .addNextIntent(Intent(app, PreferenceActivity::class.java))
                .startActivities()
        }
    }

    private fun isDarkThemeActive(): Boolean {
        // app theme is dark
        val x = prefDao.getThemeSync() == 1
        // app theme is system default && current state is dark
        val y = prefDao.getThemeSync() == 2 && (app.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

        return x || y
    }

}
