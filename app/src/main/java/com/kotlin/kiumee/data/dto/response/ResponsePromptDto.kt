package com.kotlin.kiumee.data.dto.response

import com.kotlin.kiumee.presentation.menu.chat.ChatEntity
import com.kotlin.kiumee.presentation.menu.chat.ChatEntity.Companion.VIEW_TYPE_JUMI
import com.kotlin.kiumee.presentation.menu.chat.ChatOrderInfoEntity
import com.kotlin.kiumee.presentation.menu.chat.ChatSuggestItemsEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePromptDto(
    @SerialName("result") // 채팅 말로 공통 -> 연결 함(확인 완료)
    val result: String,
    @SerialName("suggestItems") // 유저는 null -> 분리처리 함(확인 완료)
    val suggestItems: List<ResponsePromptSuggestItemsDto>? = null,
    @SerialName("orderInfo") // 유저는 null -> 분기처리 함(확인 필요)
    val orderInfo: List<ResponsePromptOrderInfoDto>,
    @SerialName("pointerId") // 유저는 null -> 분기처리 함(확인 필요)
    val pointerId: Int? = null,
    @SerialName("doBilling") // 유저는 null -> 분기처리 함(확인 필요)
    val doBilling: Boolean? = false,
    @SerialName("totalPrice") // 유저는 null -> 연결해야 함(굳이 안함)
    val totalPrice: Int? = 0
) {
    fun toChatEntity() = ChatEntity(
        VIEW_TYPE_JUMI,
        result,
        suggestItems?.map { it.toChatSuggestItemsEntity() },
        orderInfo?.map { it.toChatOrderInfoItemsEntity() },
        pointerId,
        doBilling,
        totalPrice
    )
}

@Serializable
data class ResponsePromptSuggestItemsDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String
) {
    fun toChatSuggestItemsEntity() = ChatSuggestItemsEntity(
        id,
        name,
        price,
        imageUrl
    )
}

@Serializable
data class ResponsePromptOrderInfoDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String
) {
    fun toChatOrderInfoItemsEntity() = ChatOrderInfoEntity(
        id,
        name,
        price,
        imageUrl
    )
}
