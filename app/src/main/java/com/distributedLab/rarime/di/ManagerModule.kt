package com.distributedLab.rarime.di

import android.content.Context
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.domain.manager.APIServiceManager
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.manager.ApiServiceRemoteData
import com.distributedLab.rarime.manager.ContractManager
import com.distributedLab.rarime.manager.SecureSharedPrefsManagerImpl
import com.distributedLab.rarime.modules.common.IdentityManager
import com.distributedLab.rarime.modules.common.SecurityManager
import com.distributedLab.rarime.modules.common.SettingsManager
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {
    @Binds
    @Singleton
    abstract fun dataStoreManager(dataStoreManagerImpl: SecureSharedPrefsManagerImpl): SecureSharedPrefsManager
}


@Module
@InstallIn(SingletonComponent::class)
class APIModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("http://NONE")
            .build()

    @Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): APIServiceManager =
        retrofit.create(APIServiceManager::class.java)


    @Provides
    @Singleton
    fun provideSettingsManager(
        dataStoreManager: SecureSharedPrefsManager
    ): SettingsManager {
        return SettingsManager(
            dataStoreManager
        )
    }

    @Provides
    @Singleton
    fun provideWalletManager(
        @ApplicationContext context: Context,
        dataStoreManager: SecureSharedPrefsManager,
        contractManager: ContractManager,
        apiServiceManager: ApiServiceRemoteData,
        identityManager: IdentityManager
    ): WalletManager {
        return WalletManager(
            context, dataStoreManager, contractManager, apiServiceManager, identityManager
        )
    }

    @Provides
    @Singleton
    fun provideSecurityManager(
        dataStoreManager: SecureSharedPrefsManager
    ): SecurityManager {
        return SecurityManager(dataStoreManager)
    }

    @Provides
    @Singleton
    fun provideIdentityManager(
        dataStoreManager: SecureSharedPrefsManager
    ): IdentityManager {
        return IdentityManager(dataStoreManager)
    }

    @Provides
    @Singleton
    fun web3(): Web3j {
        return Web3j.build(HttpService(BaseConfig.EVM_RPC_URL))
    }


}
