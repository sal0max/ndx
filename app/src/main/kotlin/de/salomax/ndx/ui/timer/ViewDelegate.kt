package de.salomax.ndx.ui.timer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.github.guilhe.circularprogressview.CircularProgressView
import com.joaquimverges.helium.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import java.util.concurrent.TimeUnit

class ViewDelegate(inflater: LayoutInflater) : BaseViewDelegate<State, Event>(R.layout.activity_timer, inflater) {

    // all the views
    private val tvColon = view.findViewById<TextView>(R.id.colon)
    private val tvMinus = view.findViewById<TextView>(R.id.minus)
    private val tvMinutes = view.findViewById<TextView>(R.id.textMin)
    private val tvSeconds = view.findViewById<TextView>(R.id.textSec)
    private val tvHours = view.findViewById<TextView>(R.id.textHour)
    private val tvMillis = view.findViewById<TextView>(R.id.textMilli)
    private val circularProgress = view.findViewById<CircularProgressView>(R.id.progress)
    private val btnControl = view.findViewById<FloatingActionButton>(R.id.fab_pause)

    // final objects
    private val blinkAnimation = AnimationUtils.loadAnimation(context, R.anim.blink)
    private var alarmPlayer: MediaPlayer = MediaPlayer.create(
            context,
            R.raw.timer_expire_short,
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build(),
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).generateAudioSessionId())

    init {
        alarmPlayer.isLooping = true
    }

    override fun render(viewState: State) {
        when (viewState) {
            is State.UpdateText -> {
                updateText(viewState.millisTotal, viewState.millisOffset)
            }

            is State.Init -> {
                blinkText(false)
                btnControl.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.StartCountDown) }
            }

            is State.TimerRunning -> {
                btnControl.setImageResource(R.drawable.ic_pause_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.PauseTimer) }
                blinkText(false)
            }

            is State.TimerPaused -> {
                btnControl.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.StartCountDown) }
                blinkText(true)
            }

            is State.Alarm -> {
                alarmPlayer.start()
                circularProgress.startAnimation(blinkAnimation)
                btnControl.setImageResource(R.drawable.ic_stop_white_24dp)
                btnControl.setOnClickListener { pushEvent(Event.Finish) }
            }

            is State.Finish -> {
                alarmPlayer.stop()
                (context as AppCompatActivity).finish()
            }
        }
    }

    private fun blinkText(enabled: Boolean) {
        if (enabled) {
            tvColon.startAnimation(blinkAnimation)
            tvMinus.startAnimation(blinkAnimation)
            tvMinutes.startAnimation(blinkAnimation)
            tvSeconds.startAnimation(blinkAnimation)
            tvHours.startAnimation(blinkAnimation)
            tvMillis.startAnimation(blinkAnimation)
        } else {
            tvColon.clearAnimation()
            tvMinus.clearAnimation()
            tvMinutes.clearAnimation()
            tvSeconds.clearAnimation()
            tvHours.clearAnimation()
            tvMillis.clearAnimation()
        }
    }

    private fun updateText(totalMillis: Long, currentMillis: Long) {
        // every 0.1s
        if (currentMillis % 100 == 0L) {
            val remainingTime = totalMillis - currentMillis

            // minus
            tvMinus.visibility = if (remainingTime < 0) View.VISIBLE else View.GONE

            // hours
            val h = Math.abs(TimeUnit.MILLISECONDS.toHours(remainingTime))
            if (h > 0) {
                val sH = h.toString() + context.getString(R.string.unit_hours)
                if (tvHours.text != sH)
                    tvHours.text = sH
            } else if (tvHours.text != null) {
                tvHours.text = null
            }

            // minutes
            val min = Math.abs(TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60)
            val sMin = String.format("%02d", min)
            if (tvMinutes.text != sMin)
                tvMinutes.text = sMin

            // seconds
            val sec = Math.abs(TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60)
            val sSec = String.format("%02d", sec)
            if (tvSeconds.text != sSec)
                tvSeconds.text = sSec

            // tenths of a second
            val tenth = Math.abs(remainingTime / 100 % 10)
            tvMillis.text = String.format(".%d", tenth)

        }

        // every 0.01s / 10ms (100fps)
        if (totalMillis - currentMillis >= 0 && currentMillis % 10 == 0L) {
            circularProgress.max = totalMillis.toInt()
            circularProgress.progress = currentMillis.toFloat()
        }
    }

}
