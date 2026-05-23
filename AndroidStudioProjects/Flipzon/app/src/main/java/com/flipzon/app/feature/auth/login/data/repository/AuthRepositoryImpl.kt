package com.flipzon.app.feature.auth.login.data.repository

import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.auth.login.data.model.LoginRequest
import com.flipzon.app.feature.auth.login.data.remote.AuthApiService
import com.flipzon.app.feature.auth.login.domain.model.User
import com.flipzon.app.feature.auth.login.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {
    override suspend fun login(username: String, password: String): NetworkResult<User> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            NetworkResult.Success(
                User(
                    id = response.id,
                    email = response.email,
                    firstName = response.firstName,
                    lastName = response.lastName,
                    imageUrl = response.image
                )
            )
        } catch (e: HttpException) {
            if (e.code() == 401) {
                NetworkResult.Error("Invalid credentials")
            } else {
                NetworkResult.Error(e.message() ?: "Unknown error")
            }
        } catch (e: IOException) {
            NetworkResult.Error("No internet connection")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }
}
