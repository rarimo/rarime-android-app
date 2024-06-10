package com.distributedLab.rarime.modules.common

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.RarimoChains
import com.distributedLab.rarime.data.tokens.Erc20Token
import com.distributedLab.rarime.data.tokens.PointsToken
import com.distributedLab.rarime.data.tokens.RarimoToken
import com.distributedLab.rarime.data.tokens.Token
import com.distributedLab.rarime.domain.data.AirdropRequest
import com.distributedLab.rarime.domain.data.AirdropRequestAttributes
import com.distributedLab.rarime.domain.data.AirdropRequestData
import com.distributedLab.rarime.domain.data.ProofTxFull
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.manager.ApiServiceRemoteData
import com.distributedLab.rarime.manager.ContractManager
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.ZkpUtil
import com.distributedLab.rarime.util.data.ZkProof
import com.distributedLab.rarime.util.decodeHexString
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import identity.Identity
import identity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

data class WalletAssetJSON(
    val tokenSymbol: String,
    val balance: String,
    val transactions: List<Transaction>
)

class WalletAsset(val userAddress: String, val token: Token) {
    var balance = mutableStateOf(BigInteger.ZERO)

    var transactions = mutableStateOf(listOf<Transaction>())

    fun toJSON(): String {
        return Gson().toJson(
            WalletAssetJSON(
                tokenSymbol = token.symbol,
                balance = balance.value.toString(),
                transactions = transactions.value
            )
        )
    }

    suspend fun loadBalance() {
        balance.value = token.balanceOf(userAddress)
    }

    suspend fun loadTransactions() {
        transactions.value = listOf()
    }

    fun humanBalance(): Double {
        return balance.value.divide(
            BigInteger.TEN.pow(token.decimals)
        ).toDouble()
    }
}

@Singleton
class WalletManager @Inject constructor(
    private val context: Context,
    private val dataStoreManager: SecureSharedPrefsManager,
    private val contractManager: ContractManager,
    private val apiServiceManager: ApiServiceRemoteData,
    private val identityManager: IdentityManager,
) {
    private var _walletAssets = MutableStateFlow(
        dataStoreManager.readWalletAssets(
            listOf(
                WalletAsset(
                    identityManager.rarimoAddress(),
                    RarimoToken(
                        BaseConfig.RARIMO_CHAINS[RarimoChains.MainnetBeta.chainId]!!, // FIXME: !!
                        identityManager,
                        apiServiceManager,
                    )
                ),
                WalletAsset(
                    identityManager.evmAddress,
                    Erc20Token("0x0000000000000000000000000000000000000000")
                ),
                WalletAsset(
                    identityManager.rarimoAddress,
                    PointsToken()
                )
            )
        )
    )
    val walletAssets: StateFlow<List<WalletAsset>>
        get() = _walletAssets.asStateFlow()

    private val _selectedWalletAsset = MutableStateFlow(dataStoreManager.readSelectedWalletAsset(walletAssets.value))

    val selectedWalletAsset: StateFlow<WalletAsset>
        get() = _selectedWalletAsset.asStateFlow()

    fun setSelectedWalletAsset(walletAsset: WalletAsset) {
        _selectedWalletAsset.value = walletAsset
        Log.i("setSelectedWalletAsset", _selectedWalletAsset.value.toJSON())

        dataStoreManager.saveSelectedWalletAsset(walletAsset)
    }

    suspend fun loadBalances() {
        withContext(Dispatchers.IO) {
            _walletAssets.value.forEach {
                it.token.loadDetails()

                // TODO: Promise.all
                it.loadBalance()
                it.loadTransactions()
            }

            dataStoreManager.saveWalletAssets(_walletAssets.value)
        }
    }

    private var isAirdropClaimed = mutableStateOf(false)

    private suspend fun generateAirdropQueryProof(
        registrationProof: ZkProof, eDocument: EDocument, privateKey: ByteArray
    ): ZkProof {
        val assetContext: Context = context.createPackageContext("com.distributedLab.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)
        val registrationContract = contractManager.getRegistration()

        val registrationSmtAddress = withContext(Dispatchers.IO) {
            registrationContract.registrationSmt().send()
        }

        val registrationSmtContract = contractManager.getPoseidonSMT(registrationSmtAddress)


        val proofIndex = Identity.calculateProofIndex(
            registrationProof.pub_signals[0], registrationProof.pub_signals[2]
        )

        val smtProofRaw = withContext(Dispatchers.IO) {
            registrationSmtContract.getProof(proofIndex).send()
        }
        val smtProof = ProofTxFull.fromContractProof(smtProofRaw)
        val smtProofJson = Gson().toJson(smtProof)


        val profiler = Profile().newProfile(privateKey)

        Log.i("pubSignal", Identity.bigIntToBytes(registrationProof.pub_signals[0]).size.toString())


        val passportInfoRaw = withContext(Dispatchers.IO) {
            registrationContract.getPassportInfo(
                Identity.bigIntToBytes(registrationProof.pub_signals[0])
            ).send()
        }

        val passportInfo = passportInfoRaw.component1()
        val identityInfo = passportInfoRaw.component2()

        val airDropParams = withContext(Dispatchers.IO) {
            apiServiceManager.getAirdropParams()!!
        }


        val queryProofInputs = profiler.buildAirdropQueryIdentityInputs(
            eDocument.dg1!!.decodeHexString(),
            smtProofJson.toByteArray(Charsets.UTF_8),
            airDropParams.data.attributes.query_selector,
            registrationProof.pub_signals[0],
            identityInfo.issueTimestamp.toString(),
            passportInfo.identityReissueCounter.toString(),
            airDropParams.data.attributes.event_id,
            airDropParams.data.attributes.started_at.toLong()
        )

        val queryProof = withContext(Dispatchers.Default) {
            zkp.generateZKP(
                "circuit_query_zkey.zkey",
                R.raw.query_identity_dat,
                queryProofInputs,
                ZkpUtil::queryIdentity
            )
        }

        return queryProof
    }

    private suspend fun airDrop(zkProof: ZkProof) {
        val rarimoAddress = identityManager.rarimoAddress()

        Log.i("airDrop", Gson().toJson(zkProof))

        val payload = AirdropRequest(
            data = AirdropRequestData(
                type = "create_airdrop", attributes = AirdropRequestAttributes(
                    rarimoAddress, "SHA256withRSA", zkProof
                )
            )
        )

        val gson = GsonBuilder().setPrettyPrinting().create()
        Log.i("PAYLOAD", gson.toJson(payload))


        withContext(Dispatchers.IO) {
            val resp = apiServiceManager.sendQuery(payload)
        }
    }

    suspend fun claimAirdrop() {
        if (isAirdropClaimed.value) return
        val eDocument = dataStoreManager.readEDocument()!!
        val registrationProof = dataStoreManager.readRegistrationProof()!!

        withContext(Dispatchers.Default) {
            val proof = generateAirdropQueryProof(registrationProof, eDocument, identityManager.privateKeyBytes!!)

            airDrop(proof)

            delay(10.seconds)

            val transaction = Transaction(
                id = 1,
                iconId = R.drawable.ic_airdrop,
                titleId = R.string.airdrop_tx_title,
                amount = Constants.AIRDROP_REWARD,
                date = Date(),
                state = TransactionState.INCOMING
            )

            // FIXME: use tx from token and remove transaction key from store
            dataStoreManager.addTransaction(transaction)

            loadBalances()
            isAirdropClaimed.value = true
        }


    }
}