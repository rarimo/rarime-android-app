package com.distributedLab.rarime.di

import com.distributedLab.rarime.data.manager.DataStoreManagerImpl
import com.distributedLab.rarime.domain.manager.DataStoreManager
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
    abstract fun bindDataStoreManager(dataStoreManagerImpl: DataStoreManagerImpl): DataStoreManager
}
