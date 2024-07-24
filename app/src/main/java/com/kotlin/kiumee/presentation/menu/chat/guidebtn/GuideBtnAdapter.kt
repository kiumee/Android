package com.kotlin.kiumee.presentation.menu.chat.guidebtn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.kotlin.kiumee.core.view.ItemDiffCallback
import com.kotlin.kiumee.databinding.ItemChatGuideBinding

class GuideBtnAdapter(
    private val click: (String, Int) -> Unit = { _, _ -> }
) : ListAdapter<GuideBtnEntity, GuideBtnViewHolder>(GuideBtnAdapterDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GuideBtnViewHolder {
        val binding =
            ItemChatGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideBtnViewHolder(binding, click)
    }

    override fun onBindViewHolder(
        holder: GuideBtnViewHolder,
        position: Int
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val GuideBtnAdapterDiffCallback =
            ItemDiffCallback<GuideBtnEntity>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new }
            )
    }
}
