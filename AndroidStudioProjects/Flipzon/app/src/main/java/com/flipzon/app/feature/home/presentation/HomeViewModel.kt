package com.flipzon.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.home.domain.model.Product
import com.flipzon.app.feature.home.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,

) : ViewModel() {

    val products: Flow<PagingData<Product>> = productRepository.getProducts()
        .cachedIn(viewModelScope)

    private val _selectedProduct: MutableStateFlow<SelectedProductUiState?> = MutableStateFlow<SelectedProductUiState?>(null)
    val selectedProduct: StateFlow<SelectedProductUiState?> = _selectedProduct

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<NetworkResult<List<Product>>?> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(null)
            } else {
                productRepository.searchProducts(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun onProductClick(product: Product) {
        _selectedProduct.value = SelectedProductUiState(
            id = product.id,
            title = product.title,
            price = product.price,
            thumbnail = product.thumbnail,
            dateAdded = getCurrentDate(),
            category = "Product"
        )
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}
