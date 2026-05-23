package com.flipzon.app.feature.cart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val quantity: Int,
    val thumbnail: String
)
