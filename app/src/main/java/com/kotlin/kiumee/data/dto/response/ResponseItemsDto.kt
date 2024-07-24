package com.kotlin.kiumee.data.dto.response

import com.kotlin.kiumee.presentation.menu.CategoryEntity
import com.kotlin.kiumee.presentation.menu.MenuEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseItemsDto(
    @SerialName("data")
    val data: List<ResponseItemsDataDto>
)

@Serializable
data class ResponseItemsDataDto(
    @SerialName("category")
    val category: String,
    @SerialName("items")
    val items: List<ResponseItemsDataItemsDto>
) {
    fun toCategoryEntity() = CategoryEntity(
        category,
        items.map { it.toMenuEntity() }
    )
}

@Serializable
data class ResponseItemsDataItemsDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("prompt")
    val prompt: String?,
    @SerialName("price")
    val price: Int,
    @SerialName("isActive")
    val isActive: Boolean?
) {
    fun toMenuEntity() = MenuEntity(
        id,
        name,
        description,
        imageUrl,
        price,
        isActive ?: true
    )
}
