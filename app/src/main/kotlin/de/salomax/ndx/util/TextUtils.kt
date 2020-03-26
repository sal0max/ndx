package de.salomax.ndx.util

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.RelativeSizeSpan

object TextUtils {

    fun getFraction(fraction: Number): Spannable {
        val string = SpannableString("1∕$fraction")
        string.setSpan(SuperScriptSpan(2), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // 1
        string.setSpan(SuperScriptSpan(3), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // ∕
        string.setSpan(RelativeSizeSpan(.75f), 2, fraction.toString().length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // x
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


}
