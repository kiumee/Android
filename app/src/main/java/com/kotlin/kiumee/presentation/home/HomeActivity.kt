package com.kotlin.kiumee.presentation.home

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingActivity
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.databinding.ActivityHomeBinding
import com.kotlin.kiumee.presentation.menu.MenuActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class HomeActivity : BindingActivity<ActivityHomeBinding>(R.layout.activity_home) {
    private val sessionViewModel by viewModels<SessionViewModel>()

    override fun initView() {
        initObserve()
    }

    private fun initObserve() {
        sessionViewModel.getSession.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> initScreenClickListener()
                is UiState.Failure -> Timber.d("실패 : $it")
                is UiState.Loading -> Timber.d("로딩중")
                is UiState.Empty -> Timber.d("empty")
            }
        }.launchIn(lifecycleScope)
    }

    private fun initScreenClickListener() {
        binding.ivHomeBackground.setOnClickListener {
            Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.let { startActivity(it) }
        }
    }
}
