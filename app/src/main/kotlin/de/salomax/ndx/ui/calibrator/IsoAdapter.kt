package de.salomax.ndx.ui.calibrator

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.data.model.IsoSteps
import de.salomax.ndx.widget.SnappyRecyclerView

class IsoAdapter(private val context: AppCompatActivity) : RecyclerView.Adapter<IsoAdapter.ViewHolder>() {

    companion object {
        private var items: IsoSteps? = null
    }

    var onIsoSelected: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.row_shutter, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items?.get(position)?.toString()
    }

    fun setISOs(isoSteps: IsoSteps?) {
        if (items != isoSteps) {
            items = isoSteps
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        return items?.get(position)?.toLong() ?: -1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (recyclerView as SnappyRecyclerView).snapped.observe(context, Observer {
            onIsoSelected?.invoke(items?.get(it) ?: 100)
        })
    }

    override fun getItemCount() = items?.values?.size ?: 0

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
