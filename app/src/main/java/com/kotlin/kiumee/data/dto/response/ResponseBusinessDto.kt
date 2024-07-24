package com.kotlin.kiumee.data.dto.response

import com.kotlin.kiumee.presentation.store.StoreEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseBusinessDto(
    @SerialName("data")
    val data: List<ResponseBusinessDataDto>
)

@Serializable
data class ResponseBusinessDataDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("prompt")
    val prompt: String?,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("createdDatetime")
    val createdDatetime: String,
    @SerialName("updatedDatetime")
    val updatedDatetime: String
) {
    fun toStoreEntity() = StoreEntity(
        id,
        name,
        description,
        imageUrl,
        createdDatetime,
        updatedDatetime
    )
}
