package de.salomax.ndx.ui.calculator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.billing.BillingActivity
import de.salomax.ndx.ui.filterpouch.FilterPouchActivity
import de.salomax.ndx.ui.preferences.PreferenceActivity
import de.salomax.ndx.ui.timer.TimerActivity
import de.salomax.ndx.widget.CenterLineDecoration
import kotlinx.android.synthetic.main.activity_calculator.*

class CalculatorActivity : BaseActivity() {

    private lateinit var viewModel: CalculatorViewModel
    private var timerMenuEnabled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        setContentView(R.layout.activity_calculator)
        viewModel = ViewModelProvider(this).get(CalculatorViewModel::class.java)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // list & adapter : shutter speeds
        recycler_shutter.apply {
            adapter = ShutterAdapter(this@CalculatorActivity)
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            // addItemDecoration(DotDividerDecoration(ContextCompat.getColor(context, android.R.color.white)))
            setHasFixedSize(true)
            (adapter as ShutterAdapter).onSpeedSelected = {
                viewModel.selectedSpeed.value = it
            }
        }
        // list & adapter : filters
        recycler_filters.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = FilterAdapter(this@CalculatorActivity)
            (adapter as FilterAdapter).onFilterFactorChanged = {
                viewModel.filterFactor.value = it
            }
        }

        // refresh data on init & observe
        viewModel.filters.observe(this, Observer {
            (recycler_filters.adapter as FilterAdapter).setFilters(it)
        })
        viewModel.speeds.observe(this, Observer {
            (recycler_shutter.adapter as ShutterAdapter).setSpeeds(it)
        })
        viewModel.isWarningEnabled.observe(this, Observer { enabled ->
            enabled?.let { resultView.showWarning = it }
        })
        viewModel.calculatedSpeed.observe(this, Observer { micros ->
            resultView.setDuration(micros)
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
                if (viewModel.hasPremium.value == true) {
                    val i = Intent(this, TimerActivity().javaClass)
                    i.putExtra("MILLIS", viewModel.calculatedSpeed.value?.div(1000))
                    startActivity(i)
                    true
                } else {
                    val intent = Intent(this, BillingActivity().javaClass)
                    startActivityForResult(intent, 1)
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
