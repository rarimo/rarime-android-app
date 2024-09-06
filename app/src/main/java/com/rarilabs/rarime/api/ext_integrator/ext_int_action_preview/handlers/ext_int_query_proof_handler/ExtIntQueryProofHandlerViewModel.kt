package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.ext_integrator.models.QrAction
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.contracts.rarimo.StateKeeper
import com.rarilabs.rarime.data.ProofTxFull
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
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

    private var _fieldsParams = MutableStateFlow<List<Pair<String, String>>?>(null)
    val fieldsParams: StateFlow<List<Pair<String, String>>?>
        get() = _fieldsParams.asStateFlow()

    suspend fun loadDetails(qrAction: QrAction, context: Context) {
        _queryProofParametersRequest.value = extIntegratorApiManager.queryProofData(qrAction.dataUrl!!)

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

        _fieldsParams.value = listOf(
            Pair(context.getString(R.string.ext_action_query_proof_gen_birth_date_lower_bound), queryProofParametersRequest.value?.data?.attributes?.birth_date_lower_bound.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_birth_date_upper_bound), queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_citizenship_mask), queryProofParametersRequest.value?.data?.attributes?.citizenship_mask.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_event_data), queryProofParametersRequest.value?.data?.attributes?.event_data.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_event_id), queryProofParametersRequest.value?.data?.attributes?.event_id.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_expiration_date_lower_bound), queryProofParametersRequest.value?.data?.attributes?.expiration_date_lower_bound.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_expiration_date_upper_bound), queryProofParametersRequest.value?.data?.attributes?.expiration_date_upper_bound.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_identity_counter), queryProofParametersRequest.value?.data?.attributes?.identity_counter.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_identity_counter_lower_bound), queryProofParametersRequest.value?.data?.attributes?.identity_counter_lower_bound.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_identity_counter_upper_bound), queryProofParametersRequest.value?.data?.attributes?.identity_counter_upper_bound.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_selector), queryProofParametersRequest.value?.data?.attributes?.selector.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_timestamp_lower_bound), queryProofParametersRequest.value?.data?.attributes?.timestamp_lower_bound.toString()),
            Pair(context.getString(R.string.ext_action_query_proof_gen_timestamp_upper_bound), queryProofParametersRequest.value?.data?.attributes?.timestamp_upper_bound.toString()),
        )
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