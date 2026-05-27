package com.example.radiation.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.radiation.db.data.AppDb
import com.example.radiation.db.DbRepository
import com.example.radiation.db.DbRepositoryImpl
import com.example.radiation.repository.MainRepository
import com.example.radiation.repository.auth.AuthRepository
import com.example.radiation.repository.auth.AuthRepositoryImpl
import com.example.radiation.repository.device.DeviceRepository
import com.example.radiation.repository.device.DeviceRepositoryImpl
import com.example.radiation.repository.reading.ReadingRepository
import com.example.radiation.repository.reading.ReadingRepositoryImpl
import com.example.radiation.repository.alert.AlertRepository
import com.example.radiation.repository.alert.AlertRepositoryImpl
import com.example.radiation.repository.subscription.SubscriptionRepository
import com.example.radiation.repository.subscription.SubscriptionRepositoryImpl
import com.example.radiation.network.ApiService
import com.example.radiation.config.Common
import javax.inject.Singleton

/**
 * Hilt модуль для Database та Repository конфігурації
 * Адмін功能 видалена - буде на вебі
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Singleton
    @Provides
    fun provideAppDb(
        @ApplicationContext context: Context
    ): AppDb {
        return Room.databaseBuilder(
            context,
            AppDb::class.java,
            Common.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Singleton
    @Provides
    fun provideDbRepository(
        appDb: AppDb
    ): DbRepository {
        return DbRepositoryImpl(appDb)
    }
    
    // ============ AUTH REPOSITORY ============
    @Singleton
    @Provides
    fun provideAuthRepository(
        apiService: ApiService,
        dataStore: DataStore<Preferences>
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, dataStore)
    }
    
    // ============ DEVICE REPOSITORY ============
    @Singleton
    @Provides
    fun provideDeviceRepository(
        apiService: ApiService
    ): DeviceRepository {
        return DeviceRepositoryImpl(apiService)
    }
    
    // ============ READING REPOSITORY ============
    @Singleton
    @Provides
    fun provideReadingRepository(
        apiService: ApiService
    ): ReadingRepository {
        return ReadingRepositoryImpl(apiService)
    }
    
    // ============ ALERT REPOSITORY ============
    @Singleton
    @Provides
    fun provideAlertRepository(
        apiService: ApiService
    ): AlertRepository {
        return AlertRepositoryImpl(apiService)
    }
    
    // ============ SUBSCRIPTION REPOSITORY ============
    @Singleton
    @Provides
    fun provideSubscriptionRepository(
        apiService: ApiService
    ): SubscriptionRepository {
        return SubscriptionRepositoryImpl(apiService)
    }
    
    // ============ MAIN REPOSITORY (Facade) ============
    @Singleton
    @Provides
    fun provideMainRepository(
        authRepository: AuthRepository,
        deviceRepository: DeviceRepository,
        readingRepository: ReadingRepository,
        alertRepository: AlertRepository,
        subscriptionRepository: SubscriptionRepository
    ): MainRepository {
        return MainRepository(
            authRepository,
            deviceRepository,
            readingRepository,
            alertRepository,
            subscriptionRepository
        )
    }
}
