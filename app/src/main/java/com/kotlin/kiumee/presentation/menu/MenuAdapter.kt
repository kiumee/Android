package com.kotlin.kiumee.presentation.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.kotlin.kiumee.core.view.ItemDiffCallback
import com.kotlin.kiumee.databinding.ItemMenuBinding

class MenuAdapter(
    private val click: (MenuEntity, Int) -> Unit = { _, _ -> }
) :
    ListAdapter<MenuEntity, MenuViewHolder>(
        MenuAdapterDiffCallback
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        val binding =
            ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding, click)
    }

    override fun onBindViewHolder(
        holder: MenuViewHolder,
        position: Int
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val MenuAdapterDiffCallback =
            ItemDiffCallback<MenuEntity>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new }
            )
    }
}
