package com.distributedLab.rarime.di

import com.distributedLab.rarime.data.manager.SecureSharedPrefsManagerImpl
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun dataStoreManager(dataStoreManagerImpl: SecureSharedPrefsManagerImpl): SecureSharedPrefsManager
}
