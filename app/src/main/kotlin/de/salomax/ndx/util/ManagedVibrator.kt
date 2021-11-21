package de.salomax.ndx.util

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

@Suppress("unused")
class ManagedVibrator constructor(context: Context) {

    private val vibrator  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =  context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    }



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
