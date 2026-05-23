package com.flipzon.app.feature.home.domain.repository

import androidx.paging.PagingData
import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.home.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<PagingData<Product>>
    fun searchProducts(query: String): Flow<NetworkResult<List<Product>>>
}
