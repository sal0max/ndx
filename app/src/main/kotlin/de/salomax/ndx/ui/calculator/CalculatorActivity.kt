package de.salomax.ndx.ui.calculator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.joaquimverges.helium.retained.RetainedPresenters
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.filterpouch.FilterPouchActivity
import de.salomax.ndx.ui.preferences.PreferenceActivity
import de.salomax.ndx.ui.timer.TimerActivity

class CalculatorActivity : BaseActivity() {

    private var timerEnabled: Boolean = true
    private lateinit var viewDelegate: ViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val presenter = RetainedPresenters.get(this, Presenter::class.java)
        viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        title = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.calculator, menu)

        // enable or disable timer
        val timer = menu.findItem(R.id.menu_timer)
        timer.isEnabled = timerEnabled
        timer.icon.alpha = if (timerEnabled) 255 else 128

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
            R.id.menu_timer -> {
                val i = Intent(this, TimerActivity().javaClass)
                i.putExtra("MILLIS", viewDelegate.getSelectedSpeed()!! / 1000)
                startActivity(i)
                true
            }
            else -> false
        }
    }

    fun enableTimer(enabled: Boolean) {
        if (enabled != timerEnabled) {
            timerEnabled = enabled
            invalidateOptionsMenu()
        }
    }

}
