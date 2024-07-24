package com.kotlin.kiumee.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseBillingDto(
    @SerialName("orderInfo")
    val orderInfo: ResponseBillingOrderInfoDto,
    @SerialName("totalPrice")
    val totalPrice: Int? = 0
)

@Serializable
data class ResponseBillingOrderInfoDto(
    @SerialName("items")
    val items: List<ResponseBillingOrderInfoItemsDto>
)

@Serializable
data class ResponseBillingOrderInfoItemsDto(
    @SerialName("id")
    val id: Int,
    @SerialName("category")
    val category: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int
)
