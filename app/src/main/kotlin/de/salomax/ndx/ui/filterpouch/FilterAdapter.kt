package de.salomax.ndx.ui.filterpouch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.util.MathUtils

class FilterAdapter(private val context: Context) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    private val items: MutableList<Filter> = mutableListOf()

    var onClick: ((Filter) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items[position].name
        //
        val factor = items[position].factor
        val stops = MathUtils.factor2fstopRounded(factor)
        val nd = MathUtils.factor2nd(factor)
        holder.tvInfo.text = context.resources.getQuantityString(R.plurals.filterInfo,
                factor - 1,
                factor, stops, nd)
        if (items[position].info.isNullOrBlank())
            holder.tvInfo2.visibility = View.GONE
        else {
            holder.tvInfo2.visibility = View.VISIBLE
            holder.tvInfo2.text = items[position].info
        }
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].id!!

    fun setFilters(filters: List<Filter>?) {
        items.clear()
        filters?.let {
            items.addAll(it)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.name)
        val tvInfo: TextView = itemView.findViewById(R.id.info)
        val tvInfo2: TextView = itemView.findViewById(R.id.info2)

        init {
            itemView.findViewById<SwitchCompat>(R.id.checkbox).visibility = View.GONE
            itemView.setOnClickListener {
                onClick?.invoke(items[layoutPosition])
            }
        }
    }

}
