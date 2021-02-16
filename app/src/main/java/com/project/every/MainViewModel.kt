package com.project.every

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.SkuDetails

class MainViewModel : ViewModel() {

    var skuDetailsList = ArrayList<SkuDetails>()

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val price = MutableLiveData<String>()

    val onPurchaseEvent = SingleLiveEvent<Unit>()

    fun setData(skuDetailsList: ArrayList<SkuDetails>) {
        this.skuDetailsList = skuDetailsList

        title.value = skuDetailsList[0].title
        description.value = skuDetailsList[0].description
        price.value = skuDetailsList[0].price
    }

    fun purchaseEvent() = onPurchaseEvent.call()
}