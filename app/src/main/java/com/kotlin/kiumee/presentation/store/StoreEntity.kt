package com.kotlin.kiumee.presentation.store

data class StoreEntity(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val createdDatetime: String,
    val updatedDatetime: String
)
