package com.rarilabs.rarime.api.ext_integrator

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.models.ExtIntegratorActions
import com.rarilabs.rarime.api.ext_integrator.models.QrAction
import com.rarilabs.rarime.data.ProofTxFull
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.util.EIP712TypedData
import com.rarilabs.rarime.util.EIP712Utility
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.decodeHexString
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExtIntegratorManager @Inject constructor(
    private val context: Context,
    private val extIntegratorApiManager: ExtIntegratorApiManager,
    private val identityManager: IdentityManager,
    private val contractManager: RarimoContractManager,
    private val passportManager: PassportManager,
) {
    suspend fun handleAction(requestJson: String) {
        val qrAction = Gson().fromJson(requestJson, QrAction::class.java)

        when (qrAction.type) {
            ExtIntegratorActions.SignTypedData.value -> {
                val signedMessage = signTypedData(qrAction)

//                extIntegratorApiManager.callback(qrAction.callbackUrl, signedMessage)
            }

            ExtIntegratorActions.Authorize.value -> {
                // TODO: parse payload to authorize appropriate types
                authorize(qrAction)
            }

            ExtIntegratorActions.QueryProofGen.value -> {
                generateQueryProof(qrAction)
            }
        }
    }

    private suspend fun signTypedData(qrAction: QrAction): String {
        if (qrAction.payload.isNullOrEmpty()) {
            throw Exception("Payload is empty")
        }

        val type = object : TypeToken<EIP712TypedData>() {}.type
        val typedData: EIP712TypedData = Gson().fromJson(qrAction.payload, type)
        val signedMessage = EIP712Utility.signMessage(typedData, identityManager.privateKey.value!!)

        return signedMessage
    }

    private fun authorize(qrAction: QrAction) {
        // TODO: Implement authorization logic
    }

    private suspend fun generateQueryProof(qrAction: QrAction) {
        val queryProofParametersRequest = extIntegratorApiManager.queryProofData(qrAction.dataUrl!!)

        val assetContext: Context = context.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)

        val stateKeeperContract = contractManager.getStateKeeper()

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

        val profiler = identityManager.getProfiler()

        val passportInfoRaw = withContext(Dispatchers.IO) {
            stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
        }

        val passportInfo = passportInfoRaw.component1()
        val identityInfo = passportInfoRaw.component2()

        val queryProofInputs = profiler.buildQueryIdentityInputs(
            passportManager.passport.value!!.dg1!!.decodeHexString(),
            smtProofJson.toByteArray(Charsets.UTF_8),
            BaseConfig.POINTS_SVC_SELECTOR,
            passportInfoKey,
            identityInfo.issueTimestamp.toString(),
            passportInfo.identityReissueCounter.toString(),
            BaseConfig.POINTS_SVC_ID,

            queryProofParametersRequest.data.attributes.timestampLowerBound,
            queryProofParametersRequest.data.attributes.timestampUpperBound,
            queryProofParametersRequest.data.attributes.identityCounterLowerBound.toString(),
            queryProofParametersRequest.data.attributes.identityCounterUpperBound.toString(),
            queryProofParametersRequest.data.attributes.expirationDateLowerBound,
            queryProofParametersRequest.data.attributes.expirationDateUpperBound,
            queryProofParametersRequest.data.attributes.birthDateLowerBound,
            queryProofParametersRequest.data.attributes.birthDateUpperBound,
            queryProofParametersRequest.data.attributes.citizenshipMask,
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
            qrAction.callbackUrl,
            queryProof,
            userIdHash = qrAction.callbackUrl.split("/").last()
        )
    }
}