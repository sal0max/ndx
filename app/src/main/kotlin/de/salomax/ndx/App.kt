package de.salomax.ndx

import android.app.Application
import com.android.billingclient.api.*
import de.salomax.ndx.data.PrefDao

@Suppress("unused")
class App : Application() {

   private lateinit var billingClient: BillingClient

   override fun onCreate() {
      super.onCreate()
      checkIfPremium()
   }

   private fun checkIfPremium() {
      billingClient = BillingClient.newBuilder(applicationContext)
            .setListener { _, _ -> } // needed
            .enablePendingPurchases() // needed
            .build()
      if (!billingClient.isReady) {
         billingClient.startConnection(
               object : BillingClientStateListener {
                  override fun onBillingSetupFinished(billingResult: BillingResult) {
                     // check if a user has purchased premium before
                     val result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                     // yes -> enable premium | no -> disable it, to be sure
                     PrefDao.getInstance(applicationContext).enablePremium(result.purchasesList != null)
                     // NOTE:
                     // Big problem! Refunded purchases will get reported still as valid, here.
                     // Seems to be a known flaw with the billing library. Will just leave it that
                     // way and see how it works out.
                  }

                  override fun onBillingServiceDisconnected() {}
               }
         )
      }
   }

}
