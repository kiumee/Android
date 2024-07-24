package com.kotlin.kiumee.presentation.menu.menuviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kotlin.kiumee.presentation.menu.CategoryEntity

class MenuViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private val tabList: List<CategoryEntity>) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = tabList.size

    override fun createFragment(position: Int): Fragment {
        return MenuViewPagerFragment.newInstance(position)
    }
}
