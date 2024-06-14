package com.distributedLab.rarime.di

import android.content.Context
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.airdrop.AirDropAPI
import com.distributedLab.rarime.api.airdrop.AirDropAPIManager
import com.distributedLab.rarime.api.airdrop.AirDropManager
import com.distributedLab.rarime.api.points.PointsManager
import com.distributedLab.rarime.api.auth.AuthAPI
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import com.distributedLab.rarime.api.points.PointsAPI
import com.distributedLab.rarime.api.auth.AuthAPIManager
import com.distributedLab.rarime.api.auth.AuthManager
import com.distributedLab.rarime.api.cosmos.CosmosAPI
import com.distributedLab.rarime.api.cosmos.CosmosAPIManager
import com.distributedLab.rarime.api.cosmos.CosmosManager
import com.distributedLab.rarime.manager.ContractManager
import com.distributedLab.rarime.store.SecureSharedPrefsManagerImpl
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.api.points.PointsAPIManager
import com.distributedLab.rarime.api.registration.RegistrationAPI
import com.distributedLab.rarime.api.registration.RegistrationAPIManager
import com.distributedLab.rarime.api.registration.RegistrationManager
import com.distributedLab.rarime.manager.SecurityManager
import com.distributedLab.rarime.manager.SettingsManager
import com.distributedLab.rarime.manager.WalletManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
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
            .client(OkHttpClient
                .Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()
            )
            .build()

    @Provides
    @Singleton
    fun providerRegistrationAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): RegistrationAPIManager = RegistrationAPIManager(retrofit.create(RegistrationAPI::class.java))

    @Provides
    @Singleton
    fun provideRegistrationManager(
        registrationAPIManager: RegistrationAPIManager
    ): RegistrationManager = RegistrationManager(registrationAPIManager)

    @Provides
    @Singleton
    fun provideAirDropAPIManager(@Named("jsonApiRetrofit") retrofit: Retrofit): AirDropAPIManager =
        AirDropAPIManager(
            retrofit.create(AirDropAPI::class.java),
        )

    @Provides
    @Singleton
    fun provideAirDropManager(
        @ApplicationContext context: Context,
        airDropAPIManager: AirDropAPIManager,
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
    fun providePointsAPIManager(@Named("jsonApiRetrofit") retrofit: Retrofit): PointsAPIManager =
        PointsAPIManager(retrofit.create(PointsAPI::class.java))

    @Provides
    @Singleton
    fun providePointsManager(
        @ApplicationContext context: Context,
        contractManager: ContractManager,
        pointsAPIManager: PointsAPIManager,
        identityManager: IdentityManager,
        authManager: AuthManager,
        dataStoreManager: SecureSharedPrefsManager
    ): PointsManager = PointsManager(
        context,
        contractManager,
        pointsAPIManager,
        identityManager,
        authManager,
        dataStoreManager
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
        @ApplicationContext context: Context,
        authAPIManager: AuthAPIManager,
        identityManager: IdentityManager,
        dataStoreManager: SecureSharedPrefsManager
    ): AuthManager {
        return AuthManager(
            context,
            authAPIManager,
            identityManager,
            dataStoreManager
        )
    }

    @Provides
    @Singleton
    fun provideCosmosAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): CosmosAPIManager =
        CosmosAPIManager(retrofit.create(CosmosAPI::class.java))

    @Provides
    @Singleton
    fun provideCosmosManager(
        cosmosAPIManager: CosmosAPIManager
    ): CosmosManager {
        return CosmosManager(cosmosAPIManager)
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
        dataStoreManager: SecureSharedPrefsManager,
        identityManager: IdentityManager,
        pointsManager: PointsManager,
        cosmosManager: CosmosManager
    ): WalletManager {
        return WalletManager(
            dataStoreManager = dataStoreManager,
            identityManager = identityManager,
            pointsManager = pointsManager,
            cosmosManager = cosmosManager,
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
