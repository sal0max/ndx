package de.salomax.ndx.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

@Suppress("unused")
class ManagedVibrator private constructor(context: Context) {

    companion object : SingletonHolder<ManagedVibrator, Context>(::ManagedVibrator)

    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    var isVibrating = false
        private set

    fun hasVibrator(): Boolean {
        return vibrator.hasVibrator()
    }

    fun vibrate(pattern: LongArray) {
        if (!isVibrating) {
            isVibrating = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, 0)
            }
        }
    }

    fun stop() {
        vibrator.cancel()
        isVibrating = false
    }

}
