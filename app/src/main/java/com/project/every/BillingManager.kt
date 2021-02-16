package com.project.every

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*

class BillingManager(val activity: Activity) : PurchasesUpdatedListener {

    var connectStatusType = ConnectStatusType.WAITING
    var skuDetailsStatusType = SkuDetailsStatusType.WAITING
    var purchaseStatusType = PurchaseStatusType.WAITING

    val skuDetailsList = MutableLiveData<ArrayList<SkuDetails>>()
    val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    connectStatusType = ConnectStatusType.CONNECTED
                    getSkuDetailsList()
                } else connectStatusType = ConnectStatusType.FAIL
            }
            override fun onBillingServiceDisconnected() {
                connectStatusType = ConnectStatusType.DISCONNECTED
            }
        })
    }

    fun getSkuDetailsList() {
        val skuIdList = ArrayList<String>()
        skuIdList.add("product1")

        val params = SkuDetailsParams.newBuilder().setSkusList(skuIdList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            when {
                billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty() -> {
                    skuDetailsStatusType = SkuDetailsStatusType.EXIST
                    this.skuDetailsList.value = skuDetailsList as ArrayList<SkuDetails>?
                }
                skuDetailsList.isNullOrEmpty() -> skuDetailsStatusType = SkuDetailsStatusType.EMPTY
                else -> skuDetailsStatusType = SkuDetailsStatusType.FAIL
            }
        }
    }

    fun purchaseSkuDetails(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
        print(responseCode)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchaseList: MutableList<Purchase>?) {
        when {
            billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null -> purchaseStatusType = PurchaseStatusType.SUCCESS
            billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED -> purchaseStatusType = PurchaseStatusType.CANCEL
            else -> purchaseStatusType = PurchaseStatusType.FAIL
        }
    }
}