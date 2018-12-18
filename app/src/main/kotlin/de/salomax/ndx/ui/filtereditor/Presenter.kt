package de.salomax.ndx.ui.filtereditor

import com.joaquimverges.helium.core.presenter.BasePresenter
import de.salomax.ndx.App.Companion.context
import de.salomax.ndx.data.NdxDatabase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter : BasePresenter<State, Event>() {

    override fun onViewEvent(event: Event) {
        when (event) {
            is Event.InsertOrUpdate -> {
                Single.fromCallable {
                    NdxDatabase.getInstance(context)
                            .filterDao()
                            .insert(event.filter)
                    pushState(State.Finish)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }
            is Event.Delete -> {
                Single.fromCallable {
                    NdxDatabase.getInstance(context)
                            .filterDao()
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
