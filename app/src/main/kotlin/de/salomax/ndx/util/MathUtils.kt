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

    /**
     * Converts a filter factor to the equivalent f-stop reduction. Also rounds to one fraction.
     *
     * "1024" will be converted to "10.0"
     */
    fun factor2fstop(factor: Number): String =
            String.format(Locale.US, "%.1f", kotlin.math.log2(factor.toDouble()))

    /**
     * Converts a filter factor to the equivalent f-stop reduction.
     * Rounds to one fraction, if there is a fraction.
     * If there is no fraction, only the full number is returned.
     *
     * - "1024" will be converted to "10"
     * - "3" will be converted to "1.6"
     */
    fun factor2fstopRounded(factor: Number): String {
        return factor2fstop(factor).substringBefore(".0")
    }

    /**
     * Converts a filter factor to the equivalent nd number. Also rounds to one fraction.
     *
     * "1024" will be converted to "3.0"
     */
    fun factor2nd(factor: Number): String =
            String.format(Locale.US, "%.1f", kotlin.math.log10(factor.toDouble()))
}
