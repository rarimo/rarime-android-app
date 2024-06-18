package com.distributedLab.rarime.api.airdrop

import android.content.Context
import android.util.Log
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.airdrop.models.AirDropStatuses
import com.distributedLab.rarime.api.airdrop.models.CreateAirDrop
import com.distributedLab.rarime.api.airdrop.models.CreateAirDropAttributes
import com.distributedLab.rarime.api.airdrop.models.CreateAirDropBody
import com.distributedLab.rarime.data.ProofTxFull
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import com.distributedLab.rarime.manager.ContractManager
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.modules.passportScan.models.EDocument
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.ZkpUtil
import com.distributedLab.rarime.util.data.ZkProof
import com.distributedLab.rarime.util.decodeHexString
import com.google.gson.Gson
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
    private val contractManager: ContractManager,
    private val identityManager: IdentityManager,
    private val dataStoreManager: SecureSharedPrefsManager,
) {
    private var _isAirdropClaimed = MutableStateFlow(false)

    val isAirDropClaimed: StateFlow<Boolean>
        get() = _isAirdropClaimed.asStateFlow()

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
            airDropAPIManager.getAirDropParams()!!
        }

        val queryProofInputs = profiler.buildAirdropQueryIdentityInputs(
            eDocument.dg1!!.decodeHexString(),
            smtProofJson.toByteArray(Charsets.UTF_8),
            airDropParams.data.attributes.query_selector,
            registrationProof.pub_signals[0],
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

    private suspend fun createAirDrop(zkProof: ZkProof) {
        val rarimoAddress = identityManager.rarimoAddress()

        val payload = CreateAirDropBody(
            data = CreateAirDrop(
                type = "create_airdrop",
                attributes = CreateAirDropAttributes(
                    address = rarimoAddress,
//                    "SHA256withRSA",
                    zk_proof = zkProof
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
        val registrationProof = dataStoreManager.readRegistrationProof()!!

        withContext(Dispatchers.Default) {
            val proof = generateAirdropQueryProof(registrationProof, eDocument, identityManager.privateKeyBytes!!)

            createAirDrop(proof)

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

            _isAirdropClaimed.value = true
        }
    }

    suspend fun getAirDropByNullifier() {
        val nullifier = identityManager.getUserAirDropNullifier()

        try {
            val response = airDropAPIManager.getAirDropByNullifier(nullifier)

            _isAirdropClaimed.value = response.data.attributes.status == AirDropStatuses.COMPLETED.value
        } catch (e: Exception) {
            _isAirdropClaimed.value = false
        }

    }
}