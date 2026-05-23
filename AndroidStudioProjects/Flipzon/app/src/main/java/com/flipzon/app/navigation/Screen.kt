package com.flipzon.app.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Cart : Screen("cart")
}
