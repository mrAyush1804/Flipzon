package com.flipzon.app.feature.cart.data.remote

import com.flipzon.app.feature.cart.data.model.CartRequest
import com.flipzon.app.feature.cart.data.model.CartResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CartApiService {
    @POST("carts/add")
    suspend fun checkout(@Body request: CartRequest): CartResponse
}
