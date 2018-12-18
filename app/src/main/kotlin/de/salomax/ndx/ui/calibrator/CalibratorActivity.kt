package de.salomax.ndx.ui.calibrator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.joaquimverges.helium.core.retained.RetainedPresenters
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity

class CalibratorActivity : BaseActivity() {

    private lateinit var presenter: Presenter
    private lateinit var viewDelegate: ViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = RetainedPresenters.get(this, Presenter::class.java)
        viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        // title bar
        supportActionBar?.apply {
            setTitle(R.string.title_calibrator)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.calibrator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.ok -> {
                presenter.pushState(State.Finish)
                true
            }
            R.id.manual_toggle -> {
                presenter.pushState(State.ShowManual)
                true
            }
            else -> false
        }
    }

}
