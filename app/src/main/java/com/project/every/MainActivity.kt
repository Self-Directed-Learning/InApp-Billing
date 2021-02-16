package com.project.every

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        billingManager = BillingManager(this)
        observerBillingManager()
    }

    fun observerBillingManager() {
        with(billingManager) {
            skuDetailsList.observe(this@MainActivity, Observer {
                // 상품 정보 조회 성공
            })
        }
    }
}