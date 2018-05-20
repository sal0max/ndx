package de.salomax.ndx.ui.calculator

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import de.salomax.ndx.R
import de.salomax.ndx.data.ShutterSpeeds
import de.salomax.ndx.widget.SnappyRecyclerView
import io.reactivex.disposables.Disposable

class ShutterAdapter : RecyclerView.Adapter<ShutterAdapter.ViewHolder>() {

    var selectedSpeed: Long = 1
        private set
    var speeds: ShutterSpeeds? = null
        set (speeds) {
            field = speeds
            notifyDataSetChanged()
        }
    private var subscription: Disposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.row_shutter, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = speeds?.htmlValues?.get(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        subscription = (recyclerView as SnappyRecyclerView).snappedEvent.subscribe({
            selectedSpeed = speeds?.doubleValues?.get(it) ?: 1
        })
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subscription?.dispose()
    }

    override fun getItemCount() = speeds?.doubleValues?.size ?: 0

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
