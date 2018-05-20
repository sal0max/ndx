package de.salomax.ndx.ui.preferences

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import de.salomax.ndx.R
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.Pref

import java.util.Calendar

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private lateinit var evStepsPreference: ListPreference
    private lateinit var filterSortingPreference: ListPreference
    private lateinit var donatePreference: Preference
    private lateinit var aboutPreference: Preference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)

        evStepsPreference = findPreference(getString(R.string.prefKey_evSteps)) as ListPreference
        filterSortingPreference = findPreference(getString(R.string.prefKey_sortOrder)) as ListPreference
        donatePreference = findPreference(getString(R.string.prefKey_donate))
        aboutPreference = findPreference(getString(R.string.prefKey_about))

        // ev steps
        evStepsPreference.onPreferenceChangeListener = this
        // sortOrder of the filters: by factor or by name
        filterSortingPreference.onPreferenceChangeListener = this
        // beg for donations
        donatePreference.onPreferenceClickListener = this
        // show correct version name & copyright year
        try {
            val version = activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName
            aboutPreference.title = getString(R.string.prefTitle_about, version)
            aboutPreference.summary = getString(R.string.prefSummary_about, Calendar.getInstance().get(Calendar.YEAR))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference) {
            evStepsPreference -> {
                Single.fromCallable { NdxDatabase.getInstance(context!!).prefDao().insert(Pref("EV_STEPS", newValue as String)) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                return true
            }
            filterSortingPreference -> {
                Single.fromCallable { NdxDatabase.getInstance(context!!).prefDao().insert(Pref("FILTER_SORT_ORDER", newValue as String)) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
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
        }
        return false
    }

}
