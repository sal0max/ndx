package de.salomax.ndx.ui.calibrator

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.widget.SnappyRecyclerView

class SecondsAdapter(private val context: AppCompatActivity, max: Int) : RecyclerView.Adapter<SecondsAdapter.ViewHolder>() {

    private val items: IntArray = IntArray(max + 1) { it }

    var onValueSelected: ((Int) -> Unit)? = null

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
        (recyclerView as SnappyRecyclerView).snapped.observe(context, Observer {
            onValueSelected?.invoke(items[it])
        })
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
