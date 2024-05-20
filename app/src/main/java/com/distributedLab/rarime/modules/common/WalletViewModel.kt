package com.distributedLab.rarime.modules.common

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.manager.ApiServiceRemoteData
import com.distributedLab.rarime.data.manager.ContractManager
import com.distributedLab.rarime.domain.data.AirdropRequest
import com.distributedLab.rarime.domain.data.AirdropRequestAttributes
import com.distributedLab.rarime.domain.data.AirdropRequestData
import com.distributedLab.rarime.domain.data.CosmosTransferResponse
import com.distributedLab.rarime.domain.data.IdentityInfo
import com.distributedLab.rarime.domain.data.PassportInfo
import com.distributedLab.rarime.domain.data.SMTProof
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.ZkpUtil
import com.distributedLab.rarime.util.data.ZkProof
import com.distributedLab.rarime.util.decodeHexString
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import identity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class WalletViewModel @Inject constructor(
    private val application: Application,
    private val dataStoreManager: SecureSharedPrefsManager,
    private val contractManager: ContractManager,
    private val apiServiceManager: ApiServiceRemoteData
) : AndroidViewModel(application) {

    var balance = mutableDoubleStateOf(dataStoreManager.readWalletBalance())
        private set
    var isAirdropClaimed = mutableStateOf(false)
        private set
    var transactions = mutableStateOf(dataStoreManager.readTransactions())
        private set

    fun getAddress(): String {
        val privateKey = dataStoreManager.readPrivateKey() ?: return ""

        return Profile().newProfile(
            privateKey.decodeHexString()
        ).rarimoAddress
    }


    private suspend fun generateAirdropQueryProof(
        registrationProof: ZkProof, eDocument: EDocument, privateKey: ByteArray
    ): ZkProof {

        val zkp = ZKPUseCase(application as Context)
        val registrationContract = contractManager.getRegistration()



        val proofIndex = Identity.calculateProofIndex(
            registrationProof.pub_signals[0], registrationProof.pub_signals[2]
        )


        val res = withContext(Dispatchers.IO) {
            Log.i("proof index", proofIndex.toHexString())
            apiServiceManager.getProof(proofIndex)
        }!!

        val smtProof = SMTProof(
            res.root, res.siblings
        )

        val smtProofJson = Gson().toJson(smtProof)


        val profiler = Profile().newProfile(privateKey)


        val passportKeyByts = Identity.bigIntToBytes(registrationProof.pub_signals[0])

        val inputs: ByteArray = withContext(Dispatchers.IO) {
            val rawData = registrationContract.getPassportInfo(passportKeyByts).send()
            val passportInfo = PassportInfo(
                rawData.component1().activeIdentity, rawData.component1().identityReissueCounter
            )
            val identityInfo = IdentityInfo(
                rawData.component2().activePassport, rawData.component2().issueTimestamp
            )

            profiler.buildQueryIdentityInputs(
                eDocument.dg1!!.toByteArray(),
                smtProofJson.toByteArray(),
                "39",
                registrationProof.pub_signals[0],
                identityInfo.issueTimestamp.toString(),
                passportInfo.identityReissueCounter.toString(),
                "0",
                "0",
                "1",
                "0"
            )
        }
        return zkp.generateZKP(
            "circuit_query_zkey", R.raw.query_identity_dat, inputs, ZkpUtil::queryIdentity
        )
    }

    private suspend fun airDrop(zkProof: ZkProof) {
        val payload = AirdropRequest(
            data = AirdropRequestData(
                type = "create_airdrop", attributes = AirdropRequestAttributes(
                    address = getAddress(), algorithm = "SHA256withRSA", zk_proof = zkProof
                )
            )
        )

        withContext(Dispatchers.IO) {
            apiServiceManager.sendQuery(payload)
        }

    }

    fun refreshTransactions() {
        transactions.value = dataStoreManager.readTransactions()
    }

    suspend fun fetchBalance(): String {
        return withContext(Dispatchers.IO) {
            val balance = apiServiceManager.fetchBalance(getAddress())
            balance!!.balances.first().amount
        }
    }

    suspend fun claimAirdrop() {
        if (isAirdropClaimed.value) return
        val eDocument = dataStoreManager.readEDocument()!!
        val registrationProof = dataStoreManager.readRegistrationProof()!!
        val privateKey = dataStoreManager.readPrivateKey()!!.decodeHexString()

        val proof = generateAirdropQueryProof(registrationProof, eDocument, privateKey)

        airDrop(proof)

        delay(3.seconds)

        balance.doubleValue += Constants.AIRDROP_REWARD
        //balance.doubleValue = fetchBalance().toDouble()
        dataStoreManager.saveWalletBalance(balance.doubleValue)

        transactions.value = listOf(
            Transaction(
                id = 1,
                iconId = R.drawable.ic_airdrop,
                titleId = R.string.airdrop_tx_title,
                amount = Constants.AIRDROP_REWARD,
                date = Date(),
                state = TransactionState.INCOMING
            )
        )

        refreshTransactions()
        isAirdropClaimed.value = true
    }

    suspend fun sendTokens(destination: String, amount: String): CosmosTransferResponse {
        val privateKey = dataStoreManager.readPrivateKey()!!.decodeHexString()

        val profile = Profile().newProfile(privateKey)

        val response = withContext(Dispatchers.IO) {
            profile.walletSend(
                destination, amount, BaseConfig.CHAIN_ID, BaseConfig.DENOM, BaseConfig.RPC_IP
            ).toString()
        }


        return Gson().fromJson(response, CosmosTransferResponse::class.java)
    }


}