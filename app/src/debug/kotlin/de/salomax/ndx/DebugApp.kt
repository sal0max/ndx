package de.salomax.ndx

import leakcanary.LeakCanary

@Suppress("unused")
class DebugApp : App() {

   override fun onCreate() {
      super.onCreate()
      // Hide the leak display activity launcher icon
      LeakCanary.showLeakDisplayActivityLauncherIcon(false)
      // Leak in billing library
      // LeakCanary.config = LeakCanary.config.copy(
      //       referenceMatchers = AndroidReferenceMatchers.appDefaults +
      //             AndroidReferenceMatchers.instanceFieldLeak(
      //                   className = "com.android.billingclient.api.BillingBroadcastManager",
      //                   fieldName = "zzb",
      //                   description = "PurchasesUpdatedListener from BillingClient leaks as it is not unregistered when endConnection() is called."
      //             )
      // )
   }
}
