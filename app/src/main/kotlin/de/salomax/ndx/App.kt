package de.salomax.ndx

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric

class App : Application() {

    companion object {
        lateinit var context: App
            private set
        lateinit var analytics: FirebaseAnalytics
            private set
    }

    override fun onCreate() {
        super.onCreate()

        analytics = FirebaseAnalytics.getInstance(this)
        context = this

        // leakCanary TODO: remove?
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        // crashlytics
        Fabric.with(this, Crashlytics())
    }
}
