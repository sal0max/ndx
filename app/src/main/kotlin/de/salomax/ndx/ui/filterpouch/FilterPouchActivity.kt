package de.salomax.ndx.ui.filterpouch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.databinding.ActivityFilterpouchBinding
import de.salomax.ndx.ui.FilterAdapter
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.billing.BillingActivity
import de.salomax.ndx.ui.filtereditor.FilterEditorActivity

/**
 * Shows a list of all filters.
 * Can also start a dialog to add new or edit existing filters.
 */
class FilterPouchActivity : BaseActivity() {

    private lateinit var binding: ActivityFilterpouchBinding
    private lateinit var viewModel: FilterPouchViewModel

    private val filterAdapter = FilterAdapter(this, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        binding = ActivityFilterpouchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(FilterPouchViewModel::class.java)

        filterAdapter.onClick = {
            val intent = Intent(this, FilterEditorActivity().javaClass)
            intent.putExtra(FilterEditorActivity.ARG_FILTER, it)
            filterEditorActivityWithResult.launch(intent)
        }
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
//            addItemDecoration(MarginHorizontalDividerItemDecoration(this@FilterPouchActivity))
            adapter = filterAdapter
        }
        binding.fabAdd.setOnClickListener {
            if (
                  viewModel.filters.value?.first?.size?.let { it < 3 } == true || // less than 4 filters until now...
                  viewModel.hasPremium                                     // ...or has premium
            ) {
                val intent = Intent(this, FilterEditorActivity().javaClass)
                startActivity(intent)
            } else {
                val intent = Intent(this, BillingActivity().javaClass)
                startActivity(intent)
            }
        }

        // title bar
        setTitle(R.string.title_filterPouch)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // refresh data on init & observe
        viewModel.filters.observe(this) { filterAdapter.setFilters(it.first, it.second) }
    }

    private val filterEditorActivityWithResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        // filter got deleted: show undo
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val filter = data.getParcelableExtra<Filter>("FILTER")!!
            Snackbar.make(binding.list, getString(R.string.filterDeleted, filter.name), 5_000) // 5s
                .setAction(R.string.undo) { viewModel.insert(filter) }
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
