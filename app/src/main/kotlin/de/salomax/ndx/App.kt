package de.salomax.ndx

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class App : Application() {

    companion object {
        lateinit var context: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        Fabric.with(this, Crashlytics())
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}
