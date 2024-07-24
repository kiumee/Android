package com.kotlin.kiumee.presentation.menu.chat

data class ChatEntity(
    val viewType: Int,
    val content: String,
    val suggestItems: List<ChatSuggestItemsEntity>? = null,
    val orderInfo: List<ChatOrderInfoEntity>? = null,
    val pointerId: Int? = null,
    val doBilling: Boolean? = false,
    val totalPrice: Int? = 0
) {
    companion object {
        const val VIEW_TYPE_JUMI = 0
        const val VIEW_TYPE_USER = 1
    }
}

data class ChatSuggestItemsEntity(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String
)

data class ChatOrderInfoEntity(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String
)
