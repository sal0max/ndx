package de.salomax.ndx.widget

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.widget.TextView

//TODO delete
class LongSummaryPreference(context: Context?,
                                    attrs: AttributeSet?,
                                    defStyleAttr: Int,
                                    defStyleRes: Int) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val summary = holder?.findViewById(android.R.id.summary) as TextView
        summary.maxLines = 100
    }

}
