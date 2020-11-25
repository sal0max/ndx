package de.salomax.ndx.ui.calculator

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.data.model.ShutterSpeeds
import de.salomax.ndx.widget.SnappyRecyclerView

class ShutterAdapter(private val context: AppCompatActivity) : RecyclerView.Adapter<ShutterAdapter.ViewHolder>() {

    companion object {
        private var items: ShutterSpeeds? = null
    }

    var onSpeedSelected: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.row_shutter, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items?.htmlValues?.get(position)
    }

    fun setSpeeds(speeds: ShutterSpeeds?) {
        if (items != speeds) {
            items = speeds
            notifyDataSetChanged()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (recyclerView as SnappyRecyclerView).snapped.observe(context, {
            onSpeedSelected?.invoke(items?.doubleValues?.get(it) ?: 1)
        })
    }

    override fun getItemCount() = items?.doubleValues?.size ?: 0

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
