package de.salomax.ndx.ui.calibrator

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.data.model.IsoSteps
import de.salomax.ndx.widget.SnappyRecyclerView

class IsoAdapter(private val context: AppCompatActivity) : RecyclerView.Adapter<IsoAdapter.ViewHolder>() {

    var isoSteps: IsoSteps? = null
        @SuppressLint("NotifyDataSetChanged")
        set(isoSteps) {
            if (this.isoSteps != isoSteps) {
                field = isoSteps
                notifyDataSetChanged()
            }
        }

    var onIsoSelected: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.row_shutter, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = isoSteps?.get(position)?.toString()
    }

    override fun getItemId(position: Int): Long {
        return isoSteps?.get(position)?.toLong() ?: -1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (recyclerView as SnappyRecyclerView).snapped.observe(context, {
            onIsoSelected?.invoke(isoSteps?.get(it) ?: 100)
        })
    }

    override fun getItemCount() = isoSteps?.values?.size ?: 0

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
