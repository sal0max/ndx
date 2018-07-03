package de.salomax.ndx.ui.filterpouch

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
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class FilterAdapter(private val context: Context) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    private val clickSubject = PublishSubject.create<Filter>()
    private val items: MutableList<Filter> = mutableListOf()

    val clickEvent: Observable<Filter> = clickSubject

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView1.text = items[position].name
        //
        val factor = items[position].factor
        val stops = MathUtils.factor2fstopRounded(factor)
        val nd = MathUtils.factor2nd(factor)
        holder.textView2.text = context.resources.getQuantityString(R.plurals.filterInfo,
                factor - 1,
                factor, stops, nd,
                if (items[position].info.isNullOrBlank()) "" else "\n" + items[position].info)
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].id!!

    fun setFilters(filters: List<Filter>) {
        items.clear()
        items.addAll(filters)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView1: TextView = itemView.findViewById(R.id.name)
        val textView2: TextView = itemView.findViewById(R.id.info)

        init {
            itemView.findViewById<Switch>(R.id.checkbox).visibility = View.GONE
            itemView.setOnClickListener {
                clickSubject.onNext(items[layoutPosition])
            }
        }
    }

}
