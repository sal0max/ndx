package de.salomax.ndx

import android.app.Application
import de.salomax.ndx.data.PrefDao
import leakcanary.LeakCanary
import shark.AndroidReferenceMatchers

@Suppress("unused")
class DebugApp : Application() {

   override fun onCreate() {
      super.onCreate()
      // Hide the leak display activity launcher icon
      LeakCanary.showLeakDisplayActivityLauncherIcon(false)
      // Leak in billing library - nothing we can do about it
      LeakCanary.config = LeakCanary.config.copy(
            referenceMatchers = AndroidReferenceMatchers.appDefaults +
                  AndroidReferenceMatchers.instanceFieldLeak(
                        className = "com.android.billingclient.api.BillingBroadcastManager",
                        fieldName = "zzb",
                        description = "PurchasesUpdatedListener from BillingClient leaks as it is not unregistered when endConnection() is called."
                  )
      )
      // always enable premium in debug
      PrefDao.getInstance(this).enablePremium(true)
   }
}
