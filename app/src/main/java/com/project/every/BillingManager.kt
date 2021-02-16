package com.project.every

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*

class BillingManager(context: Context) : PurchasesUpdatedListener {

    var connectStatusType = ConnectStatusType.WAITING
    var skuDetailsStatusType = SkuDetailsStatusType.WAITING

    val skuDetailsList = MutableLiveData<ArrayList<SkuDetails>>()
    val billingClient: BillingClient = BillingClient.newBuilder(context)
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
                billingResult.responseCode != BillingClient.BillingResponseCode.OK -> skuDetailsStatusType = SkuDetailsStatusType.FAIL
                skuDetailsList.isNullOrEmpty() -> skuDetailsStatusType = SkuDetailsStatusType.EMPTY
                else -> {
                    skuDetailsStatusType = SkuDetailsStatusType.EXIST
                    this.skuDetailsList.value = skuDetailsList as ArrayList<SkuDetails>?
                }
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchaseList: MutableList<Purchase>?) {}
}