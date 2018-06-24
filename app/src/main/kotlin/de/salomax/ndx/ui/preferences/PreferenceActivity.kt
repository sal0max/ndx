package de.salomax.ndx.ui.preferences

import android.os.Bundle
import android.view.MenuItem
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity


class PreferenceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_preferences)

        // title bar
        setTitle(R.string.title_preferences)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> false
        }
    }
}
