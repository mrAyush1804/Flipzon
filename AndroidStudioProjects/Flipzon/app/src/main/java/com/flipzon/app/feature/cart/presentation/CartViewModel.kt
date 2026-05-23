package com.flipzon.app.feature.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipzon.app.core.datastore.SessionManager
import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.cart.domain.model.CartItem
import com.flipzon.app.feature.cart.domain.repository.CartRepository
import com.flipzon.app.feature.home.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    object Success : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPrice: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _checkoutState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState.asStateFlow()

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(
                CartItem(
                    id = product.id,
                    title = product.title,
                    price = product.price,
                    quantity = 1,
                    thumbnail = product.thumbnail
                )
            )
        }
    }

    fun increment(id: Int) {
        viewModelScope.launch {
            val item = cartItems.value.find { it.id == id }
            item?.let {
                cartRepository.updateQuantity(id, it.quantity + 1)
            }
        }
    }

    fun decrement(id: Int) {
        viewModelScope.launch {
            val item = cartItems.value.find { it.id == id }
            item?.let {
                if (it.quantity > 1) {
                    cartRepository.updateQuantity(id, it.quantity - 1)
                } else {
                    cartRepository.removeFromCart(it)
                }
            }
        }
    }

    fun checkout() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutUiState.Loading
            val session = sessionManager.sessionFlow.first()
            if (session != null) {
                val result = cartRepository.checkout(session.userId, cartItems.value)
                when (result) {
                    is NetworkResult.Success -> {
                        cartRepository.clearCart()
                        _checkoutState.value = CheckoutUiState.Success
                    }
                    is NetworkResult.Error -> {
                        _checkoutState.value = CheckoutUiState.Error(result.message)
                    }
                    else -> {}
                }
            } else {
                _checkoutState.value = CheckoutUiState.Error("Session not found")
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutUiState.Idle
    }
}
