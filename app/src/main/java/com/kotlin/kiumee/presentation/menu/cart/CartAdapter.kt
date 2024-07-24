package com.kotlin.kiumee.presentation.menu.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.kotlin.kiumee.core.view.ItemDiffCallback
import com.kotlin.kiumee.databinding.ItemMenuCartBinding

class CartAdapter(private val onDeleteClickListener: (CartEntity) -> Unit) :
    ListAdapter<CartEntity, CartViewHolder>(CartAdapterDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val binding =
            ItemMenuCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val cartItem = currentList[position]
        holder.bind(cartItem)
        holder.binding.ibItemMenuCartTrash.setOnClickListener {
            onDeleteClickListener.invoke(cartItem)
        }
    }

    companion object {
        private val CartAdapterDiffCallback =
            ItemDiffCallback<CartEntity>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new }
            )
    }
}
