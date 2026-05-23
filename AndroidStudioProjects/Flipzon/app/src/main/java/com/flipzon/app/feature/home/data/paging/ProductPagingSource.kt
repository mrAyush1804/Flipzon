package com.flipzon.app.feature.home.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flipzon.app.feature.home.data.remote.ProductApiService
import com.flipzon.app.feature.home.domain.model.Product
import retrofit2.HttpException
import java.io.IOException

class ProductPagingSource(
    private val api: ProductApiService
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 0
        val skip = page * 20
        return try {
            val response = api.getProducts(limit = 20, skip = skip)
            val products = response.products.map {
                Product(
                    id = it.id,
                    title = it.title,
                    price = it.price,
                    thumbnail = it.thumbnail
                )
            }
            LoadResult.Page(
                data = products,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (products.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
