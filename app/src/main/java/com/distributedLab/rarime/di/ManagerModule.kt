package com.distributedLab.rarime.di

import android.content.Context
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.airdrop.AirDropAPI
import com.distributedLab.rarime.api.airdrop.AirDropAPIManager
import com.distributedLab.rarime.api.airdrop.AirDropManager
import com.distributedLab.rarime.api.points.PointsManager
import com.distributedLab.rarime.api.auth.AuthAPI
import com.distributedLab.rarime.domain.manager.APIServiceManager
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.api.points.PointsAPI
import com.distributedLab.rarime.manager.ApiServiceRemoteData
import com.distributedLab.rarime.api.auth.AuthAPIManager
import com.distributedLab.rarime.api.auth.AuthManager
import com.distributedLab.rarime.manager.ContractManager
import com.distributedLab.rarime.manager.SecureSharedPrefsManagerImpl
import com.distributedLab.rarime.modules.common.IdentityManager
import com.distributedLab.rarime.api.points.PointsAPIManager
import com.distributedLab.rarime.modules.common.SecurityManager
import com.distributedLab.rarime.modules.common.SettingsManager
import com.distributedLab.rarime.modules.common.WalletManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.converter.moshi.MoshiConverterFactory

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
    // TODO: remove
    @Provides
    @Singleton
    @Named("otherRetrofit")
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://NONE")
            .build()

    // TODO: remove
    @Provides
    @Singleton
    fun provideAPIService(@Named("otherRetrofit") retrofit: Retrofit): APIServiceManager =
        retrofit.create(APIServiceManager::class.java)

    // NEW

    @Provides
    @Singleton
    @Named("jsonApiRetrofit")
    fun provideJsonApiRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .baseUrl("http://NONE")
            .build()

    @Provides
    @Singleton
    fun provideAirDropAPIManager(@Named("jsonApiRetrofit") retrofit: Retrofit): AirDropAPIManager =
        AirDropAPIManager(
            retrofit.create(AirDropAPI::class.java),
        )

    @Provides
    @Singleton
    fun provideAirDropManager(
        airDropAPIManager: AirDropAPIManager,
        context: Context,
        contractManager: ContractManager,
        identityManager: IdentityManager,
        dataStoreManager: SecureSharedPrefsManager,
    ): AirDropManager = AirDropManager(
        airDropAPIManager,
        context,
        contractManager,
        identityManager,
        dataStoreManager,
    )

    @Provides
    @Singleton
    fun providePointsAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit,
        // TODO: remove
        identityManager: IdentityManager
    ): PointsAPIManager =
        PointsAPIManager(retrofit.create(PointsAPI::class.java), identityManager)

    @Provides
    @Singleton
    fun providePointsManager(
        pointsAPIManager: PointsAPIManager,
        identityManager: IdentityManager
    ): PointsManager = PointsManager(
        pointsAPIManager,
        identityManager,
    )

    @Provides
    @Singleton
    fun provideAuthAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): AuthAPIManager =
        AuthAPIManager(retrofit.create(AuthAPI::class.java))

    @Provides
    @Singleton
    fun provideAuthManager(
        authAPIManager: AuthAPIManager
    ): AuthManager {
        return AuthManager(authAPIManager)
    }


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
        identityManager: IdentityManager,
        pointsAPIManager: PointsAPIManager
    ): WalletManager {
        return WalletManager(
            context,
            dataStoreManager,
            contractManager,
            apiServiceManager,
            identityManager,
            pointsAPIManager
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
