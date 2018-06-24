package de.salomax.ndx.ui.preferences

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.support.design.widget.Snackbar
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import de.salomax.ndx.R
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.Pref
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import de.salomax.ndx.ui.calculator.CalculatorActivity

class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private lateinit var evStepsPreference: ListPreference
    private lateinit var filterSortingPreference: ListPreference
    private lateinit var showWarningPreference: SwitchPreference
    private lateinit var alarmBeepPreference: SwitchPreference
    private lateinit var themeSelectorPreference: ListPreference
    private lateinit var alarmVibratePreference: SwitchPreference
    private lateinit var donatePreference: Preference
    private lateinit var aboutPreference: Preference
    private lateinit var changelogPreference: Preference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)

        // general
        evStepsPreference = findPreference(getString(R.string.prefKey_evSteps)) as ListPreference
        filterSortingPreference = findPreference(getString(R.string.prefKey_sortOrder)) as ListPreference
        showWarningPreference = findPreference(getString(R.string.prefKey_showWarning)) as SwitchPreference
        themeSelectorPreference = findPreference(getString(R.string.prefKey_themeSelector)) as ListPreference
        // timer
        alarmBeepPreference = findPreference(getString(R.string.prefKey_alarmBeep)) as SwitchPreference
        alarmVibratePreference = findPreference(getString(R.string.prefKey_alarmVibrate)) as SwitchPreference
        // about
        donatePreference = findPreference(getString(R.string.prefKey_donate))
        aboutPreference = findPreference(getString(R.string.prefKey_about))
        changelogPreference = findPreference(getString(R.string.prefKey_changelog))

        // ev steps
        evStepsPreference.onPreferenceChangeListener = this
        // sortOrder of the filters: by factor or by name
        filterSortingPreference.onPreferenceChangeListener = this
        // shows a little warning label, if calculated time is too large
        showWarningPreference.onPreferenceClickListener = this
        // theme switcher
        themeSelectorPreference.onPreferenceChangeListener = this

        // alarm preferences when the timer is up
        alarmBeepPreference.onPreferenceClickListener = this
        alarmVibratePreference.onPreferenceClickListener = this
        // check whether device can vibrate
        alarmVibratePreference.isVisible = (context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).hasVibrator()

        // beg for donations
        donatePreference.onPreferenceClickListener = this
        // show changelog
        changelogPreference.onPreferenceClickListener = this
        // show correct version name & copyright year
        try {
            val version = activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName
            aboutPreference.title = getString(R.string.prefTitle_about, version)
            aboutPreference.summary = getString(R.string.prefSummary_about, Calendar.getInstance().get(Calendar.YEAR))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        // populate settings with values from db
        Single.fromCallable {
            NdxDatabase.getInstance(context!!)
                    .prefDao()
                    .getAll()
                    .subscribe {
                        for (pref in it) {
                            when (pref.key) {
                                Pref.FILTER_SORT_ORDER -> filterSortingPreference.value = pref.value
                                Pref.EV_STEPS -> evStepsPreference.value = pref.value
                                Pref.SHOW_WARNING -> showWarningPreference.isChecked = pref.value == "1"
                                Pref.ALARM_BEEP -> alarmBeepPreference.isChecked = pref.value == "1"
                                Pref.ALARM_VIBRATE -> alarmVibratePreference.isChecked = pref.value == "1"
                            }
                        }
                    }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        // theme setting is stored in shared prefs, not db
        themeSelectorPreference.value = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(Pref.THEME, resources.getStringArray(R.array.prefValues_themes)[0])
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference) {
            evStepsPreference -> {
                addToDb(Pref.EV_STEPS, newValue as String)
                return true
            }
            filterSortingPreference -> {
                addToDb(Pref.FILTER_SORT_ORDER, newValue as String)
                return true
            }
            themeSelectorPreference -> {
                // needs to be in shared prefs; not possible to store to room as it doesn't allow
                // blocking access, which would be needed in order to be retrieved before onCreate()
                val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                mPrefs.edit().putString(Pref.THEME, newValue as String).apply()
                // re-create all open activities
                TaskStackBuilder.create(context!!)
                        .addNextIntent(Intent(activity, CalculatorActivity::class.java))
                        .addNextIntent(activity!!.intent)
                        .startActivities()
                return true
            }
        }
        return false
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference) {
            donatePreference -> {
                Snackbar.make(view!!, "TODO: open In-App Purchase-Dialog", Snackbar.LENGTH_SHORT).show()
            }
            changelogPreference -> {
                val fragment = ChangelogDialog()
                fragment.show(childFragmentManager, null)
            }
            showWarningPreference -> {
                addToDb(Pref.SHOW_WARNING, if ((preference as SwitchPreference).isChecked) "1" else "0")
                return true
            }
            alarmBeepPreference -> {
                addToDb(Pref.ALARM_BEEP, if ((preference as SwitchPreference).isChecked) "1" else "0")
                return true
            }
            alarmVibratePreference -> {
                addToDb(Pref.ALARM_VIBRATE, if ((preference as SwitchPreference).isChecked) "1" else "0")
                return true
            }
        }
        return false
    }

    private fun addToDb(key: String, value: String) {
        Single.fromCallable { NdxDatabase.getInstance(context!!).prefDao().insert(Pref(key, value)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

}
