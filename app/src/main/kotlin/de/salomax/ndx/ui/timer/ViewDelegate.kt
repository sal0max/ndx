package de.salomax.ndx.ui.timer

import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.TextView
import com.github.guilhe.circularprogressview.CircularProgressView
import com.joaquimverges.helium.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import android.view.animation.AnimationUtils

class ViewDelegate(inflater: LayoutInflater) : BaseViewDelegate<State, Event>(R.layout.activity_timer, inflater) {

    private val centerSeparator = view.findViewById<TextView>(R.id.guideline)
    private val tvMinutes = view.findViewById<TextView>(R.id.textMin)
    private val tvSeconds = view.findViewById<TextView>(R.id.textSec)
    private val tvHours = view.findViewById<TextView>(R.id.textHour)
    private val tvMillis = view.findViewById<TextView>(R.id.textMilli)
    private val progressView = view.findViewById<CircularProgressView>(R.id.progress)
    private val fabPause = view.findViewById<FloatingActionButton>(R.id.fab_pause)

    private var subscription: Disposable? = null

    private var millisTotal = 0L
    private var millisOffset = 0L // maybe AtomicLong

    override fun render(viewState: State) {
        when (viewState) {
            is State.PopulateTimer -> {
                // stop possibly running timer
                subscription?.dispose()
                //
                millisTotal = viewState.millisTotal
                millisOffset = viewState.millisOffset
                // pause button
                fabPause.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                fabPause.setOnClickListener { pushEvent(Event.RunTimer) }
                // populate ui
                centerSeparator.clearAnimation()
                tvMinutes.clearAnimation()
                tvSeconds.clearAnimation()
                tvHours.clearAnimation()
                tvMillis.clearAnimation()
                updateUi(viewState.millisTotal, viewState.millisOffset)
            }
            is State.RunTimer -> {
                // pause button
                fabPause.setImageResource(R.drawable.ic_pause_white_24dp)
                fabPause.setOnClickListener { pushEvent(Event.PauseTimer) }
                // stop blinking
                centerSeparator.clearAnimation()
                tvMinutes.clearAnimation()
                tvSeconds.clearAnimation()
                tvHours.clearAnimation()
                tvMillis.clearAnimation()
                // start countdown
                subscription = Observable.interval(10, TimeUnit.MILLISECONDS)
                        .map { 10L }
                        .subscribeOn(Schedulers.io()) // Run on a background thread
                        .observeOn(AndroidSchedulers.mainThread()) // Be notified on the main thread
                        .subscribe {
                            if (millisTotal - millisOffset > 0)
                                updateUi(millisTotal, millisOffset)
                            else
                                pushEvent(Event.Alarm)
                            millisOffset += it
                        }
            }
            is State.PauseTimer -> {
                // pause button
                fabPause.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                fabPause.setOnClickListener { pushEvent(Event.RunTimer) }
                // start blinking
                val anim = AnimationUtils.loadAnimation(context, R.anim.blink)
                centerSeparator.startAnimation(anim)
                tvMinutes.startAnimation(anim)
                tvSeconds.startAnimation(anim)
                tvHours.startAnimation(anim)
                tvMillis.startAnimation(anim)
                //
                subscription?.dispose()
            }
            is State.Alarm -> {
                pushEvent(Event.Alarm)
                subscription?.dispose()
            }
            is State.Finish -> {
                subscription?.dispose()
                (context as AppCompatActivity).finish()
            }
        }
    }

    private fun updateUi(totalMillis: Long, currentMillis: Long) {
        // every 0.1s
        if (currentMillis % 100 == 0L) {
            val remainingTime = totalMillis - currentMillis

            // hours
            val h = TimeUnit.MILLISECONDS.toHours(remainingTime)
            if (h > 0) {
                val sH = h.toString() + context.getString(R.string.unit_hours)
                if (tvHours.text != sH)
                    tvHours.text = sH
            } else if (tvHours.text != null) {
                tvHours.text = null
            }

            // minutes
            val min = TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60
            val sMin = String.format("%02d", min)
            if (tvMinutes.text != sMin)
                tvMinutes.text = sMin

            // seconds
            val sec = TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60
            val sSec = String.format("%02d", sec)
            if (tvSeconds.text != sSec)
                tvSeconds.text = sSec

            // tenths of a second
            val tenth = remainingTime / 100 % 10
            tvMillis.text = String.format(".%d", tenth)

        }

        // every 0.01s / 10ms (100fps)
        if (currentMillis % 10 == 0L) {
            progressView.max = totalMillis.toInt()
            progressView.progress = currentMillis.toFloat()
        }
    }

}
