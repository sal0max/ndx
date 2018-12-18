package de.salomax.ndx.ui.calibrator

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import de.salomax.ndx.R
import de.salomax.ndx.widget.SnappyRecyclerView
import io.reactivex.disposables.Disposable

class SecondsAdapter(max: Int) : RecyclerView.Adapter<SecondsAdapter.ViewHolder>() {

    var selectedValue: Int = 1
        private set

    private val items: IntArray = IntArray(max + 1) { it }

    private var subscription: Disposable? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_shutter, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position].toString()
    }

    override fun getItemId(position: Int): Long {
        return items[position].toLong()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        subscription = (recyclerView as SnappyRecyclerView).snappedEvent.subscribe {
            selectedValue = items[it]
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subscription?.dispose()
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
