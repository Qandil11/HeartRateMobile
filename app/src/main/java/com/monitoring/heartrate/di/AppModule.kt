package com.monitoring.heartrate.di

import com.monitoring.heartrate.data.AuthRepositoryImpl
import com.monitoring.heartrate.data.DashboardRepositoryImpl
import com.monitoring.heartrate.domain.network.ApiService
import com.monitoring.heartrate.domain.network.RetrofitBuilder
import com.monitoring.heartrate.domain.repository.AuthRepository
import com.monitoring.heartrate.domain.repository.DashboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(): DashboardRepository {
        return DashboardRepositoryImpl()
    }
}
