package com.kotlin.kiumee.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPromptDto(
    @SerialName("query")
    val query: String,
    @SerialName("orderInfo")
    val orderInfo: List<Int>
)
