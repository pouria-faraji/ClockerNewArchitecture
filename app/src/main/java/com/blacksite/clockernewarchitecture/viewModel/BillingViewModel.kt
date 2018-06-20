package com.blacksite.clockernewarchitecture.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener

class BillingViewModel(application: Application) : AndroidViewModel(application), PurchasesUpdatedListener {
    lateinit private var billingClient: BillingClient

    init {
        prepareBilling()
    }
    private fun prepareBilling() {
        billingClient = BillingClient.newBuilder(getApplication()).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                Log.e("logger", "Billing Setup finished.")
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    Log.e("logger", "Billing Response OK.")
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e("logger", "Billing Service Disconnected.")
            }
        })
    }
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}