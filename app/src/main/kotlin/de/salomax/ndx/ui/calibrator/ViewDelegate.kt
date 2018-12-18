package de.salomax.ndx.ui.calibrator

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.TextView
import com.joaquimverges.helium.core.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import de.salomax.ndx.ui.calculator.ShutterAdapter
import de.salomax.ndx.util.MathUtils
import de.salomax.ndx.widget.CenterLineDecoration
import de.salomax.ndx.widget.SnappyRecyclerView

class ViewDelegate(inflater: LayoutInflater) : BaseViewDelegate<State, Event>(R.layout.activity_calibrator, inflater) {

    // without filter
    private val listShutterBefore: SnappyRecyclerView = view.findViewById(R.id.snappy1a)
    private val listIsoBefore: SnappyRecyclerView = view.findViewById(R.id.snappy1b)

    private var shutterBefore: Long = 1 // micro
    private var isoBefore: Int = 100

    // with filter
    private val listMinutesAfter: SnappyRecyclerView = view.findViewById(R.id.snappy2a)
    private val listSecondsAfter: SnappyRecyclerView = view.findViewById(R.id.snappy2b)
    private val listIsoAfter: SnappyRecyclerView = view.findViewById(R.id.snappy2c)

    private var shutterSecAfter: Int = 1 // s
    private var shutterMinAfter: Int = 1 // min
    private var isoAfter: Int = 100

    // calculated result
    private var factor: Double = 1.0
    private val resultFactor: TextView = view.findViewById(R.id.factor)
    private val resultStops: TextView = view.findViewById(R.id.f_stops)
    private val resultNd: TextView = view.findViewById(R.id.nd)

    init {
        // all
        listOf(listShutterBefore, listIsoBefore,
                listMinutesAfter, listSecondsAfter, listIsoAfter)
                .forEach {
                    it.apply {
                        addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
                        setHasFixedSize(true)
                    }
                }
        // without filter
        listShutterBefore.apply {
            adapter = ShutterAdapter()
            snappedEvent.subscribe {
                shutterBefore = (adapter as ShutterAdapter).selectedSpeed
                showResult()
            }
        }
        listIsoBefore.apply {
            adapter = IsoAdapter()
            snappedEvent.subscribe {
                isoBefore = (adapter as IsoAdapter).selectedValue
                showResult()
            }
        }
        // with filter
        listMinutesAfter.apply {
            adapter = SecondsAdapter(15)
            snappedEvent.subscribe {
                shutterMinAfter = (adapter as SecondsAdapter).selectedValue
                showResult()
            }
        }
        listSecondsAfter.apply {
            adapter = SecondsAdapter(59)
            snappedEvent.subscribe {
                shutterSecAfter = (adapter as SecondsAdapter).selectedValue
                showResult()
            }
        }
        listIsoAfter.apply {
            adapter = IsoAdapter()
            snappedEvent.subscribe {
                isoAfter = (adapter as IsoAdapter).selectedValue
                showResult()
            }
        }
    }

    override fun render(viewState: State) {
        when (viewState) {
            is State.ShowManual -> {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.manual_summary)
                builder.setPositiveButton(R.string.ok) { _, _ -> }
                builder.create().show()
            }
            is State.ISOsReady -> {
                (listIsoAfter.adapter as IsoAdapter).setISOs(viewState.isos)
                (listIsoBefore.adapter as IsoAdapter).setISOs(viewState.isos)
            }
            is State.Finish -> {
                val returnIntent = Intent()
                returnIntent.putExtra("FACTOR", factor.toInt())
                val activity = context as AppCompatActivity
                activity.setResult(Activity.RESULT_OK, returnIntent)
                activity.finish()
            }
        }
    }

    private fun showResult() {
        factor = ((((shutterMinAfter * 60) + shutterSecAfter) * 1_000_000L) / shutterBefore.toDouble() // seconds
                * isoAfter / isoBefore) // iso
        // error
        if (factor < 1) {
            resultFactor.text = "..."
            resultStops.text = String.format(view.resources.getString(R.string.label_f_stops), "...")
            resultNd.text = String.format(view.resources.getString(R.string.label_nd), "...")
        }
        // result
        else {
            resultFactor.text = String.format(view.resources.getString(R.string.label_factor), factor.toInt())
            resultStops.text = String.format(view.resources.getString(R.string.label_f_stops), MathUtils.factor2fstop(factor))
            resultNd.text = String.format(view.resources.getString(R.string.label_nd), MathUtils.factor2nd(factor))
        }
    }

}
