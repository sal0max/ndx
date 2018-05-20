package de.salomax.ndx.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.util.AttributeSet
import android.view.LayoutInflater
import de.salomax.ndx.R
import de.salomax.ndx.data.ShutterSpeeds
import kotlinx.android.synthetic.main.view_result.view.*

@SuppressLint("SetTextI18n")
class ResultView : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_result, this)
    }

    var speed: ShutterSpeeds? = ShutterSpeeds.FULL

    fun setDuration(micro: Long?) {
        // sanity check
        if (micro != null) {
            if (micro < 1_000_000L)
                smaller1s(micro)
            else
                greater1s(micro)
        } else {
            minutes.text = "\u221E"
            seconds.text = "\u221E"
            enableFractions(false)
            enableHours(false)
            enableDays(false)
        }
    }

    private fun smaller1s(micro: Long) {
        minutes.text = "00"
        seconds.text = getNearest(micro)

        enableFractions(false)
        enableDays(false)
        enableHours(false)
    }

    private fun greater1s(micro: Long) {
        val d = micro / 1_000_000 / 60 / 60 / 24
        val h = micro / 1_000_000 / 60 / 60 % 24
        val m = micro / 1_000_000 / 60 % 60
        val s = micro / 1_000_000 % 60
        val f = micro / 100_000 % 10

        days.text = d.toString()
        hours.text = h.toString()
        minutes.text = "%02d".format(m)
        seconds.text = "%02d".format(s)
        fractions.text = "." + f.toString()

        enableFractions(true)
        enableDays(d != 0L)
        enableHours(h != 0L)
    }

    private fun enableFractions(enable: Boolean) = if (enable) {
        fractions.visibility = VISIBLE
    } else {
        fractions.visibility = GONE
    }

    private fun enableHours(enable: Boolean) = if (enable) {
        hours.visibility = VISIBLE
        hours_unit.visibility = VISIBLE
    } else {
        hours.visibility = GONE
        hours_unit.visibility = GONE
    }

    private fun enableDays(enable: Boolean) = if (enable) {
        days.visibility = VISIBLE
        days_unit.visibility = VISIBLE
    } else {
        days.visibility = GONE
        days_unit.visibility = GONE
    }

    private fun getNearest(input: Long): Spanned {
        val s = speed
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
