package com.flipzon.app.feature.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.cart.presentation.CartViewModel
import com.flipzon.app.feature.home.domain.model.Product

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val searchResults by homeViewModel.searchResults.collectAsState()
    val pagingItems = homeViewModel.products.collectAsLazyPagingItems()
    val cartItems by cartViewModel.cartItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { homeViewModel.onSearchChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search products...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { homeViewModel.clearSearch() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (searchQuery.isBlank()) {
                PagedProductList(
                    pagingItems = pagingItems,
                    cartItems = cartItems,
                    onAddToCart = { cartViewModel.addToCart(it) },
                    onIncrement = { cartViewModel.increment(it) },
                    onDecrement = { cartViewModel.decrement(it) }
                )
            } else {
                SearchResultsContent(
                    result = searchResults,
                    cartItems = cartItems,
                    onAddToCart = { cartViewModel.addToCart(it) },
                    onIncrement = { cartViewModel.increment(it) },
                    onDecrement = { cartViewModel.decrement(it) }
                )
            }
        }
    }
}

@Composable
fun PagedProductList(
    pagingItems: LazyPagingItems<Product>,
    cartItems: List<com.flipzon.app.feature.cart.domain.model.CartItem>,
    onAddToCart: (Product) -> Unit,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.id },
            contentType = pagingItems.itemContentType { "product" }
        ) { index ->
            pagingItems[index]?.let { product ->
                val quantityInCart = cartItems.find { it.id == product.id }?.quantity ?: 0
                ProductCard(
                    product = product,
                    quantity = quantityInCart,
                    onAddToCart = { onAddToCart(product) },
                    onIncrement = { onIncrement(product.id) },
                    onDecrement = { onDecrement(product.id) }
                )
            }
        }

        pagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingView(Modifier.fillParentMaxSize()) }
                }
                loadState.refresh is LoadState.Error -> {
                    val e = pagingItems.loadState.refresh as LoadState.Error
                    item {
                        ErrorView(
                            message = e.error.localizedMessage ?: "Failed to load",
                            onRetry = { retry() },
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingView(Modifier.fillMaxWidth().padding(16.dp)) }
                }
                loadState.append is LoadState.Error -> {
                    val e = pagingItems.loadState.append as LoadState.Error
                    item {
                        ErrorView(
                            message = e.error.localizedMessage ?: "Failed to load more",
                            onRetry = { retry() },
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                }
                loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0 -> {
                    item { EmptyView("No products found", Modifier.fillParentMaxSize()) }
                }
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    result: NetworkResult<List<Product>>?,
    cartItems: List<com.flipzon.app.feature.cart.domain.model.CartItem>,
    onAddToCart: (Product) -> Unit,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit
) {
    when (result) {
        is NetworkResult.Loading -> LoadingView(Modifier.fillMaxSize())
        is NetworkResult.Error -> ErrorView(result.message, onRetry = {}, Modifier.fillMaxSize())
        is NetworkResult.Success -> {
            if (result.data.isEmpty()) {
                EmptyView("No products match your search", Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(result.data.size) { index ->
                        val product = result.data[index]
                        val quantityInCart = cartItems.find { it.id == product.id }?.quantity ?: 0
                        ProductCard(
                            product = product,
                            quantity = quantityInCart,
                            onAddToCart = { onAddToCart(product) },
                            onIncrement = { onIncrement(product.id) },
                            onDecrement = { onDecrement(product.id) }
                        )
                    }
                }
            }
        }
        else -> Unit
    }
}

@Composable
fun ProductCard(
    product: Product,
    quantity: Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ID: ${product.id}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2
                )
                Text(
                    text = "$${product.price}",
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.SemiBold
                )
            }

            CartButtonSection(
                quantity = quantity,
                onAddToCart = onAddToCart,
                onIncrement = onIncrement,
                onDecrement = onDecrement
            )
        }
    }
}

@Composable
fun CartButtonSection(
    quantity: Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    if (quantity > 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = onDecrement,
                modifier = Modifier.size(32.dp)
            ) {
                Text("-", fontWeight = FontWeight.Bold)
            }
            Text(text = quantity.toString(), fontWeight = FontWeight.Bold)
            FilledTonalIconButton(
                onClick = onIncrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    } else {
        Button(
            onClick = onAddToCart,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Add", fontSize = 12.sp)
        }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = Color.Red, modifier = Modifier.padding(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun EmptyView(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Gray)
    }
}
