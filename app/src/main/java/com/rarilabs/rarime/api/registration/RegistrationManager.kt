package com.rarilabs.rarime.api.registration

import RegisterIdentityCircuitType
import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.registration.models.VerifySodResponse
import com.rarilabs.rarime.contracts.rarimo.PoseidonSMT.Proof
import com.rarilabs.rarime.contracts.rarimo.StateKeeper
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.util.Constants.NOT_ALLOWED_COUNTRIES
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.data.UniversalProof
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.publicKeyToPem
import identity.CallDataBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.jmrtd.lds.icao.DG15File
import org.web3j.tuples.generated.Tuple2
import org.web3j.utils.Numeric
import javax.inject.Inject

class RegistrationManager @Inject constructor(
    private val registrationAPIManager: RegistrationAPIManager,
    private val rarimoContractManager: RarimoContractManager,
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager
) {
    private var _masterCertProof = MutableStateFlow<Proof?>(null)
    val masterCertProof: StateFlow<Proof?>
        get() = _masterCertProof.asStateFlow()

    private var _activeIdentity = MutableStateFlow(ByteArray(0))
    val activeIdentity: StateFlow<ByteArray>
        get() = _activeIdentity.asStateFlow()

    private var _registrationProof = MutableStateFlow<UniversalProof?>(null)
    val registrationProof: StateFlow<UniversalProof?>
        get() = _registrationProof.asStateFlow()

    var _revocationChallenge = MutableStateFlow<ByteArray>(ByteArray(0))
        private set
    val revocationChallenge: StateFlow<ByteArray?>
        get() = _revocationChallenge.asStateFlow()

    var _revocationCallData = MutableStateFlow(null as ByteArray?)
    val revocationCallData: StateFlow<ByteArray?>
        get() = _revocationCallData.asStateFlow()

    private var _eDocument = MutableStateFlow(null as EDocument?)
    val eDocument: StateFlow<EDocument?>
        get() = _eDocument.asStateFlow()

    private var _revEDocument = MutableStateFlow(null as EDocument?)
    val revEDocument: StateFlow<EDocument?>
        get() = _revEDocument.asStateFlow()

    private var circuitData: RegisterIdentityCircuitType? = null

    fun getCircuitData(): RegisterIdentityCircuitType? {
        return circuitData
    }

    fun setCircuitData(circuitData: RegisterIdentityCircuitType) {
        this.circuitData = circuitData
    }

    fun setRevEDocument(eDocument: EDocument) {
        _revEDocument.value = eDocument
    }

    fun setRegistrationProof(proof: UniversalProof) {
        _registrationProof.value = proof
    }

    fun setEDocument(eDocument: EDocument?) {
        _eDocument.value = eDocument
    }

    fun setMasterCertProof(proof: Proof) {
        _masterCertProof.value = proof
    }

    suspend fun relayerRegister(callData: ByteArray, destination: String) =
        registrationAPIManager.register(callData, destination)

    /**
     * if isUserRevoking is true, then this method is re-issuance for revoked passport
     * else it is registration for new passport
     */
    suspend fun register(
        zkProof: UniversalProof,
        eDocument: EDocument,
        masterCertProof: Proof,
        isUserRevoking: Boolean,
        registerIdentityCircuitName: String
    ) {
        _eDocument.value = eDocument

        val jsonProof = Gson().toJson(zkProof)

        val pubKeyPem = if (!eDocument.dg15.isNullOrEmpty()) {
            eDocument.getDg15File()!!.publicKey.publicKeyToPem()
                .toByteArray()
        } else {
            byteArrayOf()
        }

        val encapsulatedContent =
            Numeric.hexStringToByteArray(eDocument.getSodFile().readASN1Data())

        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterCalldata(
            jsonProof.toByteArray(),
            eDocument.aaSignature,
            pubKeyPem,
            encapsulatedContent.size.toLong() * 8L,
            masterCertProof.root,
            isUserRevoking,
            registerIdentityCircuitName
        )

        withContext(Dispatchers.IO) {
            val response = relayerRegister(callData, BaseConfig.REGISTER_CONTRACT_ADDRESS)

            response.data.attributes.tx_hash.let {
                rarimoContractManager.checkIsTransactionSuccessful(it)
            }
        }
    }

    suspend fun lightRegistration(
        eDocument: EDocument,
        zkProof: GrothProof
    ): VerifySodResponse {
        return registrationAPIManager.lightRegistration(eDocument, zkProof)
    }

    suspend fun getPassportInfo(
        eDocument: EDocument,
        zkProof: UniversalProof,
    ): Tuple2<StateKeeper.PassportInfo, StateKeeper.IdentityInfo>? {
        val stateKeeperContract = rarimoContractManager.getStateKeeper()

        val passportInfoKeyBytes =
            passportManager.getPassportInfoKeyBytes(eDocument, zkProof)

        val passportInfo = withContext(Dispatchers.IO) {
            stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
        }

        return passportInfo
    }

    suspend fun lightRegisterRelayer(
        zkProof: UniversalProof,
        verifySodResponse: VerifySodResponse
    ) {
        val signature = verifySodResponse.data.attributes.signature.let {
            it.ifEmpty {
                throw IllegalStateException("verifySodResponse.data.attributes.signature is empty")
            }
        }

        val passportHash = verifySodResponse.data.attributes.passport_hash.let {
            it.ifEmpty {
                throw IllegalStateException("verifySodResponse.data.attributes.passport_hash is empty")
            }
        }

        val publicKey = verifySodResponse.data.attributes.public_key.let {
            it.ifEmpty {
                throw IllegalStateException("verifySodResponse.data.attributes.public_key is null")
            }
        }

        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterSimpleCalldata(
            Gson().toJson(
                zkProof
            ).toByteArray(),
            Numeric.hexStringToByteArray(signature),
            Numeric.hexStringToByteArray(passportHash),
            Numeric.hexStringToByteArray(publicKey),
            verifySodResponse.data.attributes.verifier
        )


        withContext(Dispatchers.IO) {
            val response =
                relayerRegister(callData, BaseConfig.REGISTRATION_SIMPLE_CONTRACT_ADRRESS)

            Log.i("response", response.data.attributes.tx_hash)
            val txData = response.data.attributes.tx_hash.let {
                rarimoContractManager.checkIsTransactionSuccessful(it)
            }
            Log.i("response", txData.toString())
        }

    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun getRevocationChallenge(): ByteArray? {
        return withContext(Dispatchers.IO) {
            val passportInfo = withContext(Dispatchers.IO) {
                getPassportInfo(eDocument.value!!, registrationProof.value!!)
            }

            _activeIdentity.value = passportInfo!!.component1()!!.activeIdentity

            ErrorHandler.logDebug("ActiveIdentity", _activeIdentity.value.toHexString())

            val ZERO_BYTES32 = ByteArray(32) { 0 }
            val isUserRevoking =
                !passportInfo.component1().activeIdentity.contentEquals(ZERO_BYTES32)

            if (isUserRevoking) {
                ErrorHandler.logDebug("Revoke", "Passport is registered, revoking")
            } else {
                ErrorHandler.logDebug("Revoke", "Passport is not registered")
            }

            if (isUserRevoking) {
                _revocationChallenge.value =
                    passportInfo.component1().activeIdentity.copyOfRange(24, 32)
                return@withContext _revocationChallenge.value
            }

            return@withContext null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun buildRevocationCallData() {
        val dG15File = DG15File(revEDocument.value!!.dg15!!.decodeHexString().inputStream())

        val pubKeyPem = dG15File.publicKey.publicKeyToPem()

        val callDataBuilder = CallDataBuilder()

        ErrorHandler.logDebug(
            "buildRevocationCallData",
            revEDocument.value!!.aaSignature.toString()
        )

        ErrorHandler.logDebug("buildRevocationCallData", pubKeyPem)

        Log.i("activeIdentity.value", activeIdentity.value.toHexString())

        val encapsulatedContent =
            Numeric.hexStringToByteArray(revEDocument.value!!.getSodFile().readASN1Data())


        val callData = callDataBuilder.buildRevoceCalldata(
            activeIdentity.value,
            revEDocument.value!!.aaSignature,
            pubKeyPem.toByteArray(),
            encapsulatedContent.size.toLong() * 8
        )

        ErrorHandler.logDebug("callData", callData.toString())
        _revocationCallData.value = callData
    }

    suspend fun revoke() {
        try {
            try {
                val txResponse = registrationAPIManager.register(
                    revocationCallData.value!!,
                    BaseConfig.REGISTER_CONTRACT_ADDRESS
                )

                txResponse.data.attributes.tx_hash.let {
                    rarimoContractManager.checkIsTransactionSuccessful(it)
                }
            } catch (e: Exception) {
                ErrorHandler.logError("RegistrationManager:revoke:", "Error: $e", e)

                if (e !is UserAlreadyRevoked) {
                    throw e
                }
            }

            ErrorHandler.logDebug("registrationProof.value!!", registrationProof.value!!.toString())
            ErrorHandler.logDebug("masterCertProof.value!!", masterCertProof.value!!.toString())

            register(
                registrationProof.value!!,
                eDocument.value!!,
                masterCertProof.value!!,
                true,
                getCircuitData()!!.buildName()
            )

            identityManager.setRegistrationProof(registrationProof.value)

            if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.value!!.personDetails?.nationality)) {
                passportManager.updatePassportStatus(PassportStatus.ALLOWED)
            } else {
                passportManager.updatePassportStatus(PassportStatus.NOT_ALLOWED)
            }

        } catch (e: Exception) {
            ErrorHandler.logError("RevocationStepViewModel", "Error: $e", e)
            throw e
        }
    }
}