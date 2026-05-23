package com.flipzon.app.feature.cart.domain.model

data class CartItem(
    val id: Int,
    val title: String,
    val price: Double,
    val quantity: Int,
    val thumbnail: String
)
