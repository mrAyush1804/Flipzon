package com.flipzon.app.core.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipzonTopBar(
    imageUrl: String,
    fullName: String,
    email: String,
    onLogoutClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        actions = {
            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout"
                )
            }
        }
    )
}
