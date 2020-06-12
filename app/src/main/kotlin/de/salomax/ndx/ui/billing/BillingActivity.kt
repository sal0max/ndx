package de.salomax.ndx.ui.billing

import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
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
class BillingActivity : BaseActivity() {

   private var billingClient: BillingClient? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // init view
      setContentView(R.layout.activity_billing)

      // title bar
      supportActionBar?.apply {
         setTitle(R.string.title_billing)
         setDisplayShowHomeEnabled(true)
         setDisplayHomeAsUpEnabled(true)
         setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
      }

      // setup billing client
      billingClient = BillingClient
            .newBuilder(applicationContext)
            .enablePendingPurchases()
            .setListener(this::onPurchasesUpdated) // onPurchasesUpdated
            .build()

      // button
      findViewById<Button>(R.id.btn_billing_buy).setOnClickListener {
         ViewModelProvider(this).get(BillingViewModel::class.java).enablePremium()
//         buy() // TODO
      }
   }

   override fun onSupportNavigateUp(): Boolean {
      onBackPressed()
      return true
   }

   override fun onDestroy() {
      super.onDestroy()
      billingClient?.endConnection()
      billingClient = null
   }

   // 1. connect client
   private fun buy() {
      // connect
      billingClient?.startConnection(object : BillingClientStateListener {
         override fun onBillingSetupFinished(billingResponse: BillingResult) {
            if (billingResponse.responseCode == BillingClient.BillingResponseCode.OK) {
               Logger.log("startConnection        | The BillingClient is ready. You can query purchases here.")
               querySkuDetails()
            } else {
               Logger.log("startConnection        | Error: ${billingResponse.debugMessage}")
            }
         }

         override fun onBillingServiceDisconnected() {
            billingClient?.startConnection(this)
            Logger.log("onBillingServiceDisconnected   | Disconnected")
         }
      })
   }

   // 2. get details of all SKUs
   private fun querySkuDetails() {
      if (billingClient?.isReady == true) {
         val params = SkuDetailsParams
               .newBuilder()
               .setSkusList(listOf("premium")) // non-consumable
               .setType(BillingClient.SkuType.INAPP)
               .build()
         billingClient?.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
               Logger.log("querySkuDetailsAsync   | response: ${billingResult.responseCode} (OK)")
               launchBillingFlow(skuDetailsList as MutableList<SkuDetails>)
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
         Logger.log("launchBillingFlow      | ${skuDetail.originalJson}")
         billingClient?.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(skuDetail).build())

         // temp
//         billingClient?.consumeAsync(ConsumeParams.newBuilder()
//               .setPurchaseToken("klkmnfhjhaobpfaifhmmcmjf.AO-J1Ow_ovlJ60rrhvwFjjcI7DyhMTOb7QK1aRAaaPHIgRtryvQ8F3p3UPiz-F7KScZiQ5Bv60PmYVWL-3gZRROpWI6CyJysvcZL5xte2d_hVozzTiRzGWk")
//               .build()) { billingResult, s ->
//            Logger.log("newnew | ${billingResult.responseCode}")
//         }
      }
   }

   // 4. evaluate result of the buy...
   private fun onPurchasesUpdated(billingResponse: BillingResult?, purchases: MutableList<Purchase>?) {
      if (billingResponse?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
         for (purchase in purchases)
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
               Logger.log("onPurchasesUpdated     | premium granted")
               ViewModelProvider(this).get(BillingViewModel::class.java).enablePremium()
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
      billingClient?.acknowledgePurchase(params) { billingResult ->
         if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Logger.log("acknowledgePurchase    | success; billingResult: ${billingResult.responseCode}")
         } else {
            Logger.log("acknowledgePurchase    | failure; billingResult: ${billingResult.responseCode}")
         }

      }
   }

}
