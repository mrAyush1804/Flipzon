package com.flipzon.app.feature.cart.domain.repository

import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(cartItem: CartItem)
    suspend fun updateQuantity(id: Int, quantity: Int)
    suspend fun removeFromCart(cartItem: CartItem)
    suspend fun clearCart()
    suspend fun checkout(userId: Int, items: List<CartItem>): NetworkResult<Unit>
}
