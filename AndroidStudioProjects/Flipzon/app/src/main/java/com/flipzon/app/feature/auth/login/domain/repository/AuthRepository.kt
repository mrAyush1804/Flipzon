package com.flipzon.app.feature.auth.login.domain.repository

import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.auth.login.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): NetworkResult<User>
}
