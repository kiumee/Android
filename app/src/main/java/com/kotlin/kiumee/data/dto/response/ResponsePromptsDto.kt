package com.kotlin.kiumee.data.dto.response

import com.kotlin.kiumee.presentation.menu.chat.guidebtn.GuideBtnEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePromptsDto(
    @SerialName("data")
    val data: List<ResponsePromptsDataDto>
)

@Serializable
data class ResponsePromptsDataDto(
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answer: String,
    @SerialName("items")
    val items: List<Int>? = null,
    @SerialName("id")
    val id: Int
) {
    fun toGuideBtnEntity() = GuideBtnEntity(
        id,
        question
    )
}
