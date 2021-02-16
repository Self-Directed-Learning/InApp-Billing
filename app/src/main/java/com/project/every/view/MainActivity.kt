package com.project.every.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.every.widget.BillingManager
import com.project.every.viewmodel.MainViewModel
import com.project.every.R
import com.project.every.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        billingManager = BillingManager(this)
        observerViewModel()
        observerBillingManager()
    }

    fun observerViewModel() {
        with(viewModel) {
            onPurchaseEvent.observe(this@MainActivity, Observer {
                billingManager.purchaseSkuDetails(skuDetailsList[0])
            })
        }
    }
    fun observerBillingManager() {
        with(billingManager) {
            skuDetailsList.observe(this@MainActivity, Observer {
                viewModel.setData(it)
            })
        }
    }
}