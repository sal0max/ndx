package de.salomax.ndx.ui.preferences

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.support.design.widget.Snackbar
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import de.salomax.ndx.R
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.Pref
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private lateinit var evStepsPreference: ListPreference
    private lateinit var filterSortingPreference: ListPreference
    private lateinit var alarmBeepPreference: CheckBoxPreference
    private lateinit var alarmVibratePreference: CheckBoxPreference
    private lateinit var donatePreference: Preference
    private lateinit var aboutPreference: Preference
    private lateinit var changelogPreference: Preference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)

        // general
        evStepsPreference = findPreference(getString(R.string.prefKey_evSteps)) as ListPreference
        filterSortingPreference = findPreference(getString(R.string.prefKey_sortOrder)) as ListPreference
        // timer
        alarmBeepPreference = findPreference(getString(R.string.prefKey_alarmBeep)) as CheckBoxPreference
        alarmVibratePreference = findPreference(getString(R.string.prefKey_alarmVibrate)) as CheckBoxPreference
        // about
        donatePreference = findPreference(getString(R.string.prefKey_donate))
        aboutPreference = findPreference(getString(R.string.prefKey_about))
        changelogPreference = findPreference(getString(R.string.prefKey_changelog))

        // ev steps
        evStepsPreference.onPreferenceChangeListener = this
        // sortOrder of the filters: by factor or by name
        filterSortingPreference.onPreferenceChangeListener = this

        // alarm preferences when the timer is up
        alarmBeepPreference.onPreferenceClickListener = this
        alarmVibratePreference.onPreferenceClickListener = this
        // check whether device can vibrate
        alarmVibratePreference.isEnabled = (context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).hasVibrator()

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
                                Pref.ALARM_BEEP -> alarmBeepPreference.isChecked = pref.value == "1"
                                Pref.ALARM_VIBRATE -> alarmVibratePreference.isChecked = pref.value == "1"
                            }
                        }
                    }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
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
            alarmBeepPreference -> {
                addToDb(Pref.ALARM_BEEP, if ((preference as CheckBoxPreference).isChecked) "1" else "0")
                return true
            }
            alarmVibratePreference -> {
                addToDb(Pref.ALARM_VIBRATE, if ((preference as CheckBoxPreference).isChecked) "1" else "0")
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
