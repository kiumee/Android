package com.kotlin.kiumee.presentation.menu.chat.guidebtn

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.databinding.ItemChatGuideBinding

class GuideBtnViewHolder(
    private val binding: ItemChatGuideBinding,
    private val click: (String, Int) -> Unit = { _, _ -> }
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: GuideBtnEntity) {
        with(binding) {
            tvItemChatGuide.text = data.question

            root.setOnClickListener {
                click(data.question, adapterPosition)
            }
        }
    }
}
