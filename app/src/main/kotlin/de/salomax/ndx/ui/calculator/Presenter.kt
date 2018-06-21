package de.salomax.ndx.ui.calculator

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import com.joaquimverges.helium.presenter.BasePresenter
import de.salomax.ndx.App.Companion.context
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.ShutterSpeeds
import de.salomax.ndx.ui.timer.TimerActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter : BasePresenter<State, Event>() {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadShutterSpeeds() {
        NdxDatabase.getInstance(context).prefDao().getEvSteps()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { steps ->
                    val speeds: ShutterSpeeds = when (steps.toInt()) {
                        1 -> ShutterSpeeds.FULL
                        2 -> ShutterSpeeds.HALF
                        else -> {
                            ShutterSpeeds.THIRD
                        }
                    }
                    pushState(State.ShutterSpeedsReady(speeds))
                }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadFilters() {
        // listen to filter changes and reset list
        NdxDatabase.getInstance(context)
                .filterDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { filters -> pushState(State.FiltersReady(filters)) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadShowWarning() {
        NdxDatabase.getInstance(context)
                .prefDao()
                .getWarningEnabled()
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { pushState(State.ShowWarning(it == "1")) }
    }

    override fun onViewEvent(event: Event) {
        when (event) {
            is Event.StartTimer -> {
                val i = Intent(context, TimerActivity().javaClass)
                i.putExtra("MILLIS", event.micro / 1_000)
                context.startActivity(i)
            }
        }
    }

}
