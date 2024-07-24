package com.kotlin.kiumee.presentation.menu.menuviewpager

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingFragment
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.databinding.FragmentTabBinding
import com.kotlin.kiumee.presentation.menu.CategoryEntity
import com.kotlin.kiumee.presentation.menu.MenuActivity
import com.kotlin.kiumee.presentation.menu.MenuAdapter
import com.kotlin.kiumee.presentation.menu.MenuItemDecorator
import com.kotlin.kiumee.presentation.menu.MenuViewModel
import com.kotlin.kiumee.presentation.menu.cart.CartEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MenuViewPagerFragment : BindingFragment<FragmentTabBinding>(R.layout.fragment_tab) {
    private val menuViewModel by viewModels<MenuViewModel>()

    override fun initView() {
        initObserve()
    }

    private fun initObserve() {
        menuViewModel.getMenu.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> initMenuViewPagerAdapter(it.data)
                is UiState.Failure -> Timber.d("실패 : $it")
                is UiState.Loading -> Timber.d("로딩중")
                is UiState.Empty -> Timber.d("empty")
            }
        }.launchIn(lifecycleScope)
    }

    private fun initMenuViewPagerAdapter(menuData: List<CategoryEntity>) {
        binding.rvTab.adapter = MenuAdapter(click = { menu, position ->
            val newCartItem = CartEntity(menu.id, menu.name, menu.price)
            (activity as? MenuActivity)?.addCartItem(newCartItem)
        }).apply {
            submitList(
                menuData.getOrNull(
                    (activity as? MenuActivity)?.getLastClickedPosition() ?: 0
                )?.items
            )
        }
        binding.rvTab.addItemDecoration(MenuItemDecorator(requireContext()))
    }

    companion object {
        private const val ARG_TAB_POSITION = "tab_position"

        fun newInstance(tabPosition: Int): MenuViewPagerFragment {
            val fragment = MenuViewPagerFragment()
            val args = Bundle()
            args.putInt(ARG_TAB_POSITION, tabPosition)
            fragment.arguments = args
            return fragment
        }
    }
}
