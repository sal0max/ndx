package de.salomax.ndx.ui.calculator

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.data.model.Compensation
import de.salomax.ndx.widget.SnappyRecyclerView

class CompensationAdapter(private val context: AppCompatActivity) : RecyclerView.Adapter<CompensationAdapter.ViewHolder>() {

    var compensation: Compensation? = null
        @SuppressLint("NotifyDataSetChanged")
        set(compensation) {
            if (this.compensation != compensation) {
                field = compensation
                notifyDataSetChanged()
            }
        }

    var onCompensationSelected: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.row_shutter, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = compensation?.text?.get(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (recyclerView as SnappyRecyclerView).snapped.observe(context, {
            onCompensationSelected?.invoke(compensation?.offset?.get(it) ?: 0)
        })
    }

    override fun getItemCount() = compensation?.text?.size ?: 0

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
