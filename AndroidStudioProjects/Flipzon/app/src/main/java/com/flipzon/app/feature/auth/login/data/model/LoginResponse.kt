package com.flipzon.app.feature.auth.login.data.model

data class LoginResponse(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String
)
