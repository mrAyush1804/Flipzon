package com.flipzon.app.feature.home.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.home.data.paging.ProductPagingSource
import com.flipzon.app.feature.home.data.remote.ProductApiService
import com.flipzon.app.feature.home.domain.model.Product
import com.flipzon.app.feature.home.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService
) : ProductRepository {

    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { ProductPagingSource(apiService) }
        ).flow
    }

    override fun searchProducts(query: String): Flow<NetworkResult<List<Product>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.searchProducts(query)
            val products = response.products.map {
                Product(
                    id = it.id,
                    title = it.title,
                    price = it.price,
                    thumbnail = it.thumbnail,
                    category = it.category
                )
            }
            emit(NetworkResult.Success(products))
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.message() ?: "Unknown error"))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }
}
