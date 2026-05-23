package com.flipzon.app.feature.cart.data.model

data class CartRequest(
    val userId: Int,
    val products: List<CartProductRequest>
)

data class CartProductRequest(
    val id: Int,
    val quantity: Int
)
