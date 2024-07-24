package com.kotlin.kiumee.presentation.menu

data class MenuEntity(
    val id: Int,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val price: Int,
    val isActive: Boolean
)
