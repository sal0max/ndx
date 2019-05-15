package de.salomax.ndx.ui.calibrator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.calculator.ShutterAdapter
import de.salomax.ndx.util.MathUtils
import de.salomax.ndx.widget.CenterLineDecoration
import kotlinx.android.synthetic.main.activity_calibrator.*

class CalibratorActivity : BaseActivity() {

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
        setContentView(R.layout.activity_calibrator)
        viewModel = ViewModelProviders.of(this).get(CalibratorViewModel::class.java)

        // title bar
        supportActionBar?.apply {
            setTitle(R.string.title_calibrator)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }

        // prepare all snappy views
        // all
        listOf(snappy1a, snappy1b, snappy2a, snappy2b, snappy2c)
                .forEach {
                    it.apply {
                        addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
                        setHasFixedSize(true)
                    }
                }
        // without filter
        snappy1a.apply {
            adapter = ShutterAdapter(this@CalibratorActivity)
            (adapter as ShutterAdapter).onSpeedSelected = {
                shutterBefore = it
                showResult()
            }
        }
        snappy1b.apply {
            adapter = IsoAdapter(this@CalibratorActivity)
            (adapter as IsoAdapter).onIsoSelected = {
                isoBefore = it
                showResult()
            }
        }
        // with filter
        snappy2a.apply {
            adapter = SecondsAdapter(this@CalibratorActivity, 15)
            (adapter as SecondsAdapter).onValueSelected = {
                shutterMinAfter = it
                showResult()
            }
        }
        snappy2b.apply {
            adapter = SecondsAdapter(this@CalibratorActivity, 59)
            (adapter as SecondsAdapter).onValueSelected = {
                shutterSecAfter = it
                showResult()
            }
        }
        snappy2c.apply {
            adapter = IsoAdapter(this@CalibratorActivity)
            (adapter as IsoAdapter).onIsoSelected = {
                isoAfter = it
                showResult()
            }
        }

        // refresh data on init & observe
        viewModel.speeds.observe(this, Observer { (snappy1a.adapter as ShutterAdapter).setSpeeds(it) })
        viewModel.isoSteps.observe(this, Observer { (snappy2c.adapter as IsoAdapter).setISOs(it) })
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
            factor.text = "..."
            f_stops.text = String.format(resources.getString(R.string.label_f_stops), "...")
            nd.text = String.format(getString(R.string.label_nd), "...")
        }
        // result
        else {
            factor.text = String.format(getString(R.string.label_factor), currentFactor.toInt())
            f_stops.text = String.format(getString(R.string.label_f_stops), MathUtils.factor2fstop(currentFactor))
            nd.text = String.format(getString(R.string.label_nd), MathUtils.factor2nd(currentFactor))
        }
    }

}
