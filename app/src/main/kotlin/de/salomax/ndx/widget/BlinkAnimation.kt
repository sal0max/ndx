package de.salomax.ndx.widget

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation

class BlinkAnimation : Animation() {

    init {
        interpolator = LinearInterpolator()
        duration = 1000
        repeatCount = Animation.INFINITE
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

        t.alpha = when {
            interpolatedTime < 0.2f -> 1f
            interpolatedTime < 0.6f -> 0f
            else -> 1f
        }
    }

}
