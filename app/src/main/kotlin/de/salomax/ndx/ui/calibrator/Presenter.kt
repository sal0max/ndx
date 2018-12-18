package de.salomax.ndx.ui.calibrator

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.joaquimverges.helium.core.presenter.BasePresenter
import de.salomax.ndx.App.Companion.context
import de.salomax.ndx.data.ISOs
import de.salomax.ndx.data.NdxDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Presenter : BasePresenter<State, Event>() {

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun loadShutterSpeeds() {
        NdxDatabase.getInstance(context).prefDao().getEvSteps()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { steps ->
                    val isos: ISOs = when (steps.toInt()) {
                        1 -> ISOs.FULL
                        2 -> ISOs.HALF
                        else -> {
                            ISOs.THIRD
                        }
                    }
                    pushState(State.ISOsReady(isos))
                }
    }

    override fun onViewEvent(event: Event) {}

}
