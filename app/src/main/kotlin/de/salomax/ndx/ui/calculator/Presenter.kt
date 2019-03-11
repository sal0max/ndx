package de.salomax.ndx.ui.calculator

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.joaquimverges.helium.core.presenter.BasePresenter
import de.salomax.ndx.App
import de.salomax.ndx.data.ShutterSpeeds
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter : BasePresenter<State, Event>() {

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadShutterSpeeds() {
        App.database.prefDao().getEvSteps()
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

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadFilters() {
        // listen to filter changes and reset list
        App.database.filterDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { filters -> pushState(State.FiltersReady(filters)) }
    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadShowWarning() {
        App.database.prefDao()
                .getWarningEnabled()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { pushState(State.ShowWarning(it == "1")) }
    }

    override fun onViewEvent(event: Event) {}

}
