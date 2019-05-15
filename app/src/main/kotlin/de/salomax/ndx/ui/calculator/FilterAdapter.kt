package de.salomax.ndx.ui.calculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.util.MathUtils

class FilterAdapter(private val context: Context) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    /*
     * static fields which stores each filter and its state for
     * a) the view recycling
     * b) configuration changes
     */
    companion object {
        private val items: ArrayList<Filter> = arrayListOf()
        private val activeItems: HashSet<Filter> = hashSetOf()
    }

    var onFilterFactorChanged: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filter = items[position]
        holder.textView1.text = filter.name
        //
        val factor = items[position].factor
        val stops = MathUtils.factor2fstopRounded(factor)
        val nd = MathUtils.factor2nd(factor)
        holder.textView2.text = context.resources.getQuantityString(R.plurals.filterInfo,
                factor - 1,
                factor, stops, nd,
                if (items[position].info.isNullOrBlank()) "" else "\n" + items[position].info)
        //
        holder.switch.isChecked = activeItems.contains(filter)
    }

    override fun getItemCount() = items.size

    fun setFilters(filters: List<Filter>?) {
        items.clear()
        filters?.let {
            // add all filters
            items.addAll(it)
            // remove activeItems which aren't present any more
            activeItems.retainAll(filters)
            calculateFactor()
        }
        notifyDataSetChanged()
    }

    fun calculateFactor() {
        var factor = 1L
        for (itemsState in activeItems) {
            factor *= itemsState.factor
        }
        onFilterFactorChanged?.invoke(factor)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView1: TextView = itemView.findViewById(R.id.name)
        val textView2: TextView = itemView.findViewById(R.id.info)
        val switch: Switch = itemView.findViewById(R.id.checkbox)

        init {
            switch.isClickable = false
            itemView.setOnClickListener { switch.toggle() }
            switch.setOnCheckedChangeListener { _, isChecked ->
                val item = items[adapterPosition]
                if (isChecked)
                    activeItems.add(item)
                else
                    activeItems.remove(item)
                calculateFactor()
            }
        }
    }

}
