package de.salomax.ndx.ui.timer

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.salomax.ndx.R
import de.salomax.ndx.data.Pref
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.util.ManagedAlarmPlayer
import de.salomax.ndx.util.ManagedVibrator
import de.salomax.ndx.widget.BlinkAnimation
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.concurrent.*
import kotlin.math.abs

class TimerActivity : BaseActivity() {

    private lateinit var viewModel: TimerViewModel
    private lateinit var alarmPlayer: ManagedAlarmPlayer
    private lateinit var vibrator: ManagedVibrator

    private val blinkAnimation: Animation = BlinkAnimation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        setContentView(R.layout.activity_timer)
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        alarmPlayer = ManagedAlarmPlayer(this)
        vibrator = ManagedVibrator(this)
        btn_reset.setOnClickListener {
            viewModel.stopTimer()
            viewModel.millisCurrent.value = 0
        }

        // title bar
        setTitle(R.string.title_timer)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }

        // init timer
        intent.extras?.let {
            // totalMillis
            if (viewModel.millisTotal == null) {
                viewModel.millisTotal = it.getLong("MILLIS")
                // first start: init
                btn_reset.apply { visibility = View.GONE }
                btn_control.apply {
                    setImageResource(R.drawable.ic_play_arrow_white_24dp)
                    setOnClickListener { viewModel.startTimer() }
                }
            }
        }

        // observe running timer
        viewModel.millisCurrent.observe(this, Observer {
            refreshUi()
        })

        // observe status: runningPositive -> finished -> runningNegative
        viewModel.state.observe(this, Observer { state ->
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (state) {
                TimerViewModel.State.RUNNING_POSITIVE -> {
                    // animations
                    blinkText(false)
                    // buttons
                    btn_reset.apply { visibility = View.GONE }
                    btn_control.apply {
                        setImageResource(R.drawable.ic_pause_white_24dp)
                        setOnClickListener { viewModel.stopTimer() }
                    }
                }
                TimerViewModel.State.FINISHED -> {
                    // animations
                    progress.startAnimation(blinkAnimation)
                    // alarms
                    viewModel.prefs.observe(this, Observer { prefs ->
                        prefs?.let {
                            for (pref in it)
                                when (pref.key) {
                                    Pref.ALARM_BEEP -> if (pref.value == "1") playAlarm()
                                    Pref.ALARM_VIBRATE -> if (pref.value == "1") vibrate()
                                }
                        }
                    })
                }
                TimerViewModel.State.RUNNING_NEGATIVE -> {
                    // animations
                    blinkText(false)
                    // buttons
                    btn_reset.apply { visibility = View.GONE }
                    btn_control.apply {
                        setImageResource(R.drawable.ic_stop_white_24dp)
                        setOnClickListener {
                            alarmPlayer.stop()
                            vibrator.stop()
                            viewModel.stopTimer()
                            finish()
                        }
                    }
                }
                TimerViewModel.State.PAUSED -> {
                    // animations
                    blinkText(true)
                    // buttons
                    btn_reset.apply { visibility = View.VISIBLE }
                    btn_control.apply {
                        setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        setOnClickListener { viewModel.startTimer() }
                    }

                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("ALARM_PLAYING", alarmPlayer.isPlaying)
        outState.putBoolean("VIBRATOR_VIBRATING", vibrator.isVibrating)
        alarmPlayer.stop()
        vibrator.stop()

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.getBoolean("ALARM_PLAYING")) playAlarm()
        if (savedInstanceState.getBoolean("VIBRATOR_VIBRATING")) vibrate()
    }

    /**
     *
     */
    private fun playAlarm() {
        alarmPlayer.play(R.raw.timer_expire_short)
    }

    /**
     *
     */
    private fun vibrate() {
        val vibratorPattern = longArrayOf(0, 100, 100, 200, 500)
        vibrator.vibrate(vibratorPattern)
    }

    /**
     *
     */
    private fun blinkText(enabled: Boolean) {
        if (enabled && colon.animation == null) {
            colon.startAnimation(blinkAnimation)
            minus.startAnimation(blinkAnimation)
            textMin.startAnimation(blinkAnimation)
            textSec.startAnimation(blinkAnimation)
            textHour.startAnimation(blinkAnimation)
            textMilli.startAnimation(blinkAnimation)
        } else {
            colon.clearAnimation()
            minus.clearAnimation()
            textMin.clearAnimation()
            textSec.clearAnimation()
            textHour.clearAnimation()
            textMilli.clearAnimation()
        }
    }

    /**
     *
     */
    private fun refreshUi() {
        val remainingTime = viewModel.millisTotal!! - viewModel.millisCurrent.value!!

        // progress bar
        if (remainingTime >= 0) {
            if (progress.max != viewModel.millisTotal!!.toInt())
                progress.max = viewModel.millisTotal!!.toInt()
            progress.setProgress(viewModel.millisCurrent.value!!.toFloat(), true, 100)
        } else if (progress.max != 1) {
            progress.progress = 1f
            progress.max = 1
        }

        // negative
        minus.visibility = if (remainingTime < 0) View.VISIBLE else View.GONE

        // tenths of a second
        val tenth = abs(remainingTime / 100 % 10)
        textMilli.text = String.format(".%d", tenth)

        // seconds
        val sec = abs(TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60)
        val sSec = String.format("%02d", sec)
        if (textSec.text != sSec)
            textSec.text = sSec

        // minus
        val min = abs(TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60)
        val sMin = String.format("%02d", min)
        if (textMin.text != sMin)
            textMin.text = sMin

        // hours
        val h = abs(TimeUnit.MILLISECONDS.toHours(remainingTime))
        if (h > 0) {
            val sH = h.toString() + getString(R.string.unit_hours)
            if (textHour.text != sH)
                textHour.text = sH
        } else if (textHour.text != null) {
            textHour.text = null
        }
    }

}
