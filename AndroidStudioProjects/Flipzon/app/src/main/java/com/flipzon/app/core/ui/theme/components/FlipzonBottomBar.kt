package com.flipzon.app.core.ui.theme.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.flipzon.app.navigation.BottomNavItem

@Composable
fun FlipzonBottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Cart
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onItemClick(item.route) }
            )
        }
    }
}
