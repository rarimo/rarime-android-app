package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.contracts.rarimo.StateKeeper
import com.rarilabs.rarime.data.ProofTxFull
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.calculateAgeFromBirthDate
import com.rarilabs.rarime.util.Country
import com.rarilabs.rarime.util.DateUtil
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.decodeHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import javax.inject.Inject

@HiltViewModel
class ExtIntQueryProofHandlerViewModel @Inject constructor(
    private val extIntegratorApiManager: ExtIntegratorApiManager,
    private val passportManager: PassportManager,
    private val contractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
): ViewModel() {
    private var _queryProofParametersRequest = MutableStateFlow<QueryProofGenResponse?>(null)
    val queryProofParametersRequest: StateFlow<QueryProofGenResponse?>
        get() = _queryProofParametersRequest.asStateFlow()

    private var _passportInfo = MutableStateFlow<StateKeeper.PassportInfo?>(null)
    val passportInfo: StateFlow<StateKeeper.PassportInfo?>
        get() = _passportInfo.asStateFlow()

    private var _identityInfo = MutableStateFlow<StateKeeper.IdentityInfo?>(null)
    val identityInfo: StateFlow<StateKeeper.IdentityInfo?>
        get() = _identityInfo.asStateFlow()

    private var _fieldsParams = MutableStateFlow<MutableMap<String, String>>(mutableMapOf())
    val fieldsParams: StateFlow<Map<String, String>>
        get() = _fieldsParams.asStateFlow()

    suspend fun loadDetails(proofParamsUrl: String, redirectUrl: String) {
        _queryProofParametersRequest.value = extIntegratorApiManager.queryProofData(proofParamsUrl)

        val passportInfoKey: String = if (passportManager.passport.value!!.dg15.isNullOrEmpty()) {
            identityManager.registrationProof.value!!.pub_signals[1]
        } else {
            identityManager.registrationProof.value!!.pub_signals[0]
        }
        var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

        if (passportInfoKeyBytes.size > 32) {
            passportInfoKeyBytes = ByteArray(32 - passportInfoKeyBytes.size) + passportInfoKeyBytes
        } else if (passportInfoKeyBytes.size < 32) {
            val len = 32 - passportInfoKeyBytes.size
            var tempByteArray = ByteArray(len) { 0 }
            tempByteArray += passportInfoKeyBytes
            passportInfoKeyBytes = tempByteArray
        }

        val stateKeeperContract = contractManager.getStateKeeper()

        val passportInfoRaw = withContext(Dispatchers.IO) {
            stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
        }

        _passportInfo.value = passportInfoRaw.component1()
        _identityInfo.value = passportInfoRaw.component2()

        val tempMap = mutableMapOf<String, String>()

        try {
            var age_lower_bound_years = if (
                queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound != null &&
                queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound != "0x303030303030"
            ) {
                val birthDateUpperBoundBytes = Numeric.hexStringToByteArray(
                    queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound
                ).decodeToString()

                val mrzParsedDate = DateUtil.convertFromMrzDate(birthDateUpperBoundBytes)

                calculateAgeFromBirthDate(mrzParsedDate)
            } else {
                0
            }

            if (age_lower_bound_years > 0) {
                tempMap.set(
                    "Age",
                    "${age_lower_bound_years}+"
                )
            }
        } catch (e: Exception) {
            Log.e("age_lower_bound_years", e.message, e)
        }

        try {
            var uniqueness = queryProofParametersRequest.value?.data?.attributes?.timestamp_upper_bound?.toLong() != 0L ||
            queryProofParametersRequest.value?.data?.attributes?.identity_counter_upper_bound?.toLong() != 0L

            if (uniqueness) {
                tempMap.set(
                    "uniqueness",
                    ""
                )
            }
        } catch (e: Exception) {
            Log.e("uniqueness", e.message, e)
        }

        try {
            var nationality = if (
                queryProofParametersRequest.value?.data?.attributes?.citizenship_mask != null && queryProofParametersRequest.value?.data?.attributes?.citizenship_mask != "0x"
            ) {
                val nationality =
                    Numeric.hexStringToByteArray(queryProofParametersRequest.value?.data?.attributes?.citizenship_mask).decodeToString()

                val country = Country.fromISOCode(nationality)

                "${country.localizedName} ${country.flag}"
            } else {
                ""
            }

            if (nationality.isNotEmpty()) {
                tempMap.set(
                    "Nationality",
                    nationality
                )
            }


        } catch (e: Exception) {
            Log.e("nationality", e.message, e)
        }

        _fieldsParams.value = tempMap
    }

    suspend fun generateQueryProof(context: Context) {
        if (passportInfo.value == null || identityInfo.value == null || queryProofParametersRequest.value == null) {
            return
        }

        val assetContext: Context = context.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)

        val registrationSmtContract = contractManager.getPoseidonSMT(
            BaseConfig.REGISTRATION_SMT_CONTRACT_ADDRESS
        )

        val passportInfoKey: String = if (passportManager.passport.value!!.dg15.isNullOrEmpty()) {
            identityManager.registrationProof.value!!.pub_signals[1]
        } else {
            identityManager.registrationProof.value!!.pub_signals[0]
        }

        val proofIndex = Identity.calculateProofIndex(
            passportInfoKey, identityManager.registrationProof.value!!.pub_signals[3]
        )

        val smtProofRaw = withContext(Dispatchers.IO) {
            registrationSmtContract.getProof(proofIndex).send()
        }
        val smtProof = ProofTxFull.fromContractProof(smtProofRaw)
        val smtProofJson = Gson().toJson(smtProof)

        val profiler = identityManager.getProfiler()

        val largets_identity_counter_upper_bound = if (passportInfo.value!!.identityReissueCounter.toLong() > queryProofParametersRequest.value!!.data.attributes.identity_counter_upper_bound)
            passportInfo.value!!.identityReissueCounter.toString()
        else queryProofParametersRequest.value!!.data.attributes.identity_counter_upper_bound.toString()

        val dg1 =  passportManager.passport.value!!.dg1!!.decodeHexString()
        val smtProofJSON = smtProofJson.toByteArray(Charsets.UTF_8)
        val selector = queryProofParametersRequest.value!!.data.attributes.selector
        val pkPassportHash = passportInfoKey
        val issueTimestamp = identityInfo.value!!.issueTimestamp.toString()
        val identityCounter = passportInfo.value!!.identityReissueCounter.toString()
        val eventID = queryProofParametersRequest.value!!.data.attributes.event_id
        val eventData = queryProofParametersRequest.value!!.data.attributes.event_data
        val TimestampLowerbound = queryProofParametersRequest.value!!.data.attributes.timestamp_lower_bound

        val TimestampUpperbound =
            if (identityInfo.value!!.issueTimestamp.toLong() >= queryProofParametersRequest.value!!.data.attributes.timestamp_upper_bound.toLong())
                (identityInfo.value!!.issueTimestamp.toLong() + 1).toString()
            else queryProofParametersRequest.value!!.data.attributes.timestamp_upper_bound

        val IdentityCounterLowerbound = queryProofParametersRequest.value!!.data.attributes.identity_counter_lower_bound.toString()
        val IdentityCounterUpperbound = (passportInfo.value!!.identityReissueCounter.toLong() + 1).toString()
        val ExpirationDateLowerbound = queryProofParametersRequest.value!!.data.attributes.expiration_date_lower_bound
        val ExpirationDateUpperbound = queryProofParametersRequest.value!!.data.attributes.expiration_date_upper_bound // largets_identity_counter_upper_bound
        val BirthDateLowerbound = queryProofParametersRequest.value!!.data.attributes.birth_date_lower_bound
        val BirthDateUpperbound = queryProofParametersRequest.value!!.data.attributes.birth_date_upper_bound
        val CitizenshipMask = queryProofParametersRequest.value!!.data.attributes.citizenship_mask

        Log.i("generateQueryProof", """
            dg1: $dg1
            smtProofJSON: $smtProofJSON
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
        """.trimIndent())

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

        ErrorHandler.logDebug("Inputs", queryProofInputs.toString())

        val queryProof = withContext(Dispatchers.Default) {
            zkp.generateZKP(
                "circuit_query_zkey.zkey",
                R.raw.query_identity_dat,
                queryProofInputs,
                ZkpUtil::queryIdentity
            )
        }



        extIntegratorApiManager.queryProofCallback(
            queryProofParametersRequest.value!!.data.attributes.callback_url,
            queryProof,
            userIdHash = queryProofParametersRequest.value!!.data.attributes.callback_url.split("/").last()
        )
    }
}