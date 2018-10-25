package de.salomax.ndx.ui.filterpouch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.joaquimverges.helium.core.retained.RetainedPresenters
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.ui.BaseActivity

/**
 * Shows a list of all filters.
 * Can also start a dialog to add new or edit existing filters.
 */
class FilterPouchActivity : BaseActivity() {

    private lateinit var viewDelegate: ViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val presenter = RetainedPresenters.get(this, Presenter::class.java)
        viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        // title bar
        setTitle(R.string.title_filterPouch)
        supportActionBar?.apply {
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
