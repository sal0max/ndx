package de.salomax.ndx.ui.timer

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.view.animation.Animation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import de.salomax.ndx.R
import de.salomax.ndx.databinding.ActivityTimerBinding
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.calculator.CalculatorActivity
import de.salomax.ndx.util.TextUtils.toTimeString
import de.salomax.ndx.widget.BlinkAnimation
import java.util.concurrent.*
import kotlin.math.abs

class TimerActivity : BaseActivity(), ServiceConnection {

    private lateinit var viewBinding: ActivityTimerBinding
    private lateinit var service: TimerService
    private var isServiceBound: Boolean = false
    private val blinkAnimation: Animation = BlinkAnimation()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // only have to ask on API level >= 33
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionRationale()
                    } else {
                        showSettingDialog()
                    }
                }
            }
        }

    // service
    override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
        val binder = iBinder as TimerService.LocalBinder
        service = binder.getService()
        isServiceBound = true
        intent.extras?.let {
            // first start: initialize countdown
            if (intent.extras != null && intent.extras?.getLong("MILLIS") != 0L) {
                val millis = intent.extras!!.getLong("MILLIS")
                service.initCountdown(millis)
                viewBinding.totalTime.text = millis.toTimeString(true)
            }
        }
        observe()
    }

    override fun onServiceDisconnected(arg0: ComponentName) {
        isServiceBound = false
    }

    override fun onStart() {
        super.onStart()
        // start service and bind to it
        val intent = Intent(this, TimerService::class.java)
        ContextCompat.startForegroundService(this, intent)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        viewBinding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.btnReset.setOnClickListener { service.resetCountdown() }

        // title bar
        setTitle(R.string.title_timer)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }

        // ask for notification permission
        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(this)
            isServiceBound = false
        }
    }

    private fun observe() {
        // observe running timer
        service.millisCurrent.observe(this) {
            refreshUi()
        }

        // observe status: stopped -> runningPositive/paused -> -> runningNegative
        service.state.observe(this) { state ->
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (state) {
                TimerService.State.WAITING -> {
                    // hide reset
                    viewBinding.btnReset.apply { visibility = View.GONE }
                    // start
                    viewBinding.btnControl.apply {
                        setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        setOnClickListener { service.runCountdown() }
                    }
                }
                TimerService.State.RUNNING_POSITIVE -> {
                    // animations
                    blinkText(false)
                    // hide reset
                    viewBinding.btnReset.apply { visibility = View.GONE }
                    // pause
                    viewBinding.btnControl.apply {
                        setImageResource(R.drawable.ic_pause_white_24dp)
                        setOnClickListener { service.pauseCountdown() }
                    }
                }
                TimerService.State.PAUSED -> {
                    // animations
                    blinkText(true)
                    // show reset
                    viewBinding.btnReset.apply { visibility = View.VISIBLE }
                    // continue
                    viewBinding.btnControl.apply {
                        setImageResource(R.drawable.ic_play_arrow_white_24dp)
                        setOnClickListener { service.runCountdown() }
                    }

                }
                TimerService.State.RUNNING_NEGATIVE -> {
                    // animations
                    blinkText(false)
                    if (viewBinding.progress.animation == null) {
                        viewBinding.progress.startAnimation(blinkAnimation)
                    }
                    // hide reset
                    viewBinding.btnReset.apply { visibility = View.GONE }
                    // stop
                    viewBinding.btnControl.apply {
                        setImageResource(R.drawable.ic_stop_white_24dp)
                        setOnClickListener {
                            service.stopCountdown()
                            // exit
                            stopService(Intent(context, TimerService::class.java))
                            finish()
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        // kill service
        service.stopCountdown()
        // go back to CalculatorActivity or launch it, if it isn't on the stack anymore
        val intent = Intent(this, CalculatorActivity().javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun blinkText(enabled: Boolean) {
        if (enabled && viewBinding.colon.animation == null) {
            viewBinding.colon.startAnimation(blinkAnimation)
            viewBinding.minus.startAnimation(blinkAnimation)
            viewBinding.textMin.startAnimation(blinkAnimation)
            viewBinding.textSec.startAnimation(blinkAnimation)
            viewBinding.textHour.startAnimation(blinkAnimation)
            viewBinding.textMilli.startAnimation(blinkAnimation)
        } else {
            viewBinding.colon.clearAnimation()
            viewBinding.minus.clearAnimation()
            viewBinding.textMin.clearAnimation()
            viewBinding.textSec.clearAnimation()
            viewBinding.textHour.clearAnimation()
            viewBinding.textMilli.clearAnimation()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUi() {
        val remainingTime = service.millisTotal - service.millisCurrent.value!!

        // progress bar
        if (remainingTime >= 0) {
            if (viewBinding.progress.max != service.millisTotal.toInt())
                viewBinding.progress.max = service.millisTotal.toInt()
            viewBinding.progress.setProgress(service.millisCurrent.value!!.toFloat(), true, 100)
        } else if (viewBinding.progress.max != 1) {
            viewBinding.progress.setProgress(1f)
            viewBinding.progress.max = 1
        }

        // negative
        viewBinding.minus.visibility = if (remainingTime < 0) View.VISIBLE else View.GONE

        // tenths of a second
        val tenth = abs(remainingTime / 100 % 10)
        viewBinding.textMilli.text = ".$tenth"

        // seconds
        val sec = abs(TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60)
        val sSec = sec.toString().padStart(2, '0')
        if (viewBinding.textSec.text != sSec)
            viewBinding.textSec.text = sSec

        // minus
        val min = abs(TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60)
        val sMin = min.toString().padStart(2, '0')
        if (viewBinding.textMin.text != sMin)
            viewBinding.textMin.text = sMin

        // hours
        val h = abs(TimeUnit.MILLISECONDS.toHours(remainingTime))
        if (h > 0) {
            val sH = h.toString() + getString(R.string.unit_hours)
            if (viewBinding.textHour.text != sH)
                viewBinding.textHour.text = sH
        } else if (viewBinding.textHour.text != null) {
            viewBinding.textHour.text = null
        }
    }

    private fun showNotificationPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle(R.string.ask_notification_permission_title)
            .setMessage(R.string.ask_notification_permission_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showSettingDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.ask_notification_permission_title)
            .setMessage(R.string.ask_notification_permission_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

}
