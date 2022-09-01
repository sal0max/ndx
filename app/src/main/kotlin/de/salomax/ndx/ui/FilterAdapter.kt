package de.salomax.ndx.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.databinding.RowFilterBinding
import de.salomax.ndx.databinding.RowHeaderFiltersizeBinding
import de.salomax.ndx.util.MathUtils

class FilterAdapter(private val context: Context, private val showSwitches: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /*
     * static fields which stores each filter and its state for
     * a) the view recycling
     * b) configuration changes
     */
    companion object {
        private var groupBySize = false
        private val items: ArrayList<Any> = arrayListOf()
        private val activeItems: HashSet<Filter> = hashSetOf()
    }

    var onClick: ((Filter) -> Unit)? = null
    var onFilterFactorChanged: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)

        // header
        return if (viewType == 0) {
            val viewBinding = RowHeaderFiltersizeBinding.inflate(layoutInflater, parent, false)
            HeaderViewHolder(viewBinding)
        }
        // filter
        else {
            val viewBinding = RowFilterBinding.inflate(layoutInflater, parent, false)
            FilterViewHolder(viewBinding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // header
        if (getItemViewType(position) == 0) {
            holder as HeaderViewHolder
            val filterSize = items[position] as Int
            holder.tvFilterSize.text = "\u2300 $filterSize mm"
        }
        // filter
        else {
            holder as FilterViewHolder

            val filter = items[position] as Filter
            // name
            holder.tvName.text = filter.name
            // size
            var textSize = ""
            if (!groupBySize) {
                textSize =
                    if (filter.size != null)
                        "\u2300 ${filter.size} mm\u2002|\u2002"
                    else
                        ""
            }
            // strength
            val factor = filter.factor
            val stops = MathUtils.factor2fstopRounded(factor)
            val nd = MathUtils.factor2nd(factor)
            holder.tvInfo.text = textSize +
                    context.resources.getQuantityString(
                        R.plurals.filterInfo,
                        factor - 1,
                        factor, stops, nd
                    )
            // notes
            if (filter.info.isNullOrBlank())
                holder.tvInfo2.visibility = View.GONE
            else {
                holder.tvInfo2.visibility = View.VISIBLE
                holder.tvInfo2.text = filter.info
            }
            //
            holder.switch.isChecked = activeItems.contains(filter)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position !in items.indices) -1
        else if (items[position] is Int) 0 else 1
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setFilters(filters: List<Filter>?, groupBySize: Boolean) {
        items.clear()
        Companion.groupBySize = groupBySize
        filters?.let { list ->
            if (groupBySize) {
                for ((i, item) in list.sortedBy { it.size }.withIndex()) {
                    if ((i == 0 || list[i - 1].size != item.size) && item.size != null)
                        items.add(item.size)
                    items.add(item)
                }
            } else
                items.addAll(list)
            // remove activeItems which aren't present anymore
            activeItems.retainAll(list.toSet())
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

    inner class FilterViewHolder(itemViewBinding: RowFilterBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        val tvName: TextView = itemViewBinding.name
        val tvInfo: TextView = itemViewBinding.info
        val tvInfo2: TextView = itemViewBinding.info2
        val switch: SwitchCompat = itemViewBinding.checkbox

        init {
            if (showSwitches) {
                switch.visibility = View.VISIBLE
                switch.isClickable = false
                itemView.setOnClickListener { switch.toggle() }
                switch.setOnCheckedChangeListener { _, isChecked ->
                    val item = items[bindingAdapterPosition] as Filter
                    if (isChecked)
                        activeItems.add(item)
                    else
                        activeItems.remove(item)
                    calculateFactor()
                }
            } else {
                switch.visibility = View.GONE
                itemView.setOnClickListener {
                    onClick?.invoke(items[layoutPosition] as Filter)
                }
            }


        }
    }

    inner class HeaderViewHolder(itemViewBinding: RowHeaderFiltersizeBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        val tvFilterSize: TextView = itemViewBinding.text
    }

}
