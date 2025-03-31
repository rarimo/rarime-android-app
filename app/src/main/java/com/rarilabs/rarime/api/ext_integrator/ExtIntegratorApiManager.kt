package com.rarilabs.rarime.api.ext_integrator

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.models.LightSignatureCallbackRequest
import com.rarilabs.rarime.api.ext_integrator.models.LightSignatureCallbackRequestAttributes
import com.rarilabs.rarime.api.ext_integrator.models.LightSignatureCallbackRequestData
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenCallbackRequest
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenCallbackRequestAttributes
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenCallbackRequestData
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.contracts.rarimo.StateKeeper
import com.rarilabs.rarime.data.ProofTxFull
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import java.math.BigInteger
import javax.inject.Inject

class ExtIntegratorApiManager @Inject constructor(
    private val extIntegratorAPI: ExtIntegratorAPI,
    private val contractManager: RarimoContractManager,
    private val sharedPreferences: SecureSharedPrefsManager,
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager,
) {
    suspend fun queryProofCallback(url: String, proof: ZkProof, userIdHash: String) {
        return withContext(Dispatchers.IO) {
            val payload = QueryProofGenCallbackRequest(
                data = QueryProofGenCallbackRequestData(
                    id = userIdHash,
                    attributes = QueryProofGenCallbackRequestAttributes(
                        proof = proof
                    )
                )
            )
            val str = Gson().toJson(payload)
            Log.i("payload", str)
            try {
                extIntegratorAPI.queryProofCallback(
                    url,
                    payload
                )
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun lightSignatureCallback(
        url: String,
        pubSignals: List<String>,
        signature: String,
        userIdHash: String
    ) {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.lightSignatureCallback(
                    url,
                    LightSignatureCallbackRequest(
                        data = LightSignatureCallbackRequestData(
                            id = userIdHash,
                            attributes = LightSignatureCallbackRequestAttributes(
                                pub_signals = pubSignals,
                                signature = signature
                            )
                        )
                    )
                )
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun queryProofData(url: String): QueryProofGenResponse {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.queryProofData(url)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun queryIpfsData(url: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = extIntegratorAPI.queryIpfsData(url)

                Gson().toJson(response)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    private var _passportInfo = MutableStateFlow<StateKeeper.PassportInfo?>(null)
    val passportInfo: StateFlow<StateKeeper.PassportInfo?>
        get() = _passportInfo.asStateFlow()

    private var _identityInfo = MutableStateFlow<StateKeeper.IdentityInfo?>(null)
    val identityInfo: StateFlow<StateKeeper.IdentityInfo?>
        get() = _identityInfo.asStateFlow()

    suspend fun loadPassportInfo() {
        val eDocument = passportManager.passport.value

        if (eDocument == null) return

        val passportInfoKey = passportManager.getPassportInfoKey(
            eDocument,
            identityManager.registrationProof.value!!
        )

        val stateKeeperContract = contractManager.getStateKeeper()

        val passportInfoRaw = withContext(Dispatchers.IO) {
            stateKeeperContract.getPassportInfo(passportInfoKey).send()
        }

        _passportInfo.value = passportInfoRaw.component1()
        _identityInfo.value = passportInfoRaw.component2()
    }

    private suspend fun getQueryParams(): Pair<ByteArray, String> {

        val lightProofData = sharedPreferences.getLightRegistrationData()

        val registrationSmtContract = contractManager.getPoseidonSMT(
            BaseConfig.REGISTRATION_SMT_CONTRACT_ADDRESS
        )

        val passportInfoKey: String =
            if (lightProofData != null) {
                if (passportManager.passport.value!!.dg15.isNullOrEmpty()) {
                    BigInteger(Numeric.hexStringToByteArray(lightProofData.passport_hash)).toString()
                } else {
                    BigInteger(Numeric.hexStringToByteArray(lightProofData.public_key)).toString()
                }
            } else {
                if (passportManager.passport.value!!.dg15.isNullOrEmpty()) {
                    identityManager.registrationProof.value!!.pub_signals[1] //lightProofData.passport_hash
                } else {
                    identityManager.registrationProof.value!!.pub_signals[0] //lightProofData.public_key
                }
            }

        val proofIndex = Identity.calculateProofIndex(
            passportInfoKey,
            if (lightProofData == null) identityManager.registrationProof.value!!.pub_signals[3]
            else identityManager.registrationProof.value!!.pub_signals[2]
        )

        val smtProofRaw = withContext(Dispatchers.IO) {
            registrationSmtContract.getProof(proofIndex).send()
        }
        val smtProof = ProofTxFull.fromContractProof(smtProofRaw)
        val smtProofJson = Gson().toJson(smtProof)

        return Pair(smtProofJson.toByteArray(), passportInfoKey)

    }

    suspend fun generateQueryProof(context: Context, queryProofParametersRequest: QueryProofGenResponse): ZkProof? {
        if (passportInfo.value == null || identityInfo.value == null) return null

        val assetContext: Context = context.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)

        val queryParams = getQueryParams()

        val profiler = identityManager.getProfiler()

        val targets_identity_counter_upper_bound =
            if (passportInfo.value!!.identityReissueCounter.toLong() > queryProofParametersRequest.data.attributes.identity_counter_upper_bound) passportInfo.value!!.identityReissueCounter.toString()
            else queryProofParametersRequest.data.attributes.identity_counter_upper_bound.toString()

        val dg1 = passportManager.passport.value!!.dg1!!.decodeHexString()
        val smtProofJSON = queryParams.first
        val selector = queryProofParametersRequest.data.attributes.selector
        val pkPassportHash = queryParams.second
        val issueTimestamp = identityInfo.value!!.issueTimestamp.toString()
        val identityCounter = passportInfo.value!!.identityReissueCounter.toString()
        val eventID = queryProofParametersRequest.data.attributes.event_id
        val eventData = queryProofParametersRequest.data.attributes.event_data
        val TimestampLowerbound = queryProofParametersRequest.data.attributes.timestamp_lower_bound

        val TimestampUpperbound =
            if (identityInfo.value!!.issueTimestamp.toString().toULong() >= queryProofParametersRequest.data.attributes.timestamp_upper_bound.toULong())
                (identityInfo.value!!.issueTimestamp.toString().toULong() + 1u).toString()
            else
                queryProofParametersRequest.data.attributes.timestamp_upper_bound

        val IdentityCounterLowerbound = queryProofParametersRequest.data.attributes.identity_counter_lower_bound.toString()
        val IdentityCounterUpperbound = (passportInfo.value!!.identityReissueCounter.toLong() + 1).toString()
        val ExpirationDateLowerbound = queryProofParametersRequest.data.attributes.expiration_date_lower_bound
        val ExpirationDateUpperbound = queryProofParametersRequest.data.attributes.expiration_date_upper_bound // largets_identity_counter_upper_bound
        val BirthDateLowerbound = queryProofParametersRequest.data.attributes.birth_date_lower_bound
        val BirthDateUpperbound = queryProofParametersRequest.data.attributes.birth_date_upper_bound
        val CitizenshipMask = queryProofParametersRequest.data.attributes.citizenship_mask

        Log.i(
            "generateQueryProof", """
            dg1: ${Numeric.toHexString(dg1)}
            smtProofJSON: ${smtProofJSON.decodeToString()}
            selector: $selector
            pkPassportHash: $pkPassportHash
            issueTimestamp: $issueTimestamp
            identityCounter: $identityCounter
            eventID: $eventID
            eventData: $eventData
            TimestampLowerbound: $TimestampLowerbound
            TimestampUpperbound: $TimestampUpperbound
            IdentityCounterLowerbound: $IdentityCounterLowerbound
            IdentityCounterUpperbound: $IdentityCounterUpperbound
            ExpirationDateLowerbound: $ExpirationDateLowerbound
            ExpirationDateUpperbound: $ExpirationDateUpperbound
            BirthDateLowerbound: $BirthDateLowerbound
            BirthDateUpperbound: $BirthDateUpperbound
            CitizenshipMask: $CitizenshipMask
        """.trimIndent()
        )

        val queryProofInputs = profiler.buildQueryIdentityInputs(
            dg1,
            smtProofJSON,
            selector,

            pkPassportHash,

            issueTimestamp,
            identityCounter,

            eventID,
            eventData,
            TimestampLowerbound,
            TimestampUpperbound,

            IdentityCounterLowerbound,
            IdentityCounterUpperbound,
            ExpirationDateLowerbound,
            ExpirationDateUpperbound,
            BirthDateLowerbound,
            BirthDateUpperbound,
            CitizenshipMask,
        )

        val queryProof = withContext(Dispatchers.Default) {
            zkp.generateZKP(
                "circuit_query_zkey.zkey",
                R.raw.query_identity_dat,
                queryProofInputs,
                ZkpUtil::queryIdentity
            )
        }

        Log.i("queryProof", Gson().toJson(queryProof))

        return queryProof
    }
}