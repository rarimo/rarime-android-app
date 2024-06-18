package com.distributedLab.rarime.api.points

import android.content.Context
import android.util.Log
import coil.network.HttpException
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.auth.AuthManager
import com.distributedLab.rarime.api.points.models.BaseEvents
import com.distributedLab.rarime.api.points.models.CreateBalanceAttributes
import com.distributedLab.rarime.api.points.models.CreateBalanceBody
import com.distributedLab.rarime.api.points.models.CreateBalanceData
import com.distributedLab.rarime.api.points.models.PointsBalanceBody
import com.distributedLab.rarime.api.points.models.PointsEventBody
import com.distributedLab.rarime.api.points.models.PointsEventStatuses
import com.distributedLab.rarime.api.points.models.PointsEventsListBody
import com.distributedLab.rarime.api.points.models.PointsEventsTypesBody
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
            ),
            "Bearer ${authManager.accessToken.value!!}"
        )
    }

    suspend fun getPointsBalance(): PointsBalanceBody? {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val response = pointsAPIManager.getPointsBalance(
            userNullifierHex,
            "Bearer ${authManager.accessToken.value!!}",
            mapOf(
                "rank" to "true",
                "referral_codes" to "true",
            )
        )

        _pointsBalance.value = response

        return response
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

        val queryProofInputs = profiler.buildAirdropQueryIdentityInputs(
            eDocument.dg1!!.decodeHexString(),
            smtProofJson.toByteArray(Charsets.UTF_8),
            "23073",
            registrationProof.pub_signals[0],
            identityInfo.issueTimestamp.toString(),
            passportInfo.identityReissueCounter.toString(),
            BaseConfig.POINTS_SVC_ID,
            1715688000,
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

    suspend fun getEventTypes(): PointsEventsTypesBody {
        return withContext(Dispatchers.IO) {
            try {
                pointsAPIManager.getEventTypes(
                    mapOf()
                )
            } catch (e: HttpException) {
                PointsEventsTypesBody(data = emptyList())
            }
        }
    }

    suspend fun getEvents(
        filterParams: Map<String, String> = mapOf()
    ): PointsEventsListBody {
        val userPointsNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userPointsNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val params = filterParams.toMutableMap().apply {
            put("filter[nullifier]", userPointsNullifierHex)
        }

        return withContext(Dispatchers.IO) {
            try {
                val response =
                    pointsAPIManager.getEventsList(
                        authorization = "Bearer ${authManager.accessToken.value!!}",
                        params = params
                    )

                Log.i("PointsManager", response.toString())

                response ?: PointsEventsListBody(data = emptyList())
            } catch (e: HttpException) {
                PointsEventsListBody(data = emptyList())
            }
        }
    }

    suspend fun getTimeLimitedEvents(): PointsEventsListBody {
        return getEvents(
            mapOf(
                "filter[status]" to PointsEventStatuses.OPEN.value,
                "filter[has_expiration]" to "true",
            )
        )
    }

    suspend fun getActiveEvents(): PointsEventsListBody {
        val names = listOf(
            BaseEvents.REFERRAL_COMMON.value,
            BaseEvents.PASSPORT_SCAN.value,
        )

        val statuses = listOf(
            PointsEventStatuses.OPEN.value,
            PointsEventStatuses.FULFILLED.value,
        )

        return getEvents(
            filterParams = mapOf(
                "filter[status]" to statuses.joinToString(","),
                "filter[meta.static.name]" to names.joinToString(","),
            )
        )
    }

    suspend fun getEventById(eventId: String): PointsEventBody? {
        return withContext(Dispatchers.IO) {
            try {
                pointsAPIManager.getEvent(
                    id = eventId,
                    authorization = "Bearer ${authManager.accessToken.value!!}"
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}