package de.salomax.ndx.util

import android.util.Log

@Suppress("unused")
object Logger {

    private const val tag = "!!! NDx !!!"

    fun log(o: Any) {
        Log.w(tag, o.toString())
    }

}
