package de.salomax.ndx.ui.myfilters

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.joaquimverges.helium.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import de.salomax.ndx.ui.filtereditor.FilterEditorActivity

class ViewDelegate(inflater: LayoutInflater) : BaseViewDelegate<State, Event>(R.layout.activity_myfilters, inflater) {

    private val filterAdapter: FilterAdapter = FilterAdapter(context)
    private val list: RecyclerView = view.findViewById(android.R.id.list)

    init {
        filterAdapter.clickEvent.subscribe({ filter -> pushEvent(Event.FilterClicked(filter)) })
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = filterAdapter
        }
        view.findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener({ pushEvent(Event.AddNewClicked) })
    }

    override fun render(viewState: State) {
        when (viewState) {
            is State.FiltersReady -> filterAdapter.setFilters(viewState.data)
            is State.EditFilter -> {
                val intent = Intent(context, FilterEditorActivity().javaClass)
                intent.putExtra(FilterEditorActivity.ARG_FILTER, viewState.data)
                (context as AppCompatActivity).startActivityForResult(intent, 1)
            }
            is State.AddNewFilter -> {
                val intent = Intent(context, FilterEditorActivity().javaClass)
                (context as AppCompatActivity).startActivity(intent)
            }
            is State.ShowUndoDeletionSnackbar -> {
                val snackbar = Snackbar.make(list,
                        context.getString(R.string.filterDeleted, viewState.filter.name),
                        5_000) // 5s
                        .setAction(R.string.undo, {
                            pushEvent(Event.RestoreFilter(viewState.filter))
                        })
                snackbar.show()
            }
        }
    }

}
