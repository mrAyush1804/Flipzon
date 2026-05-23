package com.flipzon.app.di

import com.flipzon.app.feature.auth.login.data.repository.AuthRepositoryImpl
import com.flipzon.app.feature.auth.login.domain.repository.AuthRepository
import com.flipzon.app.feature.cart.data.repository.CartRepositoryImpl
import com.flipzon.app.feature.cart.domain.repository.CartRepository
import com.flipzon.app.feature.home.data.repository.ProductRepositoryImpl
import com.flipzon.app.feature.home.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
}
