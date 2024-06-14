package com.distributedLab.rarime.api.points

import android.content.Context
import android.util.Log
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.auth.AuthManager
import com.distributedLab.rarime.api.points.models.CreateBalanceAttributes
import com.distributedLab.rarime.api.points.models.CreateBalanceBody
import com.distributedLab.rarime.api.points.models.CreateBalanceData
import com.distributedLab.rarime.api.points.models.PointsBalanceBody
import com.distributedLab.rarime.api.points.models.VerifyPassportAttributes
import com.distributedLab.rarime.api.points.models.VerifyPassportBody
import com.distributedLab.rarime.api.points.models.VerifyPassportData
import com.distributedLab.rarime.api.points.models.WithdrawBody
import com.distributedLab.rarime.api.points.models.WithdrawPayload
import com.distributedLab.rarime.api.points.models.WithdrawPayloadAttributes
import com.distributedLab.rarime.data.ProofTxFull
import com.distributedLab.rarime.manager.ContractManager
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.modules.passportScan.models.EDocument
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.ZkpUtil
import com.distributedLab.rarime.util.data.ZkProof
import com.distributedLab.rarime.util.decodeHexString
import com.google.gson.Gson
import identity.Identity
import identity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PointsManager @Inject constructor(
    private val context: Context,
    private val contractManager: ContractManager,
    private val pointsAPIManager: PointsAPIManager,
    private val identityManager: IdentityManager,
    private val authManager: AuthManager,
    private val dataStoreManager: SecureSharedPrefsManager,
) {
    private var _pointsBalance = MutableStateFlow<PointsBalanceBody?>(null)

    val pointsBalance: StateFlow<PointsBalanceBody?>
        get() = _pointsBalance.asStateFlow()

    suspend fun createPointsBalance(referralCode: String) {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        pointsAPIManager.createPointsBalance(
            CreateBalanceBody(
                data = CreateBalanceData(
                    id = userNullifierHex,
                    type = "create_balance",
                    attributes = CreateBalanceAttributes(
                        referredBy = referralCode
                    )
                )
            )
        )
    }

    suspend fun getPointsBalance(): PointsBalanceBody? {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val response = pointsAPIManager.getPointsBalance(
            userNullifierHex,
            "Bearer ${authManager.accessToken.value!!}"
        )

        _pointsBalance.value = response

        Log.i("_pointsBalance.value", _pointsBalance.value.toString())

        return response
    }

    private fun padElementsZeroes(elements: List<String>, length: Int): List<String> {
        if (elements.size >= length) {
            return elements
        }

        return elements + List(length - elements.size) { "0" }
    }

    private suspend fun generateVerifyPassportQueryProof(
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
            airDropParams.data.attributes.query_selector.toString(),
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

    suspend fun verifyPassport() {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

//        val queryProof = authManager.getAuthQueryProof(userNullifierHex)

//        var pubSignals = padElementsZeroes(queryProof.pub_signals, 22).mapIndexed { idx, it ->
//            if (idx == 12) {
//                "23073"
//            }
//
//            it
//        }

        val eDocument = dataStoreManager.readEDocument()!!
        val registrationProof = dataStoreManager.readRegistrationProof()!!

        withContext(Dispatchers.Default) {
            val zkProof = generateVerifyPassportQueryProof(
                registrationProof,
                eDocument,
                identityManager.privateKeyBytes!!
            )

            pointsAPIManager.verifyPassport(
                userNullifierHex,
                VerifyPassportBody(
                    data = VerifyPassportData(
                        id = userNullifierHex,
                        type = "verify_passport",
                        attributes = VerifyPassportAttributes(
//                        proof = ZkProof(
//                            proof = queryProof.proof,
//                            pub_signals = pubSignals,
//                        )
                            proof = zkProof
                        )
                    )
                ),
                "Bearer ${authManager.accessToken.value!!}"
            )
        }
    }

    suspend fun withdrawPoints(amount: String) {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        pointsAPIManager.withdrawPoints(
            userNullifierHex,
            WithdrawBody(
                data = WithdrawPayload(
                    id = userNullifierHex,
                    type = "withdraw",
                    attributes = WithdrawPayloadAttributes(
                        amount = amount.toLong(),
                        address = identityManager.rarimoAddress(),
                        proof = identityManager.registrationProof.value!!.proof
                    )
                )
            )
        )
    }
}