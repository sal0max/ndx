package de.salomax.ndx.util

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.RelativeSizeSpan
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.absoluteValue

object TextUtils {

    fun getFraction(fraction: Number): Spannable {
        val string = SpannableString("1∕$fraction")
        string.setSpan(SuperScriptSpan(2), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // 1
        string.setSpan(SuperScriptSpan(3), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // ∕
        string.setSpan(RelativeSizeSpan(.75f), 2, fraction.toString().length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // x
        return string
    }

    // full needs to be String: Number "-0" becomes "0"
    fun getFraction(full: String, numerator: Number, denominator: Number): Spannable {
        val fullSanitized = full.replace("0", "")

        val string = SpannableString("$fullSanitized $numerator∕$denominator")
        val start = fullSanitized.length + 1
        val numeratorLength = numerator.toString().length
        string.setSpan(SuperScriptSpan(2), start, start + numeratorLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // numerator
        string.setSpan(SuperScriptSpan(3), start + numeratorLength, start + + numeratorLength + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // ∕
        string.setSpan(RelativeSizeSpan(.75f), start + numeratorLength + 1, start + denominator.toString().length + numeratorLength + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // x
        return string
    }

    private class SuperScriptSpan(val shiftFactor: Int) : MetricAffectingSpan() {

        override fun updateMeasureState(p: TextPaint) {
            p.baselineShift += (p.ascent() / shiftFactor).toInt()
            p.textSize = p.textSize * .6f
        }

        override fun updateDrawState(p: TextPaint?) {
            if (p != null) {
                p.baselineShift += (p.ascent() / shiftFactor).toInt()
                p.textSize = p.textSize * .6f
            }
        }
    }

    /**
     * takes a duration in millis and returns either h:mm:ss or mm:ss
     */
    fun Long?.toTimeString(withMillis: Boolean = false): String? {
        return if (this == null)
            null
        else {
            val signString = if (this < 0) "\u2212" else ""
            val hours = TimeUnit.MILLISECONDS.toHours(this).absoluteValue
            val minutes = TimeUnit.MILLISECONDS.toMinutes(
                this.absoluteValue - TimeUnit.HOURS.toMillis(hours)
            ).absoluteValue
            val seconds = TimeUnit.MILLISECONDS.toSeconds(
                this.absoluteValue - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
            ).absoluteValue
            val millis = this.absoluteValue - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds)

            var timeString = if (hours > 0)
                signString + String.format("%d:%02d:%02d", hours, minutes, seconds)
            else
                signString + String.format("%02d:%02d", minutes, seconds)

            if (withMillis) {
                // tenths of a second
                val tenth = abs(millis / 100 % 10)
                timeString += String.format(".%d", tenth)
            }

            return timeString
        }
    }

}
