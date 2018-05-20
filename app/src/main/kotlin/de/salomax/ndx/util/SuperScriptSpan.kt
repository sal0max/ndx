package de.salomax.ndx.util

import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class SuperScriptSpan(val shiftFactor : Int) : MetricAffectingSpan() {

    override fun updateMeasureState(p: TextPaint?) {
        if (p != null) {
            p.baselineShift += (p.ascent() / shiftFactor).toInt()
            p.textSize = p.textSize * .6f
        }
    }

    override fun updateDrawState(p: TextPaint?) {
        if (p != null) {
            p.baselineShift += (p.ascent() / shiftFactor).toInt()
            p.textSize = p.textSize * .6f
        }
    }
}
