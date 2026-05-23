package com.flipzon.app.feature.cart.data.model

data class CartResponse(
    val id: Int,
    val products: List<CartProductResponse>,
    val total: Double,
    val userId: Int
)

data class CartProductResponse(
    val id: Int,
    val title: String,
    val price: Double,
    val quantity: Int
)
