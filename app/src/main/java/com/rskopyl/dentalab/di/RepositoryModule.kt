package com.rskopyl.dentalab.di

import com.rskopyl.dentalab.data.preferences.AppPreferencesManager
import com.rskopyl.dentalab.data.preferences.AppPreferencesManagerImpl
import com.rskopyl.dentalab.repository.*
import com.rskopyl.dentalab.repository.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    fun bindOrderSearchRepository(
        impl: OrderSearchRepositoryImpl
    ): OrderSearchRepository

    @Binds
    @Singleton
    fun bindModificationRepository(
        impl: ModificationRepositoryImpl
    ): ModificationRepository

    @Binds
    @Singleton
    fun bindStructureRepository(
        impl: StructureRepositoryImpl
    ): StructureRepository

    @Binds
    @Singleton
    fun bindMaterialRepository(
        impl: MaterialRepositoryImpl
    ): MaterialRepository

    @Binds
    @Singleton
    fun bindHintRepository(
        impl: HintRepositoryImpl
    ): HintRepository

    @Binds
    @Singleton
    fun bindAppPreferencesManager(
        impl: AppPreferencesManagerImpl
    ): AppPreferencesManager
}