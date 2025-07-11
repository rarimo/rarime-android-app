package com.rarilabs.rarime.manager

import android.content.Context
import coil.network.HttpException
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.PointsAPIManager
import com.rarilabs.rarime.api.points.models.BaseEvents
import com.rarilabs.rarime.api.points.models.ClaimEventBody
import com.rarilabs.rarime.api.points.models.ClaimEventData
import com.rarilabs.rarime.api.points.models.CreateBalanceAttributes
import com.rarilabs.rarime.api.points.models.CreateBalanceBody
import com.rarilabs.rarime.api.points.models.CreateBalanceData
import com.rarilabs.rarime.api.points.models.JoinRewardsProgramRequest
import com.rarilabs.rarime.api.points.models.JoinRewardsProgramRequestAttributes
import com.rarilabs.rarime.api.points.models.JoinRewardsProgramRequestData
import com.rarilabs.rarime.api.points.models.PointsBalanceBody
import com.rarilabs.rarime.api.points.models.PointsEventBody
import com.rarilabs.rarime.api.points.models.PointsEventStatuses
import com.rarilabs.rarime.api.points.models.PointsEventsListBody
import com.rarilabs.rarime.api.points.models.PointsEventsTypesBody
import com.rarilabs.rarime.api.points.models.PointsLeaderBoardBody
import com.rarilabs.rarime.api.points.models.VerifyPassportAttributes
import com.rarilabs.rarime.api.points.models.VerifyPassportBody
import com.rarilabs.rarime.api.points.models.VerifyPassportData
import com.rarilabs.rarime.api.points.models.WithdrawBody
import com.rarilabs.rarime.api.points.models.WithdrawPayload
import com.rarilabs.rarime.api.points.models.WithdrawPayloadAttributes
import com.rarilabs.rarime.config.Keys
import com.rarilabs.rarime.data.ProofTxFull
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.data.UniversalProof
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.hmacSha256
import identity.Identity
import identity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import java.math.BigInteger
import javax.inject.Inject

class PointsManager @Inject constructor(
    private val context: Context,
    private val contractManager: RarimoContractManager,
    private val pointsAPIManager: PointsAPIManager,
    private val identityManager: IdentityManager,
    private val authManager: AuthManager,
    private val passportManager: PassportManager,
    private val secureSharedPrefsManager: SecureSharedPrefsManager
) {

    suspend fun createPointsBalance(referralCode: String?) {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        withContext(Dispatchers.IO) {
            pointsAPIManager.createPointsBalance(
                CreateBalanceBody(
                    data = CreateBalanceData(
                        id = userNullifierHex,
                        type = "create_balance",
                        attributes = CreateBalanceAttributes(
                            referredBy = if (referralCode.isNullOrEmpty()) null else referralCode
                        )
                    )
                ), "Bearer ${authManager.accessToken}"
            )
        }
    }


    suspend fun getMaintenanceStatus(): Boolean {
        return try {
            pointsAPIManager.getMaintenanceStatus().data.attributes.maintenance
        } catch (e: Exception) {
            false
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun joinRewardProgram(eDocument: EDocument) {

        val accessJwt = identityManager.getUserPointsNullifierHex()


        val countryName = eDocument.personDetails?.nationality!!

        val anonymousId = Identity.calculateAnonymousID(
            eDocument.dg1!!.decodeHexString(), BaseConfig.POINTS_SVC_ID
        )

        val hmacMessage = Identity.calculateHmacMessage(accessJwt, countryName, anonymousId)


        val hmacSignature = hmacSha256(key = Keys.joinProgram.decodeHexString(), hmacMessage)

        val requestPayload = JoinRewardsProgramRequest(
            data = JoinRewardsProgramRequestData(
                id = accessJwt,
                type = "verify_passport",
                attributes = JoinRewardsProgramRequestAttributes(
                    anonymous_id = anonymousId.toHexString(), country = countryName
                )
            )
        )

        pointsAPIManager.joinRewordsProgram(
            accessJwt,
            hmacSignature.toHexString(),
            requestPayload,
            "Bearer ${authManager.accessToken}"
        )
    }

    suspend fun getPointsBalance(): PointsBalanceBody? {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }


        val response = try {
            val balance = pointsAPIManager.getPointsBalance(
                userNullifierHex, "Bearer ${authManager.accessToken}", mapOf(
                    "rank" to "true",
                    "referral_codes" to "true",
                )
            )

            if (balance == null) {
                createPointsBalance(
                    secureSharedPrefsManager.getDeferredReferralCode() ?: Keys.genesisReferralCode
                )
                pointsAPIManager.getPointsBalance(
                    userNullifierHex, "Bearer ${authManager.accessToken}", mapOf(
                        "rank" to "true",
                        "referral_codes" to "true",
                    )
                )
            } else {
                balance
            }

        } catch (e: Exception) {
            null
        }

        return response
    }

    private suspend fun generateVerifyPassportQueryProof(
        eDocument: EDocument, privateKey: ByteArray
    ): GrothProof {

        val lightProofData = secureSharedPrefsManager.getLightRegistrationData()

        val assetContext: Context = context.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets


        val zkp = ZKPUseCase(context, assetManager)

        val stateKeeperContract = contractManager.getStateKeeper()

        val registrationSmtContract = contractManager.getPoseidonSMT(
            BaseConfig.REGISTRATION_SMT_CONTRACT_ADDRESS
        )

        val passportInfoKey: String = if (lightProofData != null) {
            if (passportManager.passport.value!!.dg15.isNullOrEmpty()) {
                BigInteger(Numeric.hexStringToByteArray(lightProofData.passport_hash)).toString()
            } else {
                BigInteger(Numeric.hexStringToByteArray(lightProofData.public_key)).toString()
            }
        } else {
            if (passportManager.passport.value!!.dg15.isNullOrEmpty()) {
                identityManager.registrationProof.value!!.getPassportHash() //lightProofData.passport_hash
            } else {
                identityManager.registrationProof.value!!.getPublicKey() //lightProofData.public_key
            }
        }

        val proofIndex = Identity.calculateProofIndex(
            passportInfoKey,
            if (lightProofData == null) identityManager.registrationProof.value!!.getIdentityKey()
            else identityManager.registrationProof.value!!.getIdentityKey()
        )

        var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

        if (passportInfoKeyBytes.size > 32) {
            passportInfoKeyBytes = ByteArray(32 - passportInfoKeyBytes.size) + passportInfoKeyBytes
        } else if (passportInfoKeyBytes.size < 32) {
            val len = 32 - passportInfoKeyBytes.size
            var tempByteArray = ByteArray(len) { 0 }
            tempByteArray += passportInfoKeyBytes
            passportInfoKeyBytes = tempByteArray
        }

        val smtProofRaw = withContext(Dispatchers.IO) {
            registrationSmtContract.getProof(proofIndex).send()
        }
        val smtProof = ProofTxFull.fromContractProof(smtProofRaw)
        val smtProofJson = Gson().toJson(smtProof)

        val profiler = Profile().newProfile(privateKey)

        val passportInfoRaw = withContext(Dispatchers.IO) {
            stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
        }

        val passportInfo = passportInfoRaw.component1()
        val identityInfo = passportInfoRaw.component2()

        val queryProofInputs = profiler.buildAirdropQueryIdentityInputs(
            eDocument.dg1!!.decodeHexString(),
            smtProofJson.toByteArray(Charsets.UTF_8),
            BaseConfig.POINTS_SVC_SELECTOR,
            passportInfoKey,
            identityInfo.issueTimestamp.toString(),
            passportInfo.identityReissueCounter.toString(),
            BaseConfig.POINTS_SVC_ID,
            BaseConfig.POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP,
        )

        ErrorHandler.logDebug("Inputs", queryProofInputs.toString())

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

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun verifyPassport() {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val eDocument = passportManager.passport.value!!

        val anonymousId = Identity.calculateAnonymousID(
            eDocument.dg1!!.decodeHexString(), BaseConfig.POINTS_SVC_ID
        )

        val hmacMessage = Identity.calculateHmacMessage(
            userNullifierHex, eDocument.personDetails!!.nationality, anonymousId
        )

        val hmacSignature = hmacSha256(Keys.joinProgram.decodeHexString(), hmacMessage)

        withContext(Dispatchers.Default) {
            val zkProof = generateVerifyPassportQueryProof(
                eDocument, identityManager.privateKeyBytes!!
            )


            pointsAPIManager.verifyPassport(
                userNullifierHex, VerifyPassportBody(
                    data = VerifyPassportData(
                        id = userNullifierHex,
                        type = "verify_passport",
                        attributes = VerifyPassportAttributes(
                            proof = zkProof,
                            country = eDocument.personDetails!!.nationality!!,
                            anonymous_id = anonymousId.toHexString()
                        )
                    )
                ), "Bearer ${authManager.accessToken}", signature = hmacSignature.toHexString()
            )
        }
    }

    suspend fun withdrawPoints(amount: String) {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }
        identityManager.registrationProof.value

        pointsAPIManager.withdrawPoints(
            userNullifierHex, WithdrawBody(
                data = WithdrawPayload(
                    id = userNullifierHex,
                    type = "withdraw",
                    attributes = WithdrawPayloadAttributes(
                        amount = amount.toLong(),
                        address = identityManager.rarimoAddress(),
                        proof = (identityManager.registrationProof.value as UniversalProof.Groth).proof.proof
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

    private suspend fun getEvents(
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
                val response = pointsAPIManager.getEventsList(
                    authorization = "Bearer ${authManager.accessToken}", params = params
                )

                response
            } catch (e: HttpException) {
                PointsEventsListBody(data = emptyList())
            } catch (e: Exception) {
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
            BaseEvents.EARLY_TEST.value
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
                    id = eventId, authorization = "Bearer ${authManager.accessToken}"
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getLeaderBoard(): PointsLeaderBoardBody {
        return withContext(Dispatchers.IO) {
            try {
                val response = pointsAPIManager.getLeaderboard()
                response
            } catch (e: Exception) {
                ErrorHandler.logError("PointsManager:getLeaderBoard", e.toString(), e)
                PointsLeaderBoardBody(data = emptyList())
            }
        }
    }

    suspend fun getActiveEventsByName(name: String): PointsEventsListBody {
        val statuses = listOf(
            PointsEventStatuses.FULFILLED.value,
        )

        val names = listOf(
            name
        )

        return getEvents(
            filterParams = mapOf(
                "filter[status]" to statuses.joinToString(","),
                "filter[meta.static.name]" to names.joinToString(","),
            )
        )
    }

    suspend fun claimPointsByEventId(eventId: String, type: String): PointsEventBody {
        val body = ClaimEventBody(data = ClaimEventData(id = eventId, type = type))
        return withContext(Dispatchers.IO) {
            try {
                val response = pointsAPIManager.claimPointsByEvent(eventId, body)
                response
            } catch (e: Exception) {
                ErrorHandler.logError("claimPointsByEventId", e.toString(), e)
                throw e
            }
        }
    }

    fun saveDeferredReferralCode(referralCode: String) {
        secureSharedPrefsManager.saveDeferredReferralCode(referralCode)
    }

    fun getDeferredReferralCode(): String? {
        return secureSharedPrefsManager.getDeferredReferralCode()
    }
}