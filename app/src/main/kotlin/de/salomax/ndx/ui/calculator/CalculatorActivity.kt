package de.salomax.ndx.ui.calculator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.databinding.ActivityCalculatorBinding
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.billing.BillingActivity
import de.salomax.ndx.ui.filterpouch.FilterPouchActivity
import de.salomax.ndx.ui.preferences.PreferenceActivity
import de.salomax.ndx.ui.timer.TimerActivity
import de.salomax.ndx.widget.CenterLineDecoration
import de.salomax.ndx.widget.MarginHorizontalDividerItemDecoration

class CalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private lateinit var viewModel: CalculatorViewModel
    private var timerMenuEnabled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(CalculatorViewModel::class.java)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // list & adapter : shutter speeds
        binding.dials.recyclerShutter.apply {
            adapter = ShutterAdapter(this@CalculatorActivity)
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            // addItemDecoration(DotDividerDecoration(ContextCompat.getColor(context, android.R.color.white)))
            setHasFixedSize(true)
            (adapter as ShutterAdapter).onSpeedSelected = {
                viewModel.selectedSpeed.value = it
            }
        }
        // list & adapter : compensation
        binding.dials.recyclerCompensation.apply {
            adapter = CompensationAdapter(this@CalculatorActivity)
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            // addItemDecoration(DotDividerDecoration(ContextCompat.getColor(context, android.R.color.white)))
            setHasFixedSize(true)
            (adapter as CompensationAdapter).onCompensationSelected = {
                viewModel.selectedOffset.value = it
            }
        }
        // list & adapter : filters
        binding.recyclerFilters.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(MarginHorizontalDividerItemDecoration(this@CalculatorActivity))
            adapter = FilterAdapter(this@CalculatorActivity)
            (adapter as FilterAdapter).onFilterFactorChanged = {
                viewModel.filterFactor.value = it
            }
        }

        // refresh data on init & observe
        viewModel.filters.observe(this, Observer {
            (binding.recyclerFilters.adapter as FilterAdapter).setFilters(it)
        })
        viewModel.speeds.observe(this, Observer {
            val adapter = binding.dials.recyclerShutter.adapter as ShutterAdapter
            if (adapter.speeds != it) {
                // set new speeds
                adapter.speeds = it
                // scroll to middle
                binding.dials.recyclerShutter.scrollToPosition(it.doubleValues.size / 2)
            }
        })
        viewModel.compensation.observe(this, Observer {
            val adapter = binding.dials.recyclerCompensation.adapter as CompensationAdapter
            if (adapter.compensation != it) {
                // set new compensation values
                adapter.compensation = it
                // scroll to middle
                binding.dials.recyclerCompensation.scrollToPosition(it.text.size / 2)
            }
        })
        viewModel.isWarningEnabled.observe(this, Observer {
            binding.resultView.showWarning = it
        })
        viewModel.isCompensationDialEnabled.observe(this, Observer {
            when (it) {
                true -> {
                    if (binding.dials.compensationContainer.visibility != View.VISIBLE) {
                        binding.dials.compensationContainer.visibility = View.VISIBLE
                        // scroll to center
                        binding.dials.recyclerCompensation.adapter?.itemCount?.div(2)?.let { center ->
                            binding.dials.recyclerCompensation.scrollToPosition(center)
                        }
                    }
                }
                false -> {
                    binding.dials.compensationContainer.visibility = View.GONE
                    viewModel.selectedOffset.value = 0
                }
            }
        })
        viewModel.calculatedSpeed.observe(this, Observer { micros ->
            binding.resultView.duration = micros
            enableTimer(micros != null && micros >= 1_000_000L)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.calculator, menu)

        // enable or disable timer
        menu.findItem(R.id.menu_timer).apply {
            isEnabled = timerMenuEnabled
            icon.alpha = if (timerMenuEnabled) 255 else 128
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_manage_filters -> {
                startActivity(Intent(this, FilterPouchActivity().javaClass))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, PreferenceActivity().javaClass))
                true
            }
            R.id.menu_timer -> {
                if (viewModel.hasPremium) {
                    val i = Intent(this, TimerActivity().javaClass)
                    i.putExtra("MILLIS", viewModel.calculatedSpeed.value?.div(1000))
                    startActivity(i)
                    true
                } else {
                    val intent = Intent(this, BillingActivity().javaClass)
                    startActivity(intent)
                    false
                }
            }
            else -> false
        }
    }

    private fun enableTimer(enabled: Boolean) {
        if (enabled != timerMenuEnabled) {
            timerMenuEnabled = enabled
            invalidateOptionsMenu()
        }
    }

}
