package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.config.Keys
import com.rarilabs.rarime.contracts.rarimo.StateKeeper
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.calculateAgeFromBirthDate
import com.rarilabs.rarime.util.Country
import com.rarilabs.rarime.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.crypto.ECDSASignature
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
fun String.hashedWithSha256() =
    MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .toHexString()

@HiltViewModel
class LightProofHandlerViewModel @Inject constructor(
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

    // Helper function to get the uncompressed public key with "04" prefix
    fun getUncompressedPublicKey(publicKey: BigInteger): String {
        val xCoord = publicKey.shiftRight(256).toString(16).padStart(64, '0') // First 32 bytes
        val yCoord = publicKey.and(BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16))
            .toString(16).padStart(64, '0') // Last 32 bytes

        // Prefix with "04" for uncompressed public key format
        return "04$xCoord$yCoord"
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signHashedEventId() {
        val eventDataHex = queryProofParametersRequest.value?.data?.attributes?.event_data
            ?: throw Exception("Event ID is null")

        val hashedEventData = eventDataHex.hashedWithSha256()

        val lightVerificationSKBytes = Numeric.hexStringToByteArray(Keys.lightVerificationSKHex)

        val ecKeyPairInstance = ECKeyPair.create(lightVerificationSKBytes)

        val signedEventData = Sign.signMessage(
            Numeric.hexStringToByteArray(hashedEventData),
            ecKeyPairInstance,
            false
        )

        val signature = signedEventData.r + signedEventData.s + signedEventData.v

        extIntegratorApiManager.lightSignatureCallback(
            queryProofParametersRequest.value!!.data.attributes.callback_url,
            Numeric.toHexString(signature).removePrefix("0x"),
            userIdHash = queryProofParametersRequest.value!!.data.attributes.callback_url.split("/").last()
        )
    }

    suspend fun loadDetails(proofParamsUrl: String): Map<String, String> {
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
            var uniqueness = if (
                queryProofParametersRequest.value?.data?.attributes?.timestamp_upper_bound?.toLong() != 0L ||
                queryProofParametersRequest.value?.data?.attributes?.identity_counter_upper_bound?.toLong() != 0L
            ) {
                true
            } else {
                false
            }

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
                queryProofParametersRequest.value?.data?.attributes?.citizenship_mask != null
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

        return tempMap
    }
}