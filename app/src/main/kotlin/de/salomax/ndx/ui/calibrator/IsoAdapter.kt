package de.salomax.ndx.ui.calibrator

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import de.salomax.ndx.R
import de.salomax.ndx.data.ISOs
import de.salomax.ndx.widget.SnappyRecyclerView
import io.reactivex.disposables.Disposable

class IsoAdapter : RecyclerView.Adapter<IsoAdapter.ViewHolder>() {

    var selectedValue: Int = 100
        private set

    private var items: ISOs? = null

    private var subscription: Disposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.row_shutter, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items?.get(position)?.toString()
    }

    fun setISOs(isos: ISOs) {
        if (items != isos) {
            items = isos
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        return items?.get(position)?.toLong() ?: -1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        subscription = (recyclerView as SnappyRecyclerView).snappedEvent.subscribe {
            selectedValue = items?.get(it) ?: 100
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subscription?.dispose()
    }

    override fun getItemCount() = items?.values?.size ?: 0

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
