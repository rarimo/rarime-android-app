package com.rarilabs.rarime.di

//import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.airdrop.AirDropAPI
import com.rarilabs.rarime.api.airdrop.AirDropAPIManager
import com.rarilabs.rarime.api.airdrop.AirDropManager
import com.rarilabs.rarime.api.auth.AuthAPI
import com.rarilabs.rarime.api.auth.AuthAPIManager
import com.rarilabs.rarime.api.auth.AuthManager
import com.rarilabs.rarime.api.auth.RefreshTokenInterceptor
import com.rarilabs.rarime.api.cosmos.CosmosAPI
import com.rarilabs.rarime.api.cosmos.CosmosAPIManager
import com.rarilabs.rarime.api.cosmos.CosmosManager
import com.rarilabs.rarime.api.erc20.Erc20API
import com.rarilabs.rarime.api.erc20.Erc20ApiManager
import com.rarilabs.rarime.api.erc20.Erc20Manager
import com.rarilabs.rarime.api.points.PointsAPI
import com.rarilabs.rarime.api.points.PointsAPIManager
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.registration.RegistrationAPI
import com.rarilabs.rarime.api.registration.RegistrationAPIManager
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.NfcManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.manager.SecurityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.StableCoinContractManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.store.SecureSharedPrefsManagerImpl
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
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
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
    fun provideNfcManager(@ApplicationContext context: Context): NfcManager {
        return NfcManager(context)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenInterceptor(authManager: AuthManager): RefreshTokenInterceptor =
        RefreshTokenInterceptor(authManager)

    @Provides
    @Singleton
    fun provideOkHttpClient(refreshTokenInterceptor: RefreshTokenInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(refreshTokenInterceptor)
            .build()

    @Provides
    @Singleton
    @Named("refreshRetrofit")
    fun provideRefreshRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl("http://NONE")  // Make sure to replace with your actual base URL
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    @Named("jsonApiRetrofit")
    fun provideJsonApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            ))
            .baseUrl("http://NONE")  // Make sure to replace with your actual base URL
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providerRegistrationAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): RegistrationAPIManager = RegistrationAPIManager(retrofit.create(RegistrationAPI::class.java))


    @Provides
    @Singleton
    fun provideErc20ApiManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): Erc20ApiManager = Erc20ApiManager(retrofit.create(Erc20API::class.java))

    @Provides
    @Singleton
    fun provideRegistrationManager(
        registrationAPIManager: RegistrationAPIManager,
        rarimoContractManager: RarimoContractManager,
        passportManager: PassportManager,
        authManager: AuthManager,
    ): RegistrationManager = RegistrationManager(
        registrationAPIManager, rarimoContractManager, passportManager, authManager
    )

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
        rarimoContractManager: RarimoContractManager,
        identityManager: IdentityManager,
        dataStoreManager: SecureSharedPrefsManager,
    ): AirDropManager = AirDropManager(
        airDropAPIManager,
        context,
        rarimoContractManager,
        identityManager,
        dataStoreManager,
    )

    @Provides
    @Singleton
    fun providePointsAPIManager(@Named("jsonApiRetrofit") retrofit: Retrofit): PointsAPIManager =
        PointsAPIManager(retrofit.create(PointsAPI::class.java))

    @Provides
    @Singleton
    fun providePassportManager(
        dataStoreManager: SecureSharedPrefsManager,
        rarimoContractManager: RarimoContractManager,
        identityManager: IdentityManager
    ): PassportManager = PassportManager(dataStoreManager, rarimoContractManager, identityManager)

    @Provides
    @Singleton
    fun providePointsManager(
        @ApplicationContext context: Context,
        contractManager: RarimoContractManager,
        pointsAPIManager: PointsAPIManager,
        identityManager: IdentityManager,
        authManager: AuthManager,
        passportManager: PassportManager
    ): PointsManager = PointsManager(
        context,
        contractManager,
        pointsAPIManager,
        identityManager,
        authManager,
        passportManager,
    )

    @Provides
    @Singleton
    fun provideAuthAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): AuthAPIManager = AuthAPIManager(retrofit.create(AuthAPI::class.java))

    @Provides
    @Singleton
    fun provideAuthManager(
        @ApplicationContext context: Context,
        authAPIManager: AuthAPIManager,
        identityManager: IdentityManager,
        dataStoreManager: SecureSharedPrefsManager
    ): AuthManager {
        return AuthManager(
            context, authAPIManager, identityManager, dataStoreManager
        )
    }

    @Provides
    @Singleton
    fun provideCosmosAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): CosmosAPIManager = CosmosAPIManager(retrofit.create(CosmosAPI::class.java))

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
        cosmosManager: CosmosManager,
        erc20Manager: Erc20Manager,
        stableCoinContractManager: StableCoinContractManager
    ): WalletManager {
        return WalletManager(
            dataStoreManager = dataStoreManager,
            identityManager = identityManager,
            pointsManager = pointsManager,
            cosmosManager = cosmosManager,
            erc20Manager = erc20Manager,
            stableCoinContractManager = stableCoinContractManager
        )
    }

    @Provides
    @Singleton
    fun provideErc20Manager(
        erc20ApiManager: Erc20ApiManager
    ): Erc20Manager {
        return Erc20Manager(erc20ApiManager)
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
    @Named("RARIMO")
    fun web3(): Web3j {
        return Web3j.build(HttpService(BaseConfig.EVM_RPC_URL))
    }

    @Provides
    @Singleton
    @Named("STABLE_COIN")
    fun web3jStableCoin(): Web3j {
        return Web3j.build(HttpService(BaseConfig.EVM_STABLE_COIN_RPC))
    }


    @Provides
    @Singleton
    fun provideStableCoinContractManager(@Named("STABLE_COIN") web3j: Web3j): StableCoinContractManager {
        return StableCoinContractManager(web3j)
    }

}
