package de.salomax.ndx.ui.billing

import android.os.Bundle
import android.widget.Button
import com.android.billingclient.api.*
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.util.Logger

/*
Billing workflow:
1. connect client:             #startConnection()
2. get details of all SKUs:    #querySkuDetailsAsync()
3. buy something:              #launchBillingFlow()
4. evaluate result of the buy: #onPurchasesUpdated()
  a) accept buy:               #acknowledgePurchase() -> for non-consumable
 */
class BillingActivity : BaseActivity(), PurchasesUpdatedListener {

   private lateinit var billingClient: BillingClient

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // setup billing client
      billingClient = BillingClient
            .newBuilder(this)
            .enablePendingPurchases()
            .setListener(this) // onPurchasesUpdated
            .build()

      // init view
      setContentView(R.layout.activity_billing)

      // title bar
      supportActionBar?.apply {
         setTitle(R.string.title_billing)
         setDisplayShowHomeEnabled(true)
         setDisplayHomeAsUpEnabled(true)
         setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
      }

      // button
      findViewById<Button>(R.id.btn_billing_buy).setOnClickListener {
         // buy() TODO
      }
   }

   override fun onSupportNavigateUp(): Boolean {
      onBackPressed()
      return true
   }

   override fun onStop() {
      super.onStop()
      billingClient.endConnection()
   }

   // 1. connect client
   private fun buy() {
      billingClient.startConnection(object : BillingClientStateListener {
         override fun onBillingSetupFinished(billingResponse: BillingResult) {
            if (billingResponse.responseCode == BillingClient.BillingResponseCode.OK) {
               Logger.log("startConnection        | The BillingClient is ready. You can query purchases here.")
               querySkuDetails()
            } else {
               Logger.log("startConnection        | Error: ${billingResponse.debugMessage}")
            }
         }

         override fun onBillingServiceDisconnected() {
            billingClient.startConnection(this)
            Logger.log("onBillingServiceDisconnected   | Disconnected")
         }
      })
   }

   // 2. get details of all SKUs
   private fun querySkuDetails() {
      if (billingClient.isReady) {
         val params = SkuDetailsParams
               .newBuilder()
               .setSkusList(listOf("android.test.purchased", "android.test.canceled", "android.test.refunded", "android.test.item_unavailable"))
//               .setSkusList(listOf("premium")) // non-consumable
               .setType(BillingClient.SkuType.INAPP)
               .build()
         billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
               Logger.log("querySkuDetailsAsync   | response: ${billingResult.responseCode} (OK)")
               launchBillingFlow(skuDetailsList)
            } else {
               Logger.log("querySkuDetailsAsync   | Can't do: ${billingResult.debugMessage}")
            }
         }
      } else {
         Logger.log("BillingClient not ready")
      }
   }

   // 3. buy something
   private fun launchBillingFlow(skuDetails: List<SkuDetails>) {
      for (skuDetail in skuDetails) {
         billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(skuDetail).build())
      }
   }

   // 4. evaluate result of the buy
   override fun onPurchasesUpdated(billingResponse: BillingResult?, purchases: MutableList<Purchase>?) {
      if (billingResponse?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
         for (purchase in purchases)
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
               // TODO Grant the item to the user...

               // ...and acknowledge the purchase if it hasn't already been acknowledged.
               if (!purchase.isAcknowledged) {
                  acknowledgePurchase(purchase.purchaseToken)
               }
            }
      } else {
         // Handle any error code (e. g. by a user cancelling the purchase flow)
         Logger.log("onPurchasesUpdated     | ${billingResponse?.responseCode}")
      }
   }

   // 4. a) accept buy
   private fun acknowledgePurchase(purchaseToken: String) {
      val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
      billingClient.acknowledgePurchase(params) { billingResult ->
         if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Logger.log("acknowledgePurchase    | success; billingResult: ${billingResult.responseCode}")
         } else {
            Logger.log("acknowledgePurchase    | failure; billingResult: ${billingResult.responseCode}")
         }

      }
   }

}
