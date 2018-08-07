package de.salomax.ndx.ui.calibrator

import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.joaquimverges.helium.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import de.salomax.ndx.ui.calculator.ShutterAdapter
import de.salomax.ndx.util.MathUtils
import de.salomax.ndx.widget.CenterLineDecoration
import de.salomax.ndx.widget.SnappyRecyclerView

class ViewDelegate(inflater: LayoutInflater) : BaseViewDelegate<State, Event>(R.layout.activity_calibrator, inflater) {

    private val listBefore: SnappyRecyclerView = view.findViewById(R.id.snappy1)
    private val listAfterMinutes: SnappyRecyclerView = view.findViewById(R.id.snappy2)
    private val listAfterSeconds: SnappyRecyclerView = view.findViewById(R.id.snappy3)

    private val resultFactor: TextView = view.findViewById(R.id.factor)
    private val resultStops: TextView = view.findViewById(R.id.f_stops)
    private val resultNd: TextView = view.findViewById(R.id.nd)
    private val resultError: TextView = view.findViewById(R.id.error)

    private var before: Long = 1 // micro
    private var afterMin: Int = 1 // min
    private var afterSec: Int = 1 // s

    init {
        listBefore.apply {
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            setHasFixedSize(true)
            adapter = ShutterAdapter()
            snappedEvent.subscribe {
                before = (adapter as ShutterAdapter).selectedSpeed
                showResult()
            }
        }
        listAfterMinutes.apply {
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            setHasFixedSize(true)
            adapter = SecondsAdapter(context, 15)
            snappedEvent.subscribe {
                afterMin = (adapter as SecondsAdapter).selectedValue
                showResult()
            }
        }
        listAfterSeconds.apply {
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            setHasFixedSize(true)
            adapter = SecondsAdapter(context, 59)
            snappedEvent.subscribe {
                afterSec = (adapter as SecondsAdapter).selectedValue
                showResult()
            }
        }
    }

    override fun render(viewState: State) {
        when (viewState) {
            //is State.FiltersReady -> filterAdapter.setFilters(viewState.data)
        }
    }

    private fun showResult() {
        val factor = (((afterMin * 60) + afterSec) * 1_000_000L) / before.toDouble()
        if (factor <= 0) {
            resultError.visibility = View.VISIBLE
            resultFactor.text = null
            resultStops.text = null
            resultNd.text = null
        } else {
            resultError.visibility = View.GONE
            resultFactor.text = String.format(view.resources.getString(R.string.label_factor), factor.toInt())
            resultStops.text = String.format(view.resources.getString(R.string.label_f_stops), MathUtils.factor2fstop(factor))
            resultNd.text = String.format(view.resources.getString(R.string.label_nd), MathUtils.factor2nd(factor))
        }
    }

}
