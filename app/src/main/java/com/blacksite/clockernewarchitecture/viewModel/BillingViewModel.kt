package com.blacksite.clockernewarchitecture.viewModel

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.android.billingclient.api.*
import com.blacksite.clockernewarchitecture.application.App
import com.blacksite.clockernewarchitecture.application.Global
import com.blacksite.clockernewarchitecture.application.Settings
import com.blacksite.clockernewarchitecture.customView.MessageDialog
import com.blacksite.clockernewarchitecture.view.MainActivity

class BillingViewModel(application: Application, activity: Activity, viewModel: MainViewModel) : AndroidViewModel(application), PurchasesUpdatedListener {
    lateinit private var billingClient: BillingClient
    var mainViewModel = viewModel
    var mActivity = activity
    var unlockFacePrice = MutableLiveData<String>()
    var unlockDialPrice = MutableLiveData<String>()

    init {
        prepareBilling()
    }

    private fun cancelPurchases() {
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, { responseCode, purchasesList ->
            if (responseCode == BillingClient.BillingResponse.OK && purchasesList != null) {
                for (purchase in purchasesList) {
                    // Process the result.
                    billingClient.consumeAsync(purchase.purchaseToken, {responseCode, outToken ->
                        if (responseCode == BillingClient.BillingResponse.OK) {
                            mainViewModel.prefManager.faceLock = true
                            mainViewModel.faceLock.value = true
                        }
                    })
                }
            }
        })
    }

    private fun prepareBilling() {
        billingClient = BillingClient.newBuilder(getApplication()).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                Log.e("logger", "Billing Setup finished.")
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    Log.e("logger", "Billing Response OK.")
                    val skuList = ArrayList<String>()
                    skuList.add(Settings.UNLOCK_FACE_SKU)
                    skuList.add(Settings.UNLOCK_DIAL_SKU)
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build(), { responseCode, skuDetailsList ->
                        // Process the result.
                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            cancelPurchases()//Should be deleted when app is published.
                            Log.e("logger", "Detail Response OK.")
                            for (skuDetails in skuDetailsList) {
                                val sku = skuDetails.sku
                                val price = skuDetails.price
                                if (Settings.UNLOCK_FACE_SKU == sku) {
                                    Log.e("logger", "Test price is: $price")
                                    unlockFacePrice.value = price
                                }else if (Settings.UNLOCK_DIAL_SKU == sku) {
                                    unlockDialPrice.value = price
                                }
                            }
                        }
                    })
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e("logger", "Billing Service Disconnected.")
            }
        })
    }
    fun purchase(activity:Activity, skuID:String){
        val flowParams = BillingFlowParams.newBuilder()
                .setSku(skuID)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build()
        val responseCode = billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(@BillingClient.BillingResponse responseCode: Int, purchases: List<Purchase>?) {
        Log.e("logger", "Purchase updated.")

        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if(purchase.sku == Settings.UNLOCK_FACE_SKU){
            Log.e("logger", "Unlock Face")
            mainViewModel.prefManager.faceLock = false
            mainViewModel.faceLock.value = false
            MessageDialog(mActivity, "All faces have been unlocked").show()
        }
        else if(purchase.sku == Settings.UNLOCK_DIAL_SKU){
            mainViewModel.prefManager.dialLock = false
            mainViewModel.dialLock.value = false
            MessageDialog(mActivity, "All dials have been unlocked").show()
        }
    }
}