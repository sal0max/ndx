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
import de.salomax.ndx.R
import de.salomax.ndx.data.model.ShutterSpeeds
import de.salomax.ndx.ui.billing.BillingActivity
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
         * populate settings with values from prefs
         */
        viewModel.filterSortOrder.observe(this, Observer { filterSortingPreference.value = it.toString() })
        viewModel.evSteps.observe(this, Observer { speeds ->
            evStepsPreference.value = when (speeds) {
                ShutterSpeeds.FULL -> "1"
                ShutterSpeeds.HALF -> "2"
                else -> "3"
            }
        })
        viewModel.showWarning.observe(this, Observer { showWarningPreference.isChecked = it })
        viewModel.alarmBeepEnabled.observe(this, Observer { alarmBeepPreference.isChecked = it })
        viewModel.alarmVibrateEnabled.observe(this, Observer { alarmVibratePreference.isChecked = it })
        viewModel.hasPremium.observe(this, Observer {
            if (it) {
                donatePreference.summary = getString(R.string.prefSummary_has_donated)
                donatePreference.isSelectable = false
            } else {
                donatePreference.summary = getString(R.string.prefSummary_donate)
                donatePreference.isSelectable = true
            }
        })
        viewModel.theme.observe(this, Observer { themeSelectorPreference.value = it.toString() })
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference) {
            evStepsPreference -> {
                viewModel.setEvSteps(newValue.toString().toInt())
            }
            filterSortingPreference -> {
                viewModel.setFilterSortOrder(newValue.toString().toInt())
            }
            themeSelectorPreference -> {
                if (viewModel.hasPremium.value == true) {
                    viewModel.setTheme(newValue.toString().toInt())
                    // re-create all open activities
                    TaskStackBuilder.create(context!!)
                          .addNextIntent(Intent(activity, CalculatorActivity::class.java))
                          .addNextIntent(activity!!.intent)
                          .startActivities()
                } else {
                    val intent = Intent(context, BillingActivity().javaClass)
                    startActivityForResult(intent, 1)
                    return false
                }
            }
            else -> return false
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference) {
            donatePreference -> {
                val intent = Intent(context, BillingActivity().javaClass)
                startActivityForResult(intent, 1)
            }
            aboutPreference -> {
                val fragment = ChangelogDialog()
                fragment.show(childFragmentManager, null)
            }
            showWarningPreference -> {
                viewModel.setWarning((preference as SwitchPreference).isChecked)
            }
            alarmBeepPreference -> {
                viewModel.setAlarmBeep((preference as SwitchPreference).isChecked)
            }
            alarmVibratePreference -> {
                viewModel.setAlarmVibrate((preference as SwitchPreference).isChecked)
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

    private fun showError(errorMsg: String) {
        val snackbar = Snackbar.make(view!!, errorMsg, Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.holo_red_light))
        snackbar.show()
    }

}
