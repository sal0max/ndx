package de.salomax.ndx

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric

class App : Application() {

    companion object {
        lateinit var analytics: FirebaseAnalytics
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // init leakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
        }

        // provide app-wide analytics
        analytics = FirebaseAnalytics.getInstance(this)
        // init crashlytics
        Fabric.with(this, Crashlytics())
    }
}
