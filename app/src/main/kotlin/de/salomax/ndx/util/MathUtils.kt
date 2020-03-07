package de.salomax.ndx.util

import java.util.*
import kotlin.math.roundToLong

object MathUtils {

    fun multiply(a: Long?, b: Long?): Long? {
        return if (a == null || b == null)
            null
        // check for overflow
        else if (a != 0L && b > Long.MAX_VALUE / a)
            null
        else
            a * b
    }

    fun factor2fstop(factor: Double): String =
            String.format(Locale.US, "%.1f", kotlin.math.log2(factor))

    fun factor2fstopRounded(factor: Number): String =
            kotlin.math.log2(factor.toDouble()).roundToLong().toString()

    fun factor2nd(factor: Number): String =
            String.format(Locale.US, "%.1f", kotlin.math.log10(factor.toDouble()))
}
