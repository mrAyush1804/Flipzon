package com.flipzon.app.feature.auth.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipzon.app.core.datastore.SessionManager
import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.auth.login.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            when (val result = authRepository.login(username, password)) {
                is NetworkResult.Success -> {
                    val user = result.data
                    sessionManager.saveSession(
                        userId = user.id,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        imageUrl = user.imageUrl
                    )
                    _uiState.value = LoginUiState.Success
                }
                is NetworkResult.Error -> {
                    _uiState.value = LoginUiState.Error(result.message)
                }
                is NetworkResult.Loading -> {
                    _uiState.value = LoginUiState.Loading
                }
            }
        }
    }
}
