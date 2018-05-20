package de.salomax.ndx.ui.calculator

import com.joaquimverges.helium.presenter.BasePresenter
import de.salomax.ndx.App.Companion.context
import de.salomax.ndx.data.NdxDatabase
import de.salomax.ndx.data.ShutterSpeeds
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter : BasePresenter<State, Event>() {

    init {
        loadShutterSpeeds()
        loadFilters()
    }

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

    private fun loadFilters() {
        // listen to filter changes and reset list
        NdxDatabase.getInstance(context)
                .filterDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { filters -> pushState(State.FiltersReady(filters)) }
    }

    override fun onViewEvent(event: Event) {
        when (event) {
        }
    }

}
