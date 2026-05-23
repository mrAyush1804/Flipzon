package com.flipzon.app.feature.cart.data.repository

import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.cart.data.local.CartDao
import com.flipzon.app.feature.cart.data.local.CartEntity
import com.flipzon.app.feature.cart.data.model.CartProductRequest
import com.flipzon.app.feature.cart.data.model.CartRequest
import com.flipzon.app.feature.cart.data.remote.CartApiService
import com.flipzon.app.feature.cart.domain.model.CartItem
import com.flipzon.app.feature.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val apiService: CartApiService
) : CartRepository {
    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map {
                CartItem(it.id, it.title, it.price, it.quantity, it.thumbnail)
            }
        }
    }

    override suspend fun addToCart(cartItem: CartItem) {
        cartDao.insertCartItem(
            CartEntity(
                cartItem.id,
                cartItem.title,
                cartItem.price,
                cartItem.quantity,
                cartItem.thumbnail
            )
        )
    }

    override suspend fun updateQuantity(id: Int, quantity: Int) {
        cartDao.updateQuantity(id, quantity)
    }

    override suspend fun removeFromCart(cartItem: CartItem) {
        cartDao.deleteCartItem(
            CartEntity(
                cartItem.id,
                cartItem.title,
                cartItem.price,
                cartItem.quantity,
                cartItem.thumbnail
            )
        )
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override suspend fun checkout(userId: Int, items: List<CartItem>): NetworkResult<Unit> {
        return try {
            val request = CartRequest(
                userId = userId,
                products = items.map { CartProductRequest(it.id, it.quantity) }
            )
            apiService.checkout(request)
            NetworkResult.Success(Unit)
        } catch (e: IOException) {
            NetworkResult.Error("No internet connection")
        } catch (e: HttpException) {
            NetworkResult.Error(e.message() ?: "Checkout failed")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }
}
