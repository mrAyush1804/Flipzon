Readme · MD
# Flipzon 🛒
 
> A modern e-commerce feed application built with **DummyJSON API**, showcasing production-grade Android development with Clean Architecture, offline-first design, and real-time reactive UI.
 
---
 
## 📱 Screenshots
 
> _Add screenshots or a screen recording GIF here_
 
---
 
## 🚀 Features
 
| Feature | Description |
|---|---|
| 🔐 **Authentication** | Secure login with session persistence via DataStore. Auto-routes to Home if session exists. |
| 📦 **Paginated Feed** | Infinite scrolling product list powered by Paging 3 with Loading, Error, and Empty states. |
| 🔍 **Smart Search** | Real-time product search using DummyJSON Search API with 300ms debounce to minimize API calls. |
| 🛒 **Offline-first Cart** | Add, increment, decrement, and remove items. Cart works fully without internet (Room as source of truth). |
| 💳 **Atomic Checkout** | Sends local cart data to remote API. Clears local DB **only on success**. Preserves cart on failure with exact error message. |
| 🎨 **Modern UI** | Fully responsive Material 3 design with persistent TopBar, Bottom Navigation, and smooth tab state preservation. |
 
---
 
## 🛠 Tech Stack
 
| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **Architecture** | MVVM + Clean Architecture + Repository Pattern |
| **UI** | Jetpack Compose + Material 3 |
| **Dependency Injection** | Hilt |
| **Networking** | Retrofit + OkHttp + Gson |
| **Local Database** | Room |
| **Session Storage** | DataStore Preferences |
| **Pagination** | Paging 3 |
| **Image Loading** | Coil |
| **Async** | Kotlin Coroutines + Flow |
 
---
 
## 🏗 Architecture
 
This project follows **Clean Architecture** with a feature-based modular package structure:
 
```
com.flipzon.app/
├── core/
│   ├── datastore/          # SessionManager (DataStore)
│   ├── network/            # Retrofit, NetworkResult
│   └── utils/              # Constants, Extensions
│
├── feature/
│   ├── auth/login/
│   │   ├── data/           # LoginRequest, LoginResponse, AuthApiService, AuthRepositoryImpl
│   │   ├── domain/         # User model, AuthRepository interface
│   │   └── presentation/   # LoginScreen, LoginViewModel
│   │
│   ├── home/
│   │   ├── data/           # ProductDto, ProductPagingSource, ProductApiService, ProductRepositoryImpl
│   │   ├── domain/         # Product model, ProductRepository interface
│   │   └── presentation/   # HomeScreen, HomeViewModel
│   │
│   └── cart/
│       ├── data/           # CartEntity, CartDao, CartDatabase, CartApiService, CartRepositoryImpl
│       ├── domain/         # CartItem model, CartRepository interface
│       └── presentation/   # CartScreen, CartViewModel
│
├── navigation/             # NavGraph, BottomNavItem
├── ui/                     # FlipzonTopBar, FlipzonBottomBar, Theme
└── di/                     # NetworkModule, DatabaseModule, RepositoryModule
```
 
---
 
## 🔌 API Reference
 
Base URL: `https://dummyjson.com`
 
| Endpoint | Method | Description |
|---|---|---|
| `/auth/login` | POST | Authenticate user |
| `/products?limit=20&skip={n}` | GET | Paginated product feed |
| `/products/search?q={query}` | GET | Search products |
| `/carts/add` | POST | Checkout / submit cart |
 
### 🔑 Demo Credentials
 
```
username : emilys
password : emilyspass
expiresInMins : 30
```
 
---
 
## 📐 Key Implementation Details
 
### Session Management
- Session stored using **DataStore Preferences** (id, email, firstName, lastName, image)
- On app launch: auto-navigate to `Home` if session exists, else `Login`
- Logout clears both DataStore session and Room cart database
### Pagination (Paging 3)
- Uses `limit` and `skip` query params from DummyJSON
- `ProductPagingSource` handles loading next pages automatically
- `cachedIn(viewModelScope)` ensures data survives recomposition
### Cart — Offline First
- `CartEntity` schema: `productId (PK)`, `title`, `price`, `thumbnail`, `quantity`
- All cart operations go through **Room as the single source of truth**
- UI observes via `Flow` — updates instantly on add/remove from Home or Cart tab
### Checkout — Atomic Logic
```
ON SUCCESS → clear Room cart + show "Order placed successfully!"
ON FAILURE → do NOT clear Room + show exact server error message
```
 
---
 
## ⚙️ Setup & Installation
 
1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/flipzon.git
   cd flipzon
   ```
 
2. **Open in Android Studio**
   - Android Studio Hedgehog or newer recommended
   - Min SDK: 26 | Target SDK: 34
3. **Run the app**
   - Connect a device or start an emulator
   - Click **Run ▶️** or use `Shift + F10`
> No API keys required. DummyJSON is a free public API.
 
---
 
## ✅ Evaluation Checklist
 
- [x] Login with session persistence (DataStore)
- [x] Auto launch logic — Home if session exists, Login if not
- [x] No back stack on Login after successful auth
- [x] TopBar persistent across all tabs (image, name, email from DataStore)
- [x] Logout clears session + cart + navigates to Login
- [x] Paginated product feed (limit + skip)
- [x] Product card: ID, Title, Thumbnail, Price
- [x] Add to Cart — saves to Room DB
- [x] Already in cart — shows +/- quantity controls on product card
- [x] Loading / Error / Empty states with Retry on Home
- [x] Search with 300ms debounce
- [x] Cart reactive UI (Flow/StateFlow)
- [x] Increment / Decrement — auto remove at quantity 0
- [x] Cart works fully offline
- [x] Total Price = Σ (price × quantity)
- [x] Checkout sends: userId (DataStore) + products (Room)
- [x] Checkout success: clear Room + success Snackbar
- [x] Checkout failure: keep Room intact + exact error Snackbar
- [x] Tab switch preserves scroll state
---
 
## 📄 License
 
```
MIT License — feel free to use for learning and reference.
```
 
---
 
<div align="center">
  <b>Built with ❤️ using Kotlin + Jetpack Compose</b>
</div>
