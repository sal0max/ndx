package de.salomax.ndx.ui.filterpouch

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_filterpouch, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView1.text = items[position].name
        holder.textView2.text = context.resources.getString(R.string.filterInfo,
                items[position].factor.toString(),
                MathUtils.factor2fstopRounded(items[position].factor.toDouble()),
                MathUtils.factor2nd(items[position].factor.toDouble()),
                if (items[position].info.isNullOrBlank()) "" else "  ·  " + items[position].info)
    }

    override fun getItemCount() = items.size

    fun setFilters(filters: List<Filter>) {
        items.clear()
        items.addAll(filters)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView1: TextView = itemView.findViewById(R.id.name)
        val textView2: TextView = itemView.findViewById(R.id.info)

        init {
            itemView.setOnClickListener {
                clickSubject.onNext(items[layoutPosition])
            }
        }
    }

}
