package de.salomax.ndx.ui.billing

import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import com.google.android.material.snackbar.Snackbar
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity

/**
 * An activity that lists all the features a user gets when buying premium.
 * At the bottom, there's the button that initializes the billing flow.
 * The entire billing flow is done here.
 */
@Suppress("UNUSED_VARIABLE", "ControlFlowWithEmptyBody")
class BillingActivity: BaseActivity(), BillingClientStateListener, PurchasesUpdatedListener {

   companion object {
      private const val PRODUCT_PREMIUM = "premium"
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
         billingClient = BillingClient.newBuilder(this)
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
      if (this::billingClient.isInitialized && billingClient.isReady) {
         // BillingClient can only be used once. After calling endConnection(), we must create a new BillingClient.
         billingClient.endConnection()
      }
   }

   override fun onBillingSetupFinished(billingResult: BillingResult) {
      val responseCode = billingResult.responseCode
      val debugMessage = billingResult.debugMessage
      if (responseCode == BillingClient.BillingResponseCode.OK) {
         // The billing client is ready. You can query purchases here.
         queryProductDetails()
         queryPurchases()
      } else {
         // something went wrong
         Snackbar.make(
            findViewById(android.R.id.content),
            "$debugMessage (Code $responseCode)",
            Snackbar.LENGTH_LONG
         ).show()
      }
   }

   override fun onBillingServiceDisconnected() {
      // try connecting again
      billingClient.startConnection(this)
   }

   /**
    * In order to make purchases, you need the [ProductDetails] for the item or subscription.
    * This is an asynchronous call.
    */
   private fun queryProductDetails() {
      val productList = listOf(
         QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_PREMIUM)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
      )
      val params = QueryProductDetailsParams.newBuilder()
         .setProductList(productList)
         .build()
      billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
         // make the purchases
         val responseCode = billingResult.responseCode
         val debugMessage = billingResult.debugMessage
         when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
               productDetailsList.forEach { productDetails ->
                  launchOfferPurchaseFlow(productDetails)
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
   }

   private fun launchOfferPurchaseFlow(productDetails: ProductDetails): BillingResult {
      val productDetailsParamsList =
         listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
               .setProductDetails(productDetails)
               .build()
         )
      val billingFlowParams =
         BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

      // Launching the billing flow: Launching the UI to make a purchase.
      return billingClient.launchBillingFlow(this, billingFlowParams)
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
      val result = billingClient.queryPurchasesAsync(
         QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
      ) { _, purchaseList ->
         processPurchases(purchaseList)
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
            // User canceled the purchase
         }
         BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
            // The user already owns this item
         }
         BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
            // onPurchasesUpdated: Developer error means that Google Play
            // does not recognize the configuration. If you are just getting started,
            // make sure you have configured the application correctly in the
            // Google Play Console. The product ID must match and the APK you
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
    * Log the number of purchases that are acknowledged and not acknowledged.
    *
    * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
    *
    * When the purchase is first received, it will not be acknowledged.
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
      Snackbar.make(
         findViewById(android.R.id.content),
         getString(R.string.billing_thanks_for_buying),
         Snackbar.LENGTH_LONG
      ).show()
      // finish
      onSupportNavigateUp()
   }

}
