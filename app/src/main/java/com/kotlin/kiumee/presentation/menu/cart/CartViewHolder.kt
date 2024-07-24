package com.kotlin.kiumee.presentation.menu.cart

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.core.util.context.formatAmount
import com.kotlin.kiumee.databinding.ItemMenuCartBinding
import java.text.DecimalFormat

class CartViewHolder(val binding: ItemMenuCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: CartEntity) {
        with(binding) {
            tvItemMenuCartName.text = data.name
            tvItemMenuCartPrice.text = "가격 : ${formatAmount(data.price)}원"
        }
    }
}
