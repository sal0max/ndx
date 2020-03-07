package de.salomax.ndx.ui.preferences

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import com.google.android.material.snackbar.Snackbar
import de.salomax.ndx.BuildConfig
import de.salomax.ndx.data.Pref
import de.salomax.ndx.R
import de.salomax.ndx.ui.calculator.CalculatorActivity
import java.util.*

class PreferenceFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private lateinit var viewModel: PreferenceViewModel

    private lateinit var evStepsPreference: ListPreference
    private lateinit var filterSortingPreference: ListPreference
    private lateinit var showWarningPreference: SwitchPreference
    private lateinit var themeSelectorPreference: ListPreference

    private lateinit var alarmBeepPreference: SwitchPreference
    private lateinit var alarmVibratePreference: SwitchPreference

    private lateinit var donatePreference: Preference
    private lateinit var aboutPreference: Preference

    private lateinit var mailPreference: Preference
    private lateinit var ratePreference: Preference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        viewModel = ViewModelProvider(this).get(PreferenceViewModel::class.java)

        /*
         * find prefs
         */
        // general
        evStepsPreference = findPreference(getString(R.string.prefKey_evSteps))!!
        filterSortingPreference = findPreference(getString(R.string.prefKey_sortOrder))!!
        showWarningPreference = findPreference(getString(R.string.prefKey_showWarning))!!
        themeSelectorPreference = findPreference(getString(R.string.prefKey_themeSelector))!!
        // timer
        alarmBeepPreference = findPreference(getString(R.string.prefKey_alarmBeep))!!
        alarmVibratePreference = findPreference(getString(R.string.prefKey_alarmVibrate))!!
        // about
        donatePreference = findPreference(getString(R.string.prefKey_donate))!!
        aboutPreference = findPreference(getString(R.string.prefKey_about))!!
        // feedback
        mailPreference = findPreference(getString(R.string.prefKey_mail))!!
        ratePreference = findPreference(getString(R.string.prefKey_rate))!!

        /*
         * set listeners & content
         */
        // general
        evStepsPreference.onPreferenceChangeListener = this
        filterSortingPreference.onPreferenceChangeListener = this
        showWarningPreference.onPreferenceClickListener = this
        themeSelectorPreference.onPreferenceChangeListener = this
        // timer
        alarmBeepPreference.onPreferenceClickListener = this
        alarmVibratePreference.onPreferenceClickListener = this
        alarmVibratePreference.isVisible = (context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).hasVibrator()
        // about
        donatePreference.onPreferenceClickListener = this
        aboutPreference.onPreferenceClickListener = this
        aboutPreference.title = getString(R.string.prefTitle_about, BuildConfig.VERSION_NAME)
        aboutPreference.summary = getString(R.string.prefSummary_about, Calendar.getInstance().get(Calendar.YEAR))
        // feedback
        mailPreference.onPreferenceClickListener = this
        ratePreference.onPreferenceClickListener = this

        /*
         * populate settings with values from db
         */
        viewModel.prefs.observe(this, Observer<List<Pref>> {
            for (pref in it) {
                when (pref.key) {
                    Pref.FILTER_SORT_ORDER -> filterSortingPreference.value = pref.value
                    Pref.EV_STEPS -> evStepsPreference.value = pref.value
                    Pref.SHOW_WARNING -> showWarningPreference.isChecked = pref.value == "1"
                    Pref.ALARM_BEEP -> alarmBeepPreference.isChecked = pref.value == "1"
                    Pref.ALARM_VIBRATE -> alarmVibratePreference.isChecked = pref.value == "1"
                }
            }
        })
        // theme setting is stored in shared prefs, not db
        themeSelectorPreference.value = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(Pref.THEME, resources.getStringArray(R.array.prefValues_themes)[0])
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference) {
            evStepsPreference -> {
                addToDb(Pref.EV_STEPS, newValue as String)
            }
            filterSortingPreference -> {
                addToDb(Pref.FILTER_SORT_ORDER, newValue as String)
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
            }
            else -> return false
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference) {
            donatePreference -> {
                Snackbar.make(view!!, "TODO: open In-App Purchase-Dialog", Snackbar.LENGTH_SHORT).show()
            }
            aboutPreference -> {
                val fragment = ChangelogDialog()
                fragment.show(childFragmentManager, null)
            }
            showWarningPreference -> {
                addToDb(Pref.SHOW_WARNING, if ((preference as SwitchPreference).isChecked) "1" else "0")
            }
            alarmBeepPreference -> {
                addToDb(Pref.ALARM_BEEP, if ((preference as SwitchPreference).isChecked) "1" else "0")
            }
            alarmVibratePreference -> {
                addToDb(Pref.ALARM_VIBRATE, if ((preference as SwitchPreference).isChecked) "1" else "0")
            }
            mailPreference -> {
                val mailIntent = Intent(Intent.ACTION_SENDTO)
                mailIntent.data = Uri.parse("mailto:") // only email apps should handle this
                mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.prefValue_mail_address)))
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.prefValue_mail_subject))
                val device = Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ")"
                val osVersion = Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT.toString() + ")"
                val appVersion = BuildConfig.VERSION_NAME
                val info = """
                    Device=$device
                    OS Version=$osVersion
                    App Version=$appVersion
                    ---


                    """.trimIndent()
                mailIntent.putExtra(Intent.EXTRA_TEXT, info)
                if (mailIntent.resolveActivity(context!!.packageManager) != null)
                    startActivity(mailIntent)
                else // no mail client available
                    showError(getString(R.string.prefError_mail))

            }
            ratePreference -> {
                val rateIntent = Intent(Intent.ACTION_VIEW)
                rateIntent.data = Uri.parse("market://details?id=de.salomax.ndx")
                if (rateIntent.resolveActivity(context!!.packageManager) != null)
                    startActivity(rateIntent)
                else // play store not installed
                    showError(getString(R.string.prefError_rate))

            }
            else -> return false
        }
        return true
    }

    private fun addToDb(key: String, value: String) {
        viewModel.insert(Pref(key, value))
    }

    private fun showError(errorMsg: String) {
        val snackbar = Snackbar.make(view!!, errorMsg, Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.holo_red_light))
        snackbar.show()
    }

}
