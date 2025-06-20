package com.rarilabs.rarime.di

import android.content.Context
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.airdrop.AirDropAPI
import com.rarilabs.rarime.api.airdrop.AirDropAPIManager
import com.rarilabs.rarime.api.auth.AuthAPI
import com.rarilabs.rarime.api.auth.AuthAPIManager
import com.rarilabs.rarime.api.auth.RefreshTokenInterceptor
import com.rarilabs.rarime.api.cosmos.CosmosAPI
import com.rarilabs.rarime.api.cosmos.CosmosAPIManager
import com.rarilabs.rarime.api.erc20.Erc20API
import com.rarilabs.rarime.api.erc20.Erc20ApiManager
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorAPI
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApi
import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApiManager
import com.rarilabs.rarime.api.hiddenPrize.models.Included
import com.rarilabs.rarime.api.hiddenPrize.models.IncludedJsonAdapter
import com.rarilabs.rarime.api.likeness.LikenessApi
import com.rarilabs.rarime.api.likeness.LikenessApiManager
import com.rarilabs.rarime.api.nativeToken.NativeTokenAPI
import com.rarilabs.rarime.api.nativeToken.models.NativeTokenAPIManager
import com.rarilabs.rarime.api.points.PointsAPI
import com.rarilabs.rarime.api.points.PointsAPIManager
import com.rarilabs.rarime.api.registration.RegistrationAPI
import com.rarilabs.rarime.api.registration.RegistrationAPIManager
import com.rarilabs.rarime.api.voting.VotingApi
import com.rarilabs.rarime.api.voting.VotingApiManager
import com.rarilabs.rarime.manager.AirDropManager
import com.rarilabs.rarime.manager.AuthManager
import com.rarilabs.rarime.manager.CosmosManager
import com.rarilabs.rarime.manager.DriveBackupManager
import com.rarilabs.rarime.manager.Erc20Manager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.NfcManager
import com.rarilabs.rarime.manager.NotificationManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.ProofGenerationManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.manager.RegistrationManager
import com.rarilabs.rarime.manager.SecurityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.StableCoinContractManager
import com.rarilabs.rarime.manager.TestContractManager
import com.rarilabs.rarime.manager.VotingManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.store.SecureSharedPrefsManagerImpl
import com.rarilabs.rarime.store.room.notifications.AppDatabase
import com.rarilabs.rarime.store.room.notifications.NotificationsDao
import com.rarilabs.rarime.store.room.notifications.NotificationsRepository
import com.rarilabs.rarime.store.room.transactons.TransactionDao
import com.rarilabs.rarime.store.room.voting.VotingDao
import com.rarilabs.rarime.store.room.voting.VotingRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Lazy
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
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideNfcManager(
        @ApplicationContext context: Context, pointsManager: PointsManager
    ): NfcManager {
        return NfcManager(context, pointsManager)
    }

    @Provides
    @Singleton
    @Named("authRetrofit")
    fun provideAuthRetrofit(): Retrofit {
        return Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        ).baseUrl(BaseConfig.RELAYER_URL).client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        ).build()
    }


    @Provides
    @Singleton
    @Named("voting")
    fun provideVotingRetrofit(): Retrofit {
        return Retrofit.Builder().addConverterFactory(
            GsonConverterFactory.create()
//            MoshiConverterFactory.create(
//                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//            )
        ).baseUrl(BaseConfig.VOTING_RELAYER_URL).client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        ).build()
    }

    @Provides
    @Singleton
    @Named("erc20Retrofit")
    fun provideErc20Retrofit(): Retrofit {
        return Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        ).baseUrl(BaseConfig.EVM_SERVICE_URL).client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        ).build()
    }


    @Provides
    @Singleton
    @Named("jsonApiRetrofit")
    fun provideJsonApiRetrofit(
        authManager: Lazy<AuthManager>, // Use Lazy injection to break the cycle
        @Named("authRetrofit") authRetrofit: Retrofit
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                RefreshTokenInterceptor(authManager, authRetrofit)
            )
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()


        val moshi = Moshi.Builder()
            .add { type, _, moshi ->
                if (type == Included::class.java) IncludedJsonAdapter(moshi) else null
            }
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BaseConfig.RELAYER_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideVotingApiManager(
        @Named("voting") retrofit: Retrofit
    ): VotingApiManager = VotingApiManager(retrofit.create(VotingApi::class.java))

    @Provides
    @Singleton
    fun provideVotingManager(
        votingApiManager: VotingApiManager,
        votingContractManager: TestContractManager,
        rarimoContractManager: RarimoContractManager,
        passportManager: PassportManager,
        identityManager: IdentityManager,
        votingRepository: VotingRepository,
        testContractManager: TestContractManager,
    ): VotingManager {
        return VotingManager(
            votingApiManager,
            votingContractManager,
            rarimoContractManager,
            testContractManager,
            passportManager,
            identityManager,
            votingRepository
        )
    }

    @Provides
    @Singleton
    fun provideAuthAPIManager(
        @Named("authRetrofit") retrofit: Retrofit
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
    fun providerRegistrationAPIManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit
    ): RegistrationAPIManager = RegistrationAPIManager(retrofit.create(RegistrationAPI::class.java))

    @Provides
    @Singleton
    fun provideErc20ApiManager(
        @Named("erc20Retrofit") retrofit: Retrofit
    ): Erc20ApiManager = Erc20ApiManager(retrofit.create(Erc20API::class.java))

    @Provides
    @Singleton
    @Named("EXT_INTEGRATOR")
    fun provideExtIntegratorRetrofit(): Retrofit {
        return Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        ).baseUrl("http://NONE").client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        ).build()
    }

    @Provides
    @Singleton
    fun provideExtIntegratorAPIManager(
        @Named("EXT_INTEGRATOR") retrofit: Retrofit,
        contractManager: RarimoContractManager,
        sharedPreferences: SecureSharedPrefsManager,
        passportManager: PassportManager,
        identityManager: IdentityManager,
    ): ExtIntegratorApiManager = ExtIntegratorApiManager(
        retrofit.create(ExtIntegratorAPI::class.java),
        contractManager,
        sharedPreferences,
        passportManager,
        identityManager,
    )

    @Provides
    @Singleton
    fun provideRegistrationManager(
        registrationAPIManager: RegistrationAPIManager,
        rarimoContractManager: RarimoContractManager,
        passportManager: PassportManager,
        identityManager: IdentityManager
    ): RegistrationManager = RegistrationManager(
        registrationAPIManager, rarimoContractManager, passportManager, identityManager
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
        dataStoreManager: SecureSharedPrefsManager, identityManager: IdentityManager
    ): PassportManager = PassportManager(dataStoreManager, identityManager)


    @Provides
    @Singleton
    fun provideProofGenerationManager(
        @ApplicationContext context: Context,
        identityManager: IdentityManager,
        registrationManager: RegistrationManager,
        rarimoContractManager: RarimoContractManager,
        passportManager: PassportManager,
        pointsManager: PointsManager
    ): ProofGenerationManager = ProofGenerationManager(
        context,
        identityManager,
        registrationManager,
        passportManager,
        rarimoContractManager,
        pointsManager
    )


    @Provides
    @Singleton
    fun providePointsManager(
        @ApplicationContext context: Context,
        contractManager: RarimoContractManager,
        pointsAPIManager: PointsAPIManager,
        identityManager: IdentityManager,
        authManager: AuthManager,
        passportManager: PassportManager,
        sharedPrefsManager: SecureSharedPrefsManager
    ): PointsManager = PointsManager(
        context,
        contractManager,
        pointsAPIManager,
        identityManager,
        authManager,
        passportManager,
        sharedPrefsManager
    )

    @Provides
    @Singleton
    @Named("jsonApiCosmosRetrofit")
    fun provideCosmosRetrofit(
        authManager: Lazy<AuthManager>, // Use Lazy injection to break the cycle
        @Named("authRetrofit") authRetrofit: Retrofit
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(
            RefreshTokenInterceptor(
                authManager, authRetrofit
            )
        ).addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        return Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        ).baseUrl(BaseConfig.COSMOS_RPC_URL).client(okHttpClient).build()
    }

    @Provides
    @Singleton
    fun provideCosmosAPIManager(
        @Named("jsonApiCosmosRetrofit") retrofit: Retrofit
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
        @Named("RARIMO") web3j: Web3j,
        nativeTokenAPIManager: NativeTokenAPIManager
    ): WalletManager {
        return WalletManager(
            dataStoreManager = dataStoreManager,
            identityManager = identityManager,
            pointsManager = pointsManager,
            web3j = web3j,
            nativeTokenAPIManager = nativeTokenAPIManager,
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
        dataStoreManager: SecureSharedPrefsManager, rarimoContractManager: RarimoContractManager
    ): IdentityManager {
        return IdentityManager(dataStoreManager, rarimoContractManager)
    }

    @Provides
    @Singleton
    @Named("RARIMO")
    fun web3(): Web3j {
        return Web3j.build(HttpService(BaseConfig.EVM_RPC_URL))
    }


    @Provides
    @Singleton
    @Named("Test")
    fun web3Test(): Web3j {
        return Web3j.build(HttpService(BaseConfig.VOTING_RPC_URL))
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideLikenessApiManager(@Named("jsonApiRetrofit") retrofit: Retrofit): LikenessApiManager {
        return LikenessApiManager(retrofit.create(LikenessApi::class.java))
    }

    @Provides
    @Singleton
    fun provideHiddenPrizeApiManager(
        @Named("jsonApiRetrofit") retrofit: Retrofit,
        authManager: AuthManager
    ): HiddenPrizeApiManager {
        return HiddenPrizeApiManager(retrofit.create(HiddenPrizeApi::class.java), authManager)
    }

    @Provides
    @Singleton
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationsDao {
        return appDatabase.notificationsDao()
    }

    @Provides
    @Singleton
    fun provideVotingDao(appDatabase: AppDatabase): VotingDao {
        return appDatabase.votingDao()
    }

    @Provides
    @Singleton
    fun provideNotificationsRepository(notificationsDao: NotificationsDao): NotificationsRepository {
        return NotificationsRepository(notificationsDao)
    }

    @Provides
    @Singleton
    fun provideDriveBackupRepository(
        @ApplicationContext context: Context
    ): DriveBackupManager = DriveBackupManager(context)


    @Provides
    @Singleton
    fun provideVotingRepository(votingDao: VotingDao): VotingRepository {
        return VotingRepository(votingDao)
    }

    @Provides
    @Singleton
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }
    @Provides
    @Singleton
    @Named("nativeTokenRetrofit")
    fun nativeTokenRetrofit(): Retrofit {
        return Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        ).baseUrl(BaseConfig.RELAYER_URL).client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        ).build()
    }
    @Provides
    @Singleton
    fun provideNativeTokenAPI(@Named("nativeTokenRetrofit")retrofit: Retrofit): NativeTokenAPI {
        return retrofit.create(NativeTokenAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideNativeTokenAPIManager(nativeTokenAPI:NativeTokenAPI): NativeTokenAPIManager {
        return NativeTokenAPIManager(nativeTokenAPI)
    }


    @Provides
    @Singleton
    fun provideNotificationManager(
        notificationsRepository: NotificationsRepository, passportManager: PassportManager
    ): NotificationManager {
        return NotificationManager(notificationsRepository, passportManager)
    }


}
