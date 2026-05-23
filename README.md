# Flipzon 🛒
E-commerce feed app built with DummyJSON API.

## Tech Stack
| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Architecture | MVVM + Clean Architecture + Repository |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Networking | Retrofit + OkHttp |
| Local DB | Room |
| Session | DataStore Preferences |
| Pagination | Paging 3 |
| Images | Coil |
| Async | Kotlin Coroutines + Flow |

## API
DummyJSON — https://dummyjson.com

## Features
- **Login**: Secure authentication with session persistence using DataStore.
- **Paginated Feed**: Infinite scrolling product list using Paging 3.
- **Search**: Real-time product search with 300ms debounce to optimize API calls.
- **Cart Management**: Offline-first cart using Room. Add, increment, decrement, and remove items directly from the feed or cart screen.
- **Checkout**: Atomic checkout process that sends local cart data to the remote API and clears the local database only on success.
- **Modern UI**: Fully responsive Material 3 design with a unified TopBar and Bottom Navigation.
