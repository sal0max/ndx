package de.salomax.ndx.ui.calibrator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import de.salomax.ndx.R
import de.salomax.ndx.databinding.ActivityCalibratorBinding
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.calculator.ShutterAdapter
import de.salomax.ndx.util.MathUtils
import de.salomax.ndx.widget.CenterLineDecoration

class CalibratorActivity : BaseActivity() {

    private lateinit var binding: ActivityCalibratorBinding
    private lateinit var viewModel: CalibratorViewModel

    // without filter
    private var shutterBefore: Long = 1 // micro
    private var isoBefore: Int = 100

    // with filter
    private var shutterSecAfter: Int = 1 // s
    private var shutterMinAfter: Int = 1 // min
    private var isoAfter: Int = 100

    // calculated result
    private var currentFactor: Double = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        binding = ActivityCalibratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(CalibratorViewModel::class.java)

        // title bar
        supportActionBar?.apply {
            setTitle(R.string.title_calibrator)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }

        // prepare all snappy views
        // all
        listOf(binding.snappy1a, binding.snappy1b, binding.snappy2a, binding.snappy2b, binding.snappy2c)
                .forEach {
                    it.apply {
                        addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
                        setHasFixedSize(true)
                    }
                }
        // without filter
        binding.snappy1a.apply {
            adapter = ShutterAdapter(this@CalibratorActivity)
            (adapter as ShutterAdapter).onSpeedSelected = {
                shutterBefore = it
                showResult()
            }
        }
        binding.snappy1b.apply {
            adapter = IsoAdapter(this@CalibratorActivity)
            (adapter as IsoAdapter).onIsoSelected = {
                isoBefore = it
                showResult()
            }
        }
        // with filter
        binding.snappy2a.apply {
            adapter = SecondsAdapter(this@CalibratorActivity, 15)
            (adapter as SecondsAdapter).onValueSelected = {
                shutterMinAfter = it
                showResult()
            }
            scrollToPosition(1)
        }
        binding.snappy2b.apply {
            adapter = SecondsAdapter(this@CalibratorActivity, 59)
            (adapter as SecondsAdapter).onValueSelected = {
                shutterSecAfter = it
                showResult()
            }
        }
        binding.snappy2c.apply {
            adapter = IsoAdapter(this@CalibratorActivity)
            (adapter as IsoAdapter).onIsoSelected = {
                isoAfter = it
                showResult()
            }
        }

        // refresh data on init & observe
        viewModel.speeds.observe(this, {
            val adapter = binding.snappy1a.adapter as ShutterAdapter
            if (adapter.speeds != it) {
                // set new values
                adapter.speeds = it
                // scroll to middle
                binding.snappy1a.scrollToPosition(it.doubleValues.size / 2)
            }
        })
        viewModel.isoSteps.observe(this, {
            (binding.snappy1b.adapter as IsoAdapter).isoSteps = it
            (binding.snappy2c.adapter as IsoAdapter).isoSteps = it
        })
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ok -> {
                val returnIntent = Intent()
                returnIntent.putExtra("FACTOR", currentFactor.toInt())
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
                true
            }
            R.id.show_manual -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.manual_summary)
                builder.setPositiveButton(R.string.ok) { _, _ -> }
                builder.create().show()
                true
            }
            else -> false
        }
    }

    /**
     *
     */
    private fun showResult() {
        currentFactor = ((((shutterMinAfter * 60) + shutterSecAfter) * 1_000_000L) / shutterBefore.toDouble() // seconds
                * isoAfter / isoBefore) // iso
        // error
        if (currentFactor < 1) {
            binding.factor.text = "..."
            binding.fStops.text = String.format(resources.getString(R.string.label_f_stops), "...")
            binding.nd.text = String.format(getString(R.string.label_nd), "...")
        }
        // result
        else {
            binding.factor.text = String.format(getString(R.string.label_factor), currentFactor.toInt())
            binding.fStops.text = String.format(getString(R.string.label_f_stops), MathUtils.factor2fstop(currentFactor))
            binding.nd.text = String.format(getString(R.string.label_nd), MathUtils.factor2nd(currentFactor))
        }
    }

}
