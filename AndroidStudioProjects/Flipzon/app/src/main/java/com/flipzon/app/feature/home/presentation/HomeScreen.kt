package com.flipzon.app.feature.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.flipzon.app.core.Animationcomponet.DynamicIsland
import com.flipzon.app.core.network.NetworkResult
import com.flipzon.app.feature.cart.presentation.CartViewModel
import com.flipzon.app.feature.home.domain.model.Product

// ─────────────────────────────────────────────────────────────
//  HomeScreen — Main Entry Point
// ─────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val searchQuery     by homeViewModel.searchQuery.collectAsState()
    val searchResults   by homeViewModel.searchResults.collectAsState()
    val pagingItems      = homeViewModel.products.collectAsLazyPagingItems()
    val cartItems       by cartViewModel.cartItems.collectAsState()
    val selectedProduct by homeViewModel.selectedProduct.collectAsState() // ← Island ke liye

    // ── Outer Box — Island is overlaid on top ──────────────────
    Box(modifier = Modifier.fillMaxSize()) {

        // ── Main Content ───────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            // Search bar ke liye Island ka space reserve karo
            Spacer(modifier = Modifier.height(64.dp))

            // Search Bar
            OutlinedTextField(
                value       = searchQuery,
                onValueChange = { homeViewModel.onSearchChange(it) },
                modifier    = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder   = { Text("Search products...") },
                leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon  = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { homeViewModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape     = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Product List / Search Results
            Box(modifier = Modifier.fillMaxSize()) {
                if (searchQuery.isBlank()) {
                    PagedProductList(
                        pagingItems  = pagingItems,
                        cartItems    = cartItems,
                        onProductClick = { homeViewModel.onProductClick(it) }, // ← Island trigger
                        onAddToCart  = { cartViewModel.addToCart(it) },
                        onIncrement  = { cartViewModel.increment(it) },
                        onDecrement  = { cartViewModel.decrement(it) }
                    )
                } else {
                    SearchResultsContent(
                        result       = searchResults,
                        cartItems    = cartItems,
                        onProductClick = { homeViewModel.onProductClick(it) }, // ← Island trigger
                        onAddToCart  = { cartViewModel.addToCart(it) },
                        onIncrement  = { cartViewModel.increment(it) },
                        onDecrement  = { cartViewModel.decrement(it) }
                    )
                }
            }
        }

        // ── DynamicIsland — Sabke Upar Overlay ────────────────
        DynamicIsland(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f),

            // ── Collapsed: selected product ka naam ya app naam
            collapsedContent = {
                Row(
                    verticalAlignment      = Alignment.CenterVertically,
                    horizontalArrangement  = Arrangement.Center,
                    modifier               = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector     = Icons.Default.Build,
                        contentDescription = null,
                        tint            = Color(0xFF006064),
                        modifier        = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = selectedProduct?.title ?: "Flipzon",
                        color      = Color(0xFF006064),
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                }
            },

            // ── Expanded: puri product detail
            expandedContent = { scope ->
                selectedProduct?.let { product ->
                    ProductDetailIslandContent(
                        product    = product,
                        onClose    = { scope.close() },
                        onAddToCart = {
                            cartViewModel.addToCart(
                                Product(
                                    id        = product.id,
                                    title     = product.title,
                                    price     = product.price,
                                    thumbnail = product.thumbnail,
                                    category  = product.category
                                )
                            )
                            scope.close()
                        }
                    )
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Island ka Expanded Content — ID, Title, Price, Category, Date
// ─────────────────────────────────────────────────────────────
@Composable
fun ProductDetailIslandContent(
    product    : SelectedProductUiState,
    onClose    : () -> Unit,
    onAddToCart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "Product Details",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = Color(0xFF006064)
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector     = Icons.Default.Close,
                    contentDescription = "Close",
                    tint            = Color(0xFF006064)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Thumbnail + Title + Price
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model              = product.thumbnail,
                contentDescription = product.title,
                modifier           = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale       = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = Color(0xFF004D40),
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = "$${product.price}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 20.sp,
                    color      = Color(0xFF00796B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFB2EBF2))
        Spacer(modifier = Modifier.height(12.dp))

        // Info Grid — Row 1
        Row(modifier = Modifier.fillMaxWidth()) {
            IslandInfoChip(
                icon     = Icons.Default.Star,
                label    = "ID",
                value    = "#${product.id}",
                modifier = Modifier.weight(1f)
            )
            IslandInfoChip(
                icon     = Icons.Default.ShoppingCart,
                label    = "Category",
                value    = product.category.replaceFirstChar { it.uppercase() },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Info Grid — Row 2
        Row(modifier = Modifier.fillMaxWidth()) {
            IslandInfoChip(
                icon     = Icons.Default.DateRange,
                label    = "Date",
                value    = product.dateAdded,
                modifier = Modifier.weight(1f)
            )
            IslandInfoChip(
                icon     = Icons.Default.ThumbUp,
                label    = "Price/Unit",
                value    = "$${product.price}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Add to Cart Button
        Button(
            onClick = onAddToCart,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00796B)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add to Cart", fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Reusable Info Chip — Island ke andar detail blocks
// ─────────────────────────────────────────────────────────────
@Composable
fun IslandInfoChip(
    icon    : ImageVector,
    label   : String,
    value   : String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector     = icon,
            contentDescription = null,
            tint            = Color(0xFF00796B),
            modifier        = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text     = label,
                fontSize = 10.sp,
                color    = Color(0xFF80CBC4)
            )
            Text(
                text       = value,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF004D40),
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  PagedProductList — onProductClick added
// ─────────────────────────────────────────────────────────────
@Composable
fun PagedProductList(
    pagingItems   : LazyPagingItems<Product>,
    cartItems     : List<com.flipzon.app.feature.cart.domain.model.CartItem>,
    onProductClick: (Product) -> Unit, // ← NEW
    onAddToCart   : (Product) -> Unit,
    onIncrement   : (Int) -> Unit,
    onDecrement   : (Int) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        state           = lazyListState,
        contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count       = pagingItems.itemCount,
            key         = pagingItems.itemKey { it.id },
            contentType = pagingItems.itemContentType { "product" }
        ) { index ->
            pagingItems[index]?.let { product ->
                val quantityInCart = cartItems.find { it.id == product.id }?.quantity ?: 0
                ProductCard(
                    product        = product,
                    quantity       = quantityInCart,
                    onProductClick = { onProductClick(product) }, // ← Island trigger
                    onAddToCart    = { onAddToCart(product) },
                    onIncrement    = { onIncrement(product.id) },
                    onDecrement    = { onDecrement(product.id) }
                )
            }
        }

        pagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingView(Modifier.fillParentMaxSize()) }
                }
                loadState.refresh is LoadState.Error -> {
                    val e = loadState.refresh as LoadState.Error
                    item {
                        ErrorView(
                            message  = e.error.localizedMessage ?: "Failed to load",
                            onRetry  = { retry() },
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingView(Modifier.fillMaxWidth().padding(16.dp)) }
                }
                loadState.append is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item {
                        ErrorView(
                            message  = e.error.localizedMessage ?: "Failed to load more",
                            onRetry  = { retry() },
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                }
                loadState.refresh is LoadState.NotLoading && itemCount == 0 -> {
                    item { EmptyView("No products found", Modifier.fillParentMaxSize()) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  SearchResultsContent — onProductClick added
// ─────────────────────────────────────────────────────────────
@Composable
fun SearchResultsContent(
    result        : NetworkResult<List<Product>>?,
    cartItems     : List<com.flipzon.app.feature.cart.domain.model.CartItem>,
    onProductClick: (Product) -> Unit, // ← NEW
    onAddToCart   : (Product) -> Unit,
    onIncrement   : (Int) -> Unit,
    onDecrement   : (Int) -> Unit
) {
    when (result) {
        is NetworkResult.Loading -> LoadingView(Modifier.fillMaxSize())
        is NetworkResult.Error   -> ErrorView(result.message, onRetry = {}, Modifier.fillMaxSize())
        is NetworkResult.Success -> {
            if (result.data.isEmpty()) {
                EmptyView("No products match your search", Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(result.data.size) { index ->
                        val product        = result.data[index]
                        val quantityInCart = cartItems.find { it.id == product.id }?.quantity ?: 0
                        ProductCard(
                            product        = product,
                            quantity       = quantityInCart,
                            onProductClick = { onProductClick(product) }, // ← Island trigger
                            onAddToCart    = { onAddToCart(product) },
                            onIncrement    = { onIncrement(product.id) },
                            onDecrement    = { onDecrement(product.id) }
                        )
                    }
                }
            }
        }
        else -> Unit
    }
}

// ─────────────────────────────────────────────────────────────
//  ProductCard — onProductClick added to Card
// ─────────────────────────────────────────────────────────────
@Composable
fun ProductCard(
    product       : Product,
    quantity      : Int,
    onProductClick: () -> Unit, // ← NEW
    onAddToCart   : () -> Unit,
    onIncrement   : () -> Unit,
    onDecrement   : () -> Unit
) {
    Card(
        onClick   = onProductClick, // ← tap karo → Island expand
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model              = product.thumbnail,
                contentDescription = product.title,
                modifier           = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale       = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = "ID: ${product.id}",
                    fontSize = 12.sp,
                    color    = Color.Gray
                )
                Text(
                    text       = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    maxLines   = 2
                )
                Text(
                    text       = "$${product.price}",
                    color      = Color(0xFF388E3C),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Cart button click Island trigger nahi karega
            // isliye stopPropagation effect ke liye
            // CartButtonSection ko onClick se alag rakha hai
            CartButtonSection(
                quantity    = quantity,
                onAddToCart = onAddToCart,
                onIncrement = onIncrement,
                onDecrement = onDecrement
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  CartButtonSection — unchanged
// ─────────────────────────────────────────────────────────────
@Composable
fun CartButtonSection(
    quantity   : Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    if (quantity > 0) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick  = onDecrement,
                modifier = Modifier.size(32.dp)
            ) {
                Text("-", fontWeight = FontWeight.Bold)
            }
            Text(text = quantity.toString(), fontWeight = FontWeight.Bold)
            FilledTonalIconButton(
                onClick  = onIncrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    } else {
        Button(
            onClick         = onAddToCart,
            contentPadding  = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier        = Modifier.height(36.dp)
        ) {
            Text("Add", fontSize = 12.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  State / Error / Empty Views — unchanged
// ─────────────────────────────────────────────────────────────
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier              = modifier,
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        Text(text = message, color = Color.Red, modifier = Modifier.padding(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun EmptyView(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Gray)
    }
}