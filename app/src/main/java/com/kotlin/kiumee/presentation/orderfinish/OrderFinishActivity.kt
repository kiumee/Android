package com.kotlin.kiumee.presentation.orderfinish

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingActivity
import com.kotlin.kiumee.databinding.ActivityOrderFinishBinding
import com.kotlin.kiumee.presentation.home.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderFinishActivity :
    BindingActivity<ActivityOrderFinishBinding>(R.layout.activity_order_finish) {
    override fun initView() {
        initTimer()
        addOnBackPressedCallback()
    }

    private fun addOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initTimer() {
        lifecycleScope.launch {
            delay(5000) // 5초
            Intent(this@OrderFinishActivity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.let { startActivity(it) }
        }
    }
}
