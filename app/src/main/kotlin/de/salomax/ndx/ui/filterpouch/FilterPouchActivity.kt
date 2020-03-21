package de.salomax.ndx.ui.filterpouch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.billing.BillingActivity
import de.salomax.ndx.ui.filtereditor.FilterEditorActivity
import de.salomax.ndx.widget.MarginHorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_filterpouch.*

/**
 * Shows a list of all filters.
 * Can also start a dialog to add new or edit existing filters.
 */
class FilterPouchActivity : BaseActivity() {

    private lateinit var viewModel: FilterPouchViewModel

    private val filterAdapter: FilterAdapter = FilterAdapter(this)

    companion object {
        private const val ARG_EDIT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        setContentView(R.layout.activity_filterpouch)
        viewModel = ViewModelProvider(this).get(FilterPouchViewModel::class.java)

        filterAdapter.onClick = {
            val intent = Intent(this, FilterEditorActivity().javaClass)
            intent.putExtra(FilterEditorActivity.ARG_FILTER, it)
            startActivityForResult(intent, ARG_EDIT)
        }
        list.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(MarginHorizontalDividerItemDecoration(this@FilterPouchActivity, resources.getDimension(R.dimen.margin2x).toInt()))
            adapter = filterAdapter
        }
        fab_add.setOnClickListener {
            if (
                  viewModel.filters.value?.size?.let { it < 4 } == true || // less than 4 filters until now...
                  viewModel.hasPremium                                     // ...or has premium
            ) {
                val intent = Intent(this, FilterEditorActivity().javaClass)
                startActivity(intent)
            } else {
                val intent = Intent(this, BillingActivity().javaClass)
                startActivityForResult(intent, 1)
            }
        }

        // title bar
        setTitle(R.string.title_filterPouch)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // refresh data on init & observe
        viewModel.filters.observe(this, Observer { filterAdapter.setFilters(it) })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // filter got deleted: show undo
        if (requestCode == ARG_EDIT && resultCode == Activity.RESULT_OK && data != null) {
            val filter = data.getParcelableExtra<Filter>("FILTER")!!
            Snackbar.make(list, getString(R.string.filterDeleted, filter.name), 5_000) // 5s
                    .setAction(R.string.undo) { viewModel.insert(filter) }
                    .show()
        }
    }

}
