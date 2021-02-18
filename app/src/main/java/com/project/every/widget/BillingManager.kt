package com.project.every.widget

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.project.every.model.ConnectStatusType
import com.project.every.model.ConsumeStatusType
import com.project.every.model.PurchaseStatusType
import com.project.every.model.SkuDetailsStatusType

class BillingManager(val activity: Activity) : PurchasesUpdatedListener, ConsumeResponseListener {

    var connectStatusType = ConnectStatusType.WAITING
    var skuDetailsStatusType = SkuDetailsStatusType.WAITING
    var purchaseStatusType = PurchaseStatusType.WAITING
    var consumeStatusType = ConsumeStatusType.WAITING

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

        billingClient.launchBillingFlow(activity, flowParams).responseCode
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchaseList: MutableList<Purchase>?) {
        when {
            billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null -> {
                purchaseStatusType = PurchaseStatusType.SUCCESS
                val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseList[0].purchaseToken).build()
                billingClient.consumeAsync(consumeParams, this)
            }
            billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED -> purchaseStatusType = PurchaseStatusType.CANCEL
            else -> purchaseStatusType = PurchaseStatusType.FAIL
        }
    }

    override fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) consumeStatusType = ConsumeStatusType.SUCCESS
        else consumeStatusType = ConsumeStatusType.FAIL
    }
}