package de.salomax.ndx.ui.filterpouch

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.joaquimverges.helium.core.presenter.BasePresenter
import de.salomax.ndx.App
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter : BasePresenter<State, Event>() {

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadFilters() {
        App.database.filterDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { filters ->
                    pushState(State.FiltersReady(filters))
                }
    }

    override fun onViewEvent(event: Event) {
        when (event) {
            is Event.FilterClicked -> pushState(State.EditFilter(event.filter))
            is Event.AddNewClicked -> pushState(State.AddNewFilter)
            is Event.ReceivedDeletionResult -> pushState(State.ShowUndoDeletionSnackbar(event.filter))
            is Event.RestoreFilter -> {
                Single.fromCallable {
                    App.database.filterDao()
                            .insert(event.filter)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }
        }
    }

}
