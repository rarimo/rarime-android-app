package com.rarilabs.rarime.api.ext_integrator

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.models.ExtIntegratorActions
import com.rarilabs.rarime.api.ext_integrator.models.ProofParametersRequest
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
import com.rarilabs.rarime.util.data.ZkProof
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
    suspend fun handleAction(payload: String) {
        val qrAction = Gson().fromJson(payload, QrAction::class.java)

        val requestData = extIntegratorApiManager.getRequestData(qrAction.dataUrl)

        when (qrAction.type) {
            ExtIntegratorActions.SignTypedData.value -> {
                val signedMessage = signTypedData(requestData.data.attributes.requestData)

                extIntegratorApiManager.callback(qrAction.callbackUrl, signedMessage)
            }

            ExtIntegratorActions.Authorize.value -> {
                // TODO: parse payload to authorize appropriate types
                authorize(payload)
            }
        }
    }

    private fun signTypedData(body: String): String {
        val type = object : TypeToken<EIP712TypedData>() {}.type
        val typedData: EIP712TypedData = Gson().fromJson(body, type)
        val signedMessage = EIP712Utility.signMessage(typedData, identityManager.privateKey.value!!)

        return signedMessage
    }

    private fun authorize(body: String) {
        // TODO: Implement authorization logic
    }

    private suspend fun generateQueryProof(queryProofParametersJson: String): ZkProof {
        val queryProofParametersRequest = Gson().fromJson(queryProofParametersJson, ProofParametersRequest::class.java)

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

        val queryProofInputs = profiler.buildAirdropQueryIdentityInputs(
            passportManager.passport.value!!.dg1!!.decodeHexString(),
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
}