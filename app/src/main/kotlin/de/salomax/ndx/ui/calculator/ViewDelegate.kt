package de.salomax.ndx.ui.calculator

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.joaquimverges.helium.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import de.salomax.ndx.data.ShutterSpeeds
import de.salomax.ndx.util.MathUtils
import de.salomax.ndx.widget.CenterLineDecoration
import de.salomax.ndx.widget.DotDividerDecoration
import de.salomax.ndx.widget.SnappyRecyclerView
import de.salomax.ndx.widget.ResultView

class ViewDelegate(inflater: LayoutInflater)
    : BaseViewDelegate<State, Event>(R.layout.activity_calculator, inflater) {

    private val filterAdapter: FilterAdapter = FilterAdapter(context)
    private val shutterSpeedsAdapter = ShutterAdapter()

    private var selectedSpeed: Long = 1
    private var filterFactor: Long = 1
    private var speeds: ShutterSpeeds? = null

    private val resultView = view.findViewById<ResultView>(R.id.resultView)


    init {
        // list & adapter : shutter speeds
        view.findViewById<SnappyRecyclerView>(R.id.recycler_shutter).apply {
            adapter = shutterSpeedsAdapter
            addItemDecoration(CenterLineDecoration(ContextCompat.getColor(context, android.R.color.white))) // center line
            // addItemDecoration(DotDividerDecoration(ContextCompat.getColor(context, android.R.color.white)))
            setHasFixedSize(true)
            snappedEvent.subscribe({
                selectedSpeed = shutterSpeedsAdapter.selectedSpeed
                showResult()
            })
        }
        // list & adapter : filters
        view.findViewById<RecyclerView>(R.id.recycler_filters).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = filterAdapter
            filterAdapter.filterFactorChanged.subscribe({
                filterFactor = it
                showResult()
            })
        }
    }

    override fun render(viewState: State) {
        when (viewState) {
            is State.FiltersReady -> filterAdapter.setFilters(viewState.filters)
            is State.ShutterSpeedsReady -> {
                speeds = viewState.speeds
                shutterSpeedsAdapter.speeds = speeds
                resultView.speed = speeds
                showResult()
            }
        }
    }

    private fun showResult() {
        val micro = MathUtils.multiply(selectedSpeed, filterFactor)
        resultView.setDuration(micro)
    }

}
