package de.salomax.ndx.ui.billing

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity

/**
 * An activity that lists all the features a user gets when buying premium.
 * At the bottom, there's the button that initializes the billing flow.
 * The entire billing flow is done here.
 */
class BillingActivity: BaseActivity(), PurchasesUpdatedListener, BillingClientStateListener,
      SkuDetailsResponseListener {

   companion object {
      private const val SKU_PREMIUM = "premium"
   }

   /**
    * Instantiate a new BillingClient instance.
    */
   private lateinit var billingClient: BillingClient

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

      // button
      findViewById<Button>(R.id.btn_billing_buy).setOnClickListener {
         // Create a new BillingClient in onCreate().
         // Since the BillingClient can only be used once, we need to create a new instance
         // after ending the previous connection to the Google Play Store in onDestroy().
         billingClient = BillingClient.newBuilder(applicationContext)
               .setListener(this)
               .enablePendingPurchases()
               .build()
         if (!billingClient.isReady) {
            billingClient.startConnection(this)
         }
      }
   }

   override fun onSupportNavigateUp(): Boolean {
      onBackPressed()
      return true
   }

   override fun onDestroy() {
      super.onDestroy()
      if (billingClient.isReady) {
         // BillingClient can only be used once. After calling endConnection(), we must create a new BillingClient.
         billingClient.endConnection()
      }
   }

   override fun onBillingSetupFinished(billingResult: BillingResult) {
      val responseCode = billingResult.responseCode
      val debugMessage = billingResult.debugMessage
      if (responseCode == BillingClient.BillingResponseCode.OK) {
         // The billing client is ready. You can query purchases here.
         querySkuDetails()
         queryPurchases()
      }
   }

   override fun onBillingServiceDisconnected() {
      // try connecting again
      billingClient.startConnection(this)
   }

   /**
    * In order to make purchases, you need the [SkuDetails] for the item or subscription.
    * This is an asynchronous call that will receive a result in [onSkuDetailsResponse].
    */
   private fun querySkuDetails() {
      val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(listOf(SKU_PREMIUM))
            .build()
      params.let { skuDetailsParams ->
         billingClient.querySkuDetailsAsync(skuDetailsParams, this)
      }
   }

   /**
    * Receives the result from [querySkuDetails].
    * make the purchases
    */
   override fun onSkuDetailsResponse(billingResult: BillingResult, skuDetailsList: MutableList<SkuDetails>?) {
      val responseCode = billingResult.responseCode
      val debugMessage = billingResult.debugMessage
      when (responseCode) {
         BillingClient.BillingResponseCode.OK -> {
            skuDetailsList?.forEach { skuDetails ->
               launchBillingFlow(BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build())
            }
         }
         BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
         BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
         BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
         BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
         BillingClient.BillingResponseCode.DEVELOPER_ERROR,
         BillingClient.BillingResponseCode.ERROR -> {
            // TODO?
         }
         BillingClient.BillingResponseCode.USER_CANCELED,
         BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
         BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
         BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
            // ignore: these response codes are not expected.
         }
      }
   }

   /**
    * Query Google Play Billing for existing purchases.
    *
    * New purchases will be provided to the PurchasesUpdatedListener.
    * You still need to check the Google Play Billing API to know when purchase tokens are removed.
    */
   private fun queryPurchases() {
      if (!billingClient.isReady) {
         // TODO: what now?
      }
      val result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
      if (result.purchasesList != null) {
         processPurchases(result.purchasesList!!)
      }
   }

   /**
    * Called by the Billing Library when new purchases are detected.
    */
   override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
      val responseCode = billingResult.responseCode
      val debugMessage = billingResult.debugMessage
      when (responseCode) {
         BillingClient.BillingResponseCode.OK -> {
            if (purchases != null) {
               processPurchases(purchases)
            }
         }
         BillingClient.BillingResponseCode.USER_CANCELED -> {
            // User canceled the purchase")
         }
         BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
            // The user already owns this item")
         }
         BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
            // onPurchasesUpdated: Developer error means that Google Play
            // does not recognize the configuration. If you are just getting started,
            // make sure you have configured the application correctly in the
            // Google Play Console. The SKU product ID must match and the APK you
            // are using must be signed with release keys.
         }
      }
   }

   /**
    * Send purchase SingleLiveEvent and update purchases LiveData.
    *
    * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever.
    * The LiveData will allow Google Play settings UI to update based on the latest purchase data.
    */
   private fun processPurchases(purchasesList: List<Purchase>) {
      for (purchase in purchasesList) {
         acknowledgePurchase(purchase.purchaseToken)
      }
      logAcknowledgementStatus(purchasesList)
   }

   /**
    * Log the number of purchases that are acknowledge and not acknowledged.
    *
    * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
    *
    * When the purchase is first received, it will not be acknowledge.
    * This application sends the purchase token to the server for registration. After the
    * purchase token is registered to an account, the Android app acknowledges the purchase token.
    * The next time the purchase list is updated, it will contain acknowledged purchases.
    */
   private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
      var ackYes = 0
      var ackNo = 0
      for (purchase in purchasesList) {
         if (purchase.isAcknowledged) {
            ackYes++
         } else {
            ackNo++
         }
      }
   }

   /**
    * Launching the billing flow.
    * Launching the UI to make a purchase.
    */
   private fun launchBillingFlow(params: BillingFlowParams): Int {
      val sku = params.sku
      val oldSku = params.oldSku
      if (!billingClient.isReady) {
         // TODO: what now?
      }
      val billingResult = billingClient.launchBillingFlow(this, params)
      val responseCode = billingResult.responseCode
      val debugMessage = billingResult.debugMessage
      return responseCode
   }

   /**
    * Acknowledge a purchase.
    *
    * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
    *
    * Apps should acknowledge the purchase after confirming that the purchase token
    * has been associated with a user. This app only acknowledges purchases after
    * successfully receiving the subscription data back from the server.
    *
    * Developers can choose to acknowledge purchases from a server using the
    * Google Play Developer API. The server has direct access to the user database,
    * so using the Google Play Developer API for acknowledgement might be more reliable.
    *
    * If the purchase token is not acknowledged within 3 days,
    * then Google Play will automatically refund and revoke the purchase.
    * This behavior helps ensure that users are not charged for subscriptions unless the
    * user has successfully received access to the content.
    * This eliminates a category of issues where users complain to developers
    * that they paid for something that the app is not giving to them.
    */
   private fun acknowledgePurchase(purchaseToken: String) {
      val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
      billingClient.acknowledgePurchase(params) { billingResult ->
         val responseCode = billingResult.responseCode
         val debugMessage = billingResult.debugMessage
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
