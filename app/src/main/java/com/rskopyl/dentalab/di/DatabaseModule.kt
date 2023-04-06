package com.rskopyl.dentalab.di

import android.content.Context
import androidx.room.Room
import com.rskopyl.dentalab.data.local.AppDatabase
import com.rskopyl.dentalab.data.local.dao.*
import com.rskopyl.dentalab.util.ROOM_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                ROOM_DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao =
        appDatabase.orderDao

    @Provides
    fun provideOrderSearchDao(appDatabase: AppDatabase): OrderSearchDao =
        appDatabase.orderSearchDao

    @Provides
    fun provideModificationDao(appDatabase: AppDatabase): ModificationDao =
        appDatabase.modificationDao

    @Provides
    fun provideStructureDao(appDatabase: AppDatabase): StructureDao =
        appDatabase.structureDao

    @Provides
    fun provideMaterialDao(appDatabase: AppDatabase): MaterialDao =
        appDatabase.materialDao

    @Provides
    fun provideHintDao(appDatabase: AppDatabase): HintDao =
        appDatabase.hintDao
}