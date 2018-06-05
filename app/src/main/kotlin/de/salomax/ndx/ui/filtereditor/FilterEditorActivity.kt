package de.salomax.ndx.ui.filtereditor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.joaquimverges.helium.retained.RetainedPresenters
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter

class FilterEditorActivity : AppCompatActivity() {

    companion object {
        const val ARG_FILTER = "ARG_FILTER"
    }

    private lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = RetainedPresenters.get(this, Presenter::class.java)
        val viewDelegate = ViewDelegate(layoutInflater)
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

}
