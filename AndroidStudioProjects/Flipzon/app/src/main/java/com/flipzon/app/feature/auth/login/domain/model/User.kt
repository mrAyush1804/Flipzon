package com.flipzon.app.feature.auth.login.domain.model

data class User(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String
)
