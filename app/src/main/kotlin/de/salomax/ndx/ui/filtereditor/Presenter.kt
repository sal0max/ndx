package de.salomax.ndx.ui.filtereditor

import android.os.Bundle
import com.joaquimverges.helium.core.presenter.BasePresenter
import de.salomax.ndx.App
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter : BasePresenter<State, Event>() {

    override fun onViewEvent(event: Event) {
        when (event) {
            is Event.InsertOrUpdate -> {
                Single.fromCallable {
                    App.database.filterDao()
                            .insert(event.filter)
                    pushState(State.Finish)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                // analytics
                val params = Bundle()
                params.putString("filter_name", event.filter.name.take(100))
                App.analytics.logEvent("filter_inserted_or_updated", params)
            }
            is Event.Delete -> {
                Single.fromCallable {
                    App.database.filterDao()
                            .delete(event.id)
                    pushState(State.DeleteAndFinish)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }
            is Event.Cancel -> pushState(State.Finish)
            is Event.Calibrated -> pushState(State.Calibrated(event.factor))
        }
    }

}
