package de.salomax.ndx.ui.calculator

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.util.MathUtils
import io.reactivex.subjects.BehaviorSubject

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

    val filterFactorChanged = BehaviorSubject.createDefault(1L)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filter = items[position]
        holder.textView1.text = filter.name
        holder.textView2.text = context.resources.getString(R.string.filterInfo,
                filter.factor.toString(),
                MathUtils.factor2fstopRounded(filter.factor.toDouble()),
                MathUtils.factor2nd(filter.factor.toDouble()),
                if (filter.info.isNullOrBlank()) "" else "\n" + filter.info)
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
        filterFactorChanged.onNext(factor)
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
