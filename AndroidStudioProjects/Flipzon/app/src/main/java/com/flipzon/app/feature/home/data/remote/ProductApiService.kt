package com.flipzon.app.feature.home.data.remote

import com.flipzon.app.feature.home.data.model.ProductsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductsResponse

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductsResponse
}
