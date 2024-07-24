package com.kotlin.kiumee.presentation.menu.chat.menubtn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.kotlin.kiumee.core.view.ItemDiffCallback
import com.kotlin.kiumee.databinding.ItemChatBtnBinding
import com.kotlin.kiumee.presentation.menu.cart.CartEntity
import com.kotlin.kiumee.presentation.menu.chat.ChatSuggestItemsEntity

class MenuBtnAdapter(
    private val click: (CartEntity, Int) -> Unit = { _, _ -> }
) : ListAdapter<ChatSuggestItemsEntity, MenuBtnViewHolder>(MenuBtnAdapterDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuBtnViewHolder {
        val binding =
            ItemChatBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuBtnViewHolder(binding, click)
    }

    override fun onBindViewHolder(
        holder: MenuBtnViewHolder,
        position: Int
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val MenuBtnAdapterDiffCallback =
            ItemDiffCallback<ChatSuggestItemsEntity>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new }
            )
    }
}
