package com.flipzon.app.feature.home.presentation

data class SelectedProductUiState(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val thumbnail: String,
    val dateAdded: String = "Today" // DummyJSON mein date nahi hai
)
