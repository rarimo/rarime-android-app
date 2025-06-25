package com.rarilabs.rarime.manager

import android.content.Context
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.airdrop.AirDropAPIManager
import com.rarilabs.rarime.api.airdrop.models.AirDropStatuses
import com.rarilabs.rarime.api.airdrop.models.CreateAirDrop
import com.rarilabs.rarime.api.airdrop.models.CreateAirDropAttributes
import com.rarilabs.rarime.api.airdrop.models.CreateAirDropBody
import com.rarilabs.rarime.data.ProofTxFull
import com.rarilabs.rarime.data.tokens.TokenType
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.data.UniversalProof
import com.rarilabs.rarime.util.decodeHexString
import identity.Identity
import identity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class AirDropManager @Inject constructor(
    private val airDropAPIManager: AirDropAPIManager,
    private val context: Context,
    private val rarimoContractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
    private val dataStoreManager: SecureSharedPrefsManager,
) {
    private var _isAirdropClaimed = MutableStateFlow(false)

    val isAirDropClaimed: StateFlow<Boolean>
        get() = _isAirdropClaimed.asStateFlow()

    private suspend fun generateAirdropQueryProof(
        registrationProof: UniversalProof, eDocument: EDocument, privateKey: ByteArray
    ): GrothProof {
        val assetContext: Context = context.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)
        val stateKeeperContract = rarimoContractManager.getStateKeeper()

        val registrationSmtContract =
            rarimoContractManager.getPoseidonSMT(BaseConfig.REGISTRATION_SMT_CONTRACT_ADDRESS)

        val proofIndex = Identity.calculateProofIndex(
            registrationProof.getPubSignals()[0], registrationProof.getPubSignals()[3]
        )

        val smtProofRaw = withContext(Dispatchers.IO) {
            registrationSmtContract.getProof(proofIndex).send()
        }
        val smtProof = ProofTxFull.fromContractProof(smtProofRaw)
        val smtProofJson = Gson().toJson(smtProof)


        val profiler = Profile().newProfile(privateKey)


        val passportInfoRaw = withContext(Dispatchers.IO) {
            stateKeeperContract.getPassportInfo(
                Identity.bigIntToBytes(registrationProof.getPubSignals()[0])
            ).send()
        }

        val passportInfo = passportInfoRaw.component1()
        val identityInfo = passportInfoRaw.component2()

        val airDropParams = withContext(Dispatchers.IO) {
            airDropAPIManager.getAirDropParams()
        }

        val queryProofInputs = profiler.buildAirdropQueryIdentityInputs(
            eDocument.dg1!!.decodeHexString(),
            smtProofJson.toByteArray(Charsets.UTF_8),
            airDropParams.data.attributes.query_selector,
            registrationProof.getPubSignals()[0],
            identityInfo.issueTimestamp.toString(),
            passportInfo.identityReissueCounter.toString(),
            airDropParams.data.attributes.event_id,
            airDropParams.data.attributes.started_at
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

    private suspend fun createAirDrop(proof: GrothProof) {
        val rarimoAddress = identityManager.rarimoAddress()

        val payload = CreateAirDropBody(
            data = CreateAirDrop(
                type = "create_airdrop",
                attributes = CreateAirDropAttributes(
                    address = rarimoAddress,
//                    "SHA256withRSA",
                    zk_proof = proof
                )
            )
        )


        withContext(Dispatchers.IO) {
            airDropAPIManager.createAirDrop(payload)
        }
    }

    suspend fun claimAirdrop() {
        if (_isAirdropClaimed.value) return

        val eDocument = dataStoreManager.readEDocument()!!
        val registrationProof = dataStoreManager.readUniversalProof()!!

        withContext(Dispatchers.Default) {
            val proof = generateAirdropQueryProof(
                registrationProof,
                eDocument,
                identityManager.privateKeyBytes!!
            )

            createAirDrop(proof)

            delay(10.seconds)

            val transaction = Transaction(
                id = "1",
                amount = Constants.AIRDROP_REWARD,
                date = Date(),
                state = TransactionState.INCOMING,
                from = "",
                to = identityManager.evmAddress(),
                tokenType = TokenType.POINTS,
                operationType = TransactionType.TRANSFER

            )

            // FIXME: use tx from token and remove transaction key from store
            dataStoreManager.addTransaction(transaction)

            _isAirdropClaimed.value = true
        }
    }

    suspend fun getAirDropByNullifier() {
        val nullifier = identityManager.getUserAirDropNullifier()

        try {
            val response = airDropAPIManager.getAirDropByNullifier(nullifier)

            _isAirdropClaimed.value =
                response.data.attributes.status == AirDropStatuses.COMPLETED.value
        } catch (e: Exception) {
            _isAirdropClaimed.value = false
        }

    }
}