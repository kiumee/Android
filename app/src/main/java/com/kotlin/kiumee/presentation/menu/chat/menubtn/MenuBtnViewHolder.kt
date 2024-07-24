package com.kotlin.kiumee.presentation.menu.chat.menubtn

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.kiumee.databinding.ItemChatBtnBinding
import com.kotlin.kiumee.presentation.menu.cart.CartEntity
import com.kotlin.kiumee.presentation.menu.chat.ChatSuggestItemsEntity

class MenuBtnViewHolder(
    private val binding: ItemChatBtnBinding,
    private val click: (CartEntity, Int) -> Unit = { _, _ -> }
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: ChatSuggestItemsEntity) {
        with(binding) {
            data.imageUrl.let { url ->
                Glide.with(ivItemChatBtn.context)
                    .load(url)
                    .centerCrop()
                    .into(ivItemChatBtn)
            }
            ivItemChatBtn.clipToOutline = true
            tvItemChatBtnName.text = data.name
            tvItemChatBtnPrice.text = "${data.price}원"

            root.setOnClickListener {
                click(CartEntity(data.id, data.name, data.price), adapterPosition)
            }
        }
    }
}
