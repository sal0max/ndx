package de.salomax.ndx.ui.timer

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.lifecycle.ViewModelProvider
import de.salomax.ndx.R
import de.salomax.ndx.databinding.ActivityTimerBinding
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.util.ManagedAlarmPlayer
import de.salomax.ndx.util.ManagedVibrator
import de.salomax.ndx.widget.BlinkAnimation
import java.util.concurrent.*
import kotlin.math.abs

class TimerActivity : BaseActivity() {

    private lateinit var binding: ActivityTimerBinding
    private lateinit var viewModel: TimerViewModel
    private lateinit var alarmPlayer: ManagedAlarmPlayer
    private lateinit var vibrator: ManagedVibrator

    private val blinkAnimation: Animation = BlinkAnimation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        alarmPlayer = ManagedAlarmPlayer(this)
        vibrator = ManagedVibrator(this)
        binding.btnReset.setOnClickListener {
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
                binding.btnReset.apply { visibility = View.GONE }
                binding.btnControl.apply {
                    setImageResource(R.drawable.ic_play_arrow_white_24dp)
                    setOnClickListener { viewModel.startTimer() }
                }
            }
        }

        // observe running timer
        viewModel.millisCurrent.observe(this, {
            refreshUi()
        })

        // observe status: runningPositive -> finished -> runningNegative
        viewModel.state.observe(this, { state ->
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (state) {
                TimerViewModel.State.RUNNING_POSITIVE -> {
                    // animations
                    blinkText(false)
                    // buttons
                    binding.btnReset.apply { visibility = View.GONE }
                    binding.btnControl.apply {
                        setImageResource(R.drawable.ic_pause_white_24dp)
                        setOnClickListener { viewModel.stopTimer() }
                    }
                }
                TimerViewModel.State.FINISHED -> {
                    // animations
                    binding.progress.startAnimation(blinkAnimation)
                    // alarms
                    if (viewModel.shouldAlarmBeep()) playAlarm()
                    if (viewModel.shouldAlarmVibrate()) vibrate()
                }
                TimerViewModel.State.RUNNING_NEGATIVE -> {
                    // animations
                    blinkText(false)
                    // buttons
                    binding.btnReset.apply { visibility = View.GONE }
                    binding.btnControl.apply {
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
                    binding.btnReset.apply { visibility = View.VISIBLE }
                    binding.btnControl.apply {
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
        if (enabled && binding.colon.animation == null) {
            binding.colon.startAnimation(blinkAnimation)
            binding.minus.startAnimation(blinkAnimation)
            binding.textMin.startAnimation(blinkAnimation)
            binding.textSec.startAnimation(blinkAnimation)
            binding.textHour.startAnimation(blinkAnimation)
            binding.textMilli.startAnimation(blinkAnimation)
        } else {
            binding.colon.clearAnimation()
            binding.minus.clearAnimation()
            binding.textMin.clearAnimation()
            binding.textSec.clearAnimation()
            binding.textHour.clearAnimation()
            binding.textMilli.clearAnimation()
        }
    }

    /**
     *
     */
    private fun refreshUi() {
        val remainingTime = viewModel.millisTotal!! - viewModel.millisCurrent.value!!

        // progress bar
        if (remainingTime >= 0) {
            if (binding.progress.max != viewModel.millisTotal!!.toInt())
                binding.progress.max = viewModel.millisTotal!!.toInt()
            binding.progress.setProgress(viewModel.millisCurrent.value!!.toFloat(), true, 100)
        } else if (binding.progress.max != 1) {
            binding.progress.progress = 1f
            binding.progress.max = 1
        }

        // negative
        binding.minus.visibility = if (remainingTime < 0) View.VISIBLE else View.GONE

        // tenths of a second
        val tenth = abs(remainingTime / 100 % 10)
        binding.textMilli.text = String.format(".%d", tenth)

        // seconds
        val sec = abs(TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60)
        val sSec = String.format("%02d", sec)
        if (binding.textSec.text != sSec)
            binding.textSec.text = sSec

        // minus
        val min = abs(TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60)
        val sMin = String.format("%02d", min)
        if (binding.textMin.text != sMin)
            binding.textMin.text = sMin

        // hours
        val h = abs(TimeUnit.MILLISECONDS.toHours(remainingTime))
        if (h > 0) {
            val sH = h.toString() + getString(R.string.unit_hours)
            if (binding.textHour.text != sH)
                binding.textHour.text = sH
        } else if (binding.textHour.text != null) {
            binding.textHour.text = null
        }
    }

}
