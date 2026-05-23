package com.flipzon.app.feature.auth.login.data.remote

import com.flipzon.app.feature.auth.login.data.model.LoginRequest
import com.flipzon.app.feature.auth.login.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
