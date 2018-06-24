package de.salomax.ndx.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import de.salomax.ndx.R
import de.salomax.ndx.data.Pref

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // get theme from shared prefs
        val theme = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(Pref.THEME, resources.getStringArray(R.array.prefValues_themes)[0])
        // apply theme
        when (theme) {
            "0" -> setTheme(R.style.AppTheme_Light)
            "1" -> setTheme(R.style.AppTheme_Dark)
            "2" -> setTheme(R.style.AppTheme_Black)
        }
        super.onCreate(savedInstanceState)
    }

}
