package de.salomax.ndx.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.salomax.ndx.R
import de.salomax.ndx.data.ShutterSpeeds
import kotlinx.android.synthetic.main.view_result.view.*

@SuppressLint("SetTextI18n")
class ResultView : ConstraintLayout {

    private var evSteps: ShutterSpeeds? = ShutterSpeeds.THIRD //TODO

    private val dUnit = resources.getString(R.string.unit_days)
    private val hUnit = resources.getString(R.string.unit_hours)

    var showWarning = false
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_result, this)
    }


    fun setDuration(micro: Long?) {
        // set time
        if (micro == null) {
            infinite()
        } else {
            if (micro < 1_000_000L)
                smaller1s(micro)
            else
                greater1s(micro)
        }
        // show warning
        if (showWarning && (micro == null || micro >= 1_000_000L * 60 * 20)) { // 20min
            warning.visibility = View.VISIBLE
        } else
            warning.visibility = View.GONE
    }


    private fun infinite() {
        minutes.text = "\u221E"
        seconds.text = "\u221E"
        hoursDays.text = null
    }

    private fun smaller1s(micro: Long) {
        minutes.text = "00"
        seconds.text = getNearest(micro)
        hoursDays.text = null
    }

    private fun greater1s(micro: Long) {
        val d = micro / 1_000_000 / 60 / 60 / 24
        val h = micro / 1_000_000 / 60 / 60 % 24
        val m = micro / 1_000_000 / 60 % 60
        val s = micro / 1_000_000 % 60
        val f = micro / 100_000 % 10

        /*
         * fractions & seconds
         */
        val sf = SpannableString("  " + // 2 leading blanks for centering
                "%02d".format(s) + // seconds
                "." + f.toString()) // fractions
        sf.setSpan(RelativeSizeSpan(.6f), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sf.setSpan(RelativeSizeSpan(.6f), sf.length - 2, sf.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        seconds.text = sf

        /*
         * minutes
         */
        minutes.text = "%02d".format(m)

        /*
         * hours & days
         */
        val hourDayStringBuilder = StringBuilder()
        if (d != 0L)
            hourDayStringBuilder
                    .append(d.toString())
                    .append(dUnit)
                    .append(" ")
        if (h != 0L)
            hourDayStringBuilder
                    .append(h.toString())
                    .append(hUnit)
        if (hourDayStringBuilder.isNotEmpty()) {
            hoursDays.visibility = View.VISIBLE
            val hourDayString = SpannableString(hourDayStringBuilder)
            val indexDUnit = hourDayStringBuilder.indexOf(dUnit)
            val indexHUnit = hourDayStringBuilder.indexOf(hUnit)
            if (indexDUnit != -1) hourDayString.setSpan(RelativeSizeSpan(.75f), indexDUnit, indexDUnit + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (indexHUnit != -1) hourDayString.setSpan(RelativeSizeSpan(.75f), indexHUnit, indexHUnit + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            hoursDays.text = hourDayString
        } else {
            hoursDays.visibility = View.INVISIBLE
        }

    }

    private fun getNearest(input: Long): Spanned {
        val s = evSteps
        return if (s != null) {
            var nearest: Spanned? = null
            var oldDiff = java.lang.Long.MAX_VALUE
            val values = s.doubleValues

            for ((index, value) in values.withIndex()) {
                val diff = Math.abs(value - input)
                if (oldDiff > diff) {
                    oldDiff = diff
                    nearest = s.htmlValues[index]
                }
            }
            if (nearest != null) SpannedString(nearest) else SpannedString("???")
        } else SpannedString("???")
    }

}
