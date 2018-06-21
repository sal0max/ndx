package de.salomax.ndx.ui.calculator

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.joaquimverges.helium.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import de.salomax.ndx.util.MathUtils
import de.salomax.ndx.widget.CenterLineDecoration
import de.salomax.ndx.widget.ResultView
import de.salomax.ndx.widget.SnappyRecyclerView

class ViewDelegate(inflater: LayoutInflater)
    : BaseViewDelegate<State, Event>(R.layout.activity_calculator, inflater) {

    private val filterAdapter: FilterAdapter = FilterAdapter(context)
    private val shutterSpeedsAdapter = ShutterAdapter()

    private var selectedSpeed: Long = 1
    private var filterFactor: Long = 1

    private val shutterView: SnappyRecyclerView = view.findViewById(R.id.recycler_shutter)
    private val filtersView: RecyclerView = view.findViewById(R.id.recycler_filters)
    private val resultView: ResultView = view.findViewById(R.id.resultView)

    init {
        // list & adapter : shutter speeds
        shutterView.apply {
            adapter = shutterSpeedsAdapter
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            // addItemDecoration(DotDividerDecoration(ContextCompat.getColor(context, android.R.color.white)))
            setHasFixedSize(true)
            snappedEvent.subscribe {
                selectedSpeed = shutterSpeedsAdapter.selectedSpeed
                showResult()
            }
        }
        // list & adapter : filters
        filtersView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = filterAdapter
            filterAdapter.filterFactorChanged.subscribe {
                filterFactor = it
                showResult()
            }
        }
    }

    override fun render(viewState: State) {
        when (viewState) {
            is State.FiltersReady -> filterAdapter.setFilters(viewState.filters)
            is State.ShutterSpeedsReady -> {
                shutterSpeedsAdapter.setSpeeds(viewState.speeds)
                shutterView.snap()
                resultView.evSteps = viewState.speeds
                showResult()
            }
            is State.ShowWarning -> {
                resultView.showWarning = viewState.enabled
                showResult()
            }
        }
    }

    private fun showResult() {
        val micro = MathUtils.multiply(selectedSpeed, filterFactor)
        resultView.setDuration(micro)
        // activate timer?
        if (micro != null)
            (context as CalculatorActivity).enableTimer(micro >= 1_000_000L) // >= 1s
        else
            (context as CalculatorActivity).enableTimer(false)
    }

    // micros
    fun getSelectedSpeed(): Long? {
        return MathUtils.multiply(selectedSpeed, filterFactor)
    }

}
