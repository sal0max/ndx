package de.salomax.ndx.ui.calculator

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.joaquimverges.helium.retained.RetainedPresenters
import de.salomax.ndx.R
import de.salomax.ndx.ui.filterpouch.FilterPouchActivity
import de.salomax.ndx.ui.preferences.PreferenceActivity

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val presenter = RetainedPresenters.get(this, Presenter::class.java)
        val viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        title = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.calculator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_manage_filters -> {
                startActivity(Intent(this, FilterPouchActivity().javaClass))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, PreferenceActivity().javaClass))
                true
            }
            else -> false
        }
    }

}
