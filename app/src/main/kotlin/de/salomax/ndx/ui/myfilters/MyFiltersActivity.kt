package de.salomax.ndx.ui.myfilters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter

/**
 * Shows a list of all filters.
 * Can also start a dialog to add new or edit existing filters.
 */
class MyFiltersActivity : AppCompatActivity() {

    private lateinit var viewDelegate: ViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val presenter = Presenter()
        viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        supportActionBar?.apply {
            title = getString(R.string.title_myFilters)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val filter = data.getParcelableExtra<Filter>("FILTER")
            viewDelegate.pushEvent(Event.ReceivedDeletionResult(filter))
        }
    }

}
