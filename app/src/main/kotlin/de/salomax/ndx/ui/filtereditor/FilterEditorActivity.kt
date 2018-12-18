package de.salomax.ndx.ui.filtereditor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.joaquimverges.helium.core.retained.RetainedPresenters
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.ui.BaseActivity

class FilterEditorActivity : BaseActivity() {

    companion object {
        const val ARG_FILTER = "ARG_FILTER"
    }

    private lateinit var presenter: Presenter
    private lateinit var viewDelegate: ViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = RetainedPresenters.get(this, Presenter::class.java)
        viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        // edit existing filter
        val filter = intent.getParcelableExtra<Filter>(ARG_FILTER)
        if (filter != null) presenter.pushState(State.Prepopulate(filter))

        // title bar
        setTitle(if (filter != null) R.string.title_editFilter else R.string.title_addFilter)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.filtereditor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save -> {
                presenter.pushState(State.InsertOrUpdate)
                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null && data.hasExtra("FACTOR")) {
            val factor = data.getIntExtra("FACTOR", 1)
            viewDelegate.pushEvent(Event.Calibrated(factor))
        }
    }

}
