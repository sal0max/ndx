package de.salomax.ndx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import de.salomax.ndx.R
import de.salomax.ndx.data.PrefDao

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // pure black
        setTheme(
            if (PrefDao.getInstance(this).isPureBlackEnabled())
                R.style.AppTheme_Black
            else
                R.style.AppTheme
        )
        // theme
        AppCompatDelegate.setDefaultNightMode(
            when (PrefDao.getInstance(this).getThemeSync()) {
                // light
                0 -> AppCompatDelegate.MODE_NIGHT_NO
                // dark
                1 -> AppCompatDelegate.MODE_NIGHT_YES
                // system default (light or dark)
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        super.onCreate(savedInstanceState)
    }

}
