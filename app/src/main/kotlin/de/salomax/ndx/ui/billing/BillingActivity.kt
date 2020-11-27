package de.salomax.ndx.ui.billing

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity

/**
Billing workflow:
1. connect client:             #startConnection()
2. get details of all SKUs:    #querySkuDetailsAsync()
3. buy something:              #launchBillingFlow()
4. evaluate result of the buy: #onPurchasesUpdated()
  a) accept buy:               #acknowledgePurchase() -> for non-consumable
 */
class BillingActivity : BaseActivity(), PurchasesUpdatedListener, BillingClientStateListener,
      SkuDetailsResponseListener, AcknowledgePurchaseResponseListener {

   private var billingClient: BillingClient? = null

   @Suppress("PrivatePropertyName")
   private val SKU_PREMIUM = "premium"

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
            .setListener(this::onPurchasesUpdated)
            .build()

      // button
      findViewById<Button>(R.id.btn_billing_buy).setOnClickListener {
         // 1. begin payment flow
         billingClient?.startConnection(this)
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

   // 2. BillingClientStateListener
   override fun onBillingSetupFinished(billingResult: BillingResult) {
      if (billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
         enablePremiumThankAndFinish()
         return
      }

      // Acknowledge any purchases that haven't been acknowledged already
      val purchases = billingClient?.queryPurchases(BillingClient.SkuType.INAPP)
      purchases?.purchasesList?.forEach { purchase ->
         if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
               val params = AcknowledgePurchaseParams.newBuilder()
                     .setPurchaseToken(purchase.purchaseToken)
                     .build()
               billingClient?.acknowledgePurchase(params, this::onAcknowledgePurchaseResponse)
            }
            enablePremiumThankAndFinish()
         }
      }

      val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(listOf(SKU_PREMIUM)) // non-consumable
            .setType(BillingClient.SkuType.INAPP)
            .build()
      billingClient?.querySkuDetailsAsync(params, this::onSkuDetailsResponse)
   }

   override fun onBillingServiceDisconnected() {
      // TODO failed
   }

   // 3. SkuDetailsResponseListener
   override fun onSkuDetailsResponse(billingResult: BillingResult, skuDetailsList: MutableList<SkuDetails>?) {
      if (billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
         enablePremiumThankAndFinish()
         return
      } else if (billingResult.responseCode != BillingResponseCode.OK) {
         // TODO failed
         return
      }
      skuDetailsList?.forEach { skuDetails ->
         billingClient?.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build())
      }
   }

   // 4. evaluate result of the buy...
   override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
      when {
         billingResult.responseCode == BillingResponseCode.OK && purchases != null -> {
            for (purchase in purchases) {
               // This call only returns unacknowledged purchases
               if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                  val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                  billingClient?.acknowledgePurchase(params, this::onAcknowledgePurchaseResponse)
               }
            }
            enablePremiumThankAndFinish()
         }
         billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED -> {
            enablePremiumThankAndFinish()
         }
         billingResult.responseCode == BillingResponseCode.USER_CANCELED -> {
            // TODO ?
         }
      }
   }

   // 5. AcknowledgePurchaseResponseListener
   override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
      if (billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
         enablePremiumThankAndFinish()
      }
   }

   private fun enablePremiumThankAndFinish() {
      // enablePremium
      ViewModelProvider(this).get(BillingViewModel::class.java).enablePremium()
      // thank
      Toast.makeText(applicationContext, getString(R.string.billing_thanks_for_buying), Toast.LENGTH_LONG).show()
      // finish
      onSupportNavigateUp()
   }

}
