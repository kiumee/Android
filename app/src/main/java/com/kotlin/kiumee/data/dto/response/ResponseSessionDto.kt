package com.kotlin.kiumee.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseSessionDto(
    @SerialName("sessionId")
    val sessionId: String
)
