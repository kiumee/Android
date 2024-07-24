package com.kotlin.kiumee.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLoginDto(
    @SerialName("token")
    val token: ResponseLoginTokenDto
)

@Serializable
data class ResponseLoginTokenDto(
    @SerialName("accessToken")
    val accessToken: String
)
