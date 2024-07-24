package com.kotlin.kiumee.presentation.store

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingActivity
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.databinding.ActivityStoreBinding
import com.kotlin.kiumee.presentation.LoadingActivity
import com.kotlin.kiumee.presentation.home.HomeActivity
import com.kotlin.kiumee.presentation.login.LoginActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class StoreActivity : BindingActivity<ActivityStoreBinding>(R.layout.activity_store) {
    private val storeViewModel by viewModels<StoreViewModel>()
    private val loadingDialog by lazy { LoadingActivity(this) }

    override fun initView() {
        initHomeBtnClickListener()
        initObserve()
    }

    private fun initHomeBtnClickListener() {
        binding.appbarStore.ibLoginHome.setOnClickListener {
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.let { startActivity(it) }
        }
    }

    private fun initObserve() {
        storeViewModel.getStore.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> {
                    loadingDialog.dismiss()
                    initFormAdapter(it.data)
                }
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    Timber.d("실패 : $it")
                }
                is UiState.Loading -> {
                    loadingDialog.show()
                    Timber.d("로딩중")
                }
                is UiState.Empty -> {
                    loadingDialog.dismiss()
                    Timber.d("empty")
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun initEmptyLayout() {
        with(binding) {
            tvStoreTitle.visibility = View.GONE
            rvStore.visibility = View.GONE
            layoutStoreEmpty.visibility = View.VISIBLE
        }
    }

    private fun initFormAdapter(storeData: List<StoreEntity>) {
        if (storeData.isEmpty()) {
            initEmptyLayout()
        } else {
            binding.rvStore.adapter = StoreAdapter(click = { storeData, position ->
                storeViewModel.getBusinessId(storeData.id)
                startActivity(Intent(this, HomeActivity::class.java))
            }).apply {
                submitList(storeData)
            }
            binding.rvStore.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvStore.addItemDecoration(StoreItemDecorator(this))
        }
    }
}
