package de.salomax.ndx.ui.billing

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.google.android.material.snackbar.Snackbar
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.util.Logger
import kotlinx.android.synthetic.main.activity_billing.*

/**
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
         buy()
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
            when (billingResponse.responseCode) {
               BillingResponseCode.OK -> {
                  // The BillingClient is ready. You can query purchases here.
                  Logger.log("startConnection        | The BillingClient is ready. You can query purchases here.")
                  querySkuDetails()
               }
               BillingResponseCode.ITEM_ALREADY_OWNED -> {
                  Logger.log("querySkuDetailsAsync   | item already owned: premium granted")
                  ViewModelProvider(this@BillingActivity).get(BillingViewModel::class.java).enablePremium()
                  thanksAndFinish()
               }
               else -> {
                  Snackbar.make(btn_billing_buy, billingResponse.debugMessage, Snackbar.LENGTH_LONG).setBackgroundTint(getColor(android.R.color.holo_red_light)).show()
                  Logger.log("startConnection        | Error: ${billingResponse.debugMessage} (${billingResponse.responseCode})")
               }
            }
         }

         override fun onBillingServiceDisconnected() {
            // try to restart the connection on the next request to Google Play by calling the startConnection() method.
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
         billingClient?.querySkuDetailsAsync(params) { billingResult, skuDetails ->
            when (billingResult.responseCode) {
               BillingResponseCode.OK -> {
                  Logger.log("querySkuDetailsAsync   | response: ${billingResult.responseCode} -> launchBillingFLow")
                  launchBillingFlow(skuDetails as MutableList<SkuDetails>)
               }
               BillingResponseCode.ITEM_ALREADY_OWNED -> {
                  Logger.log("querySkuDetailsAsync   | item already owned: premium granted")
                  ViewModelProvider(this).get(BillingViewModel::class.java).enablePremium()
                  thanksAndFinish()
               }
               else -> {
                  Logger.log("querySkuDetailsAsync   | Can't do: ${billingResult.debugMessage}")
               }
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
      }
   }

   // 4. evaluate result of the buy...
   private fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
      when {
         billingResult?.responseCode == BillingResponseCode.OK && purchases != null -> {
            for (purchase in purchases)
               if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                  Logger.log("onPurchasesUpdated     | premium granted")
                  ViewModelProvider(this).get(BillingViewModel::class.java).enablePremium()
                  // ...and acknowledge the purchase if it hasn't already been acknowledged.
                  if (!purchase.isAcknowledged) {
                     acknowledgePurchase(purchase.purchaseToken)
                  }
               }
         }
         billingResult?.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED && purchases != null -> {
            Logger.log("querySkuDetailsAsync   | item already owned: premium granted")
            ViewModelProvider(this).get(BillingViewModel::class.java).enablePremium()
            thanksAndFinish()
         }
         else -> {
            // handle any error code (e. g. by a user cancelling the purchase flow)
            Logger.log("onPurchasesUpdated     | ${billingResult?.responseCode}")
         }
      }
   }

   // 4. a) accept buy
   private fun acknowledgePurchase(purchaseToken: String) {
      val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
      billingClient?.acknowledgePurchase(params) { billingResult ->
         when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
               thanksAndFinish()
            }
            BillingResponseCode.ITEM_ALREADY_OWNED -> {
               Logger.log("querySkuDetailsAsync   | item already owned: premium granted")
               ViewModelProvider(this).get(BillingViewModel::class.java).enablePremium()
               thanksAndFinish()
            }
            else -> {
               Logger.log("acknowledgePurchase    | failure; billingResult: ${billingResult.responseCode}")
            }
         }
      }
   }

   private fun thanksAndFinish() {
      Toast.makeText(applicationContext, getString(R.string.billing_thanks_for_buying), Toast.LENGTH_LONG).show()
      onSupportNavigateUp()
   }

}
