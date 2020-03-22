package de.salomax.ndx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import de.salomax.ndx.R
import de.salomax.ndx.data.Pref
import de.salomax.ndx.data.PrefDao
import de.salomax.ndx.util.Logger

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // apply theme
        when (PrefDao.getInstance(this).getThemeSync()) {
            0 -> setTheme(R.style.AppTheme_Light)
            1 -> setTheme(R.style.AppTheme_Dark)
            2 -> setTheme(R.style.AppTheme_Black)
        }

        super.onCreate(savedInstanceState)
    }

}
