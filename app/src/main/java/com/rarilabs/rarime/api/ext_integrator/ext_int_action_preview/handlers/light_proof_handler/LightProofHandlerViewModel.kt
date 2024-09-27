package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
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
import java.time.LocalDateTime
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
        /* Query Proof pub signals
        [
             "20925303098627062266630214635967906856225360340756326562498326001746719100911", // 0 - nullifier
             "52992115355956", // 1 - birthDate
             "55216908480563", // 2 - expirationDate
             "0", // 3 - name
             "0", // 4 - nameResidual
             "0", // 5 - nationality
             "5589842", // 6 - citizenship
             "0", // 7 - sex
             "0", // 8 - documentNumber
             "304358862882731539112827930982999386691702727710421481944329166126417129570", // 9 - eventID
             "1217571210886365587192326979343136122389414675532", // 10 - eventData
             "5904469035765435216409767735512782299719282306270684213646687525744667841608", // 11 - idStateRoot
             "39", // 12 - selector
             "52983525027888", // 13 - currentDate
             "0", // 14 - timestampLowerbound
             "0", // 15 - timestampUpperbound
             "1", // 16 - identityCounterLowerbound
             "0", // 17 - identityCounterUpperbound
             "52983525027888", // 18 - birthDateLowerbound
             "52983525027888", // 19 - birthDateUpperbound
             "52983525027888", // 20 - expirationDateLowerbound
             "5298352502788", // 21 - expirationDateUpperbound
             "0" // 22 - citizenshipMask
        ]
        * */

        // TODO: convert queryProofParametersRequest.data.attributes to Query Proof pub signals
        val queryProofPubSignals = mutableListOf<String>()

        queryProofParametersRequest.value?.data?.attributes?.let {
            // nullifier
            val nullifier = identityManager.getUserPointsNullifier()
            queryProofPubSignals.add(nullifier)

            // birthDate
            val birthDate = passportManager.passport.value?.personDetails?.birthDate
                ?: throw Exception("Birth Date is null")

            queryProofPubSignals.add(birthDate)
            // expirationDate
            val expirationDate = passportManager.passport.value?.personDetails?.expiryDate
                ?: throw Exception("Expiration Date is null")

            queryProofPubSignals.add(expirationDate)

            // name
            queryProofPubSignals.add("0")

            // nameResidual
            queryProofPubSignals.add("0")

            // nationality
            val nationality = passportManager.passport.value?.personDetails?.nationality
                ?: throw Exception("Nationality is null")

            queryProofPubSignals.add(nationality)

            // citizenship
            val citizenship = queryProofParametersRequest.value?.data?.attributes?.citizenship_mask // FIXME

            if (citizenship == null) {
                throw Exception("Citizenship is null")
            }

            queryProofPubSignals.add(citizenship)
            // sex
            val sex = passportManager.passport.value?.personDetails?.gender ?: throw Exception("sex is null")

            queryProofPubSignals.add(sex)

            // documentNumber
            val documentNumber = passportManager.passport.value?.personDetails?.personalNumber
                ?: throw Exception("documentNumber is null")

            queryProofPubSignals.add(documentNumber)

            // eventID
            val eventID = queryProofParametersRequest.value?.data?.attributes?.event_id
                ?: throw Exception("Event ID is null")

            queryProofPubSignals.add(eventID)

            // eventData
            val eventData = queryProofParametersRequest.value?.data?.attributes?.event_data
                ?: throw Exception("Event Data is null")

            queryProofPubSignals.add(eventData)

            // idStateRoot
            val idStateRoot = identityManager.registrationProof.value?.pub_signals?.get(0)
                ?: throw Exception("idStateRoot is null") // FIXME

            queryProofPubSignals.add(idStateRoot)

            // selector
            val selector = queryProofParametersRequest.value?.data?.attributes?.selector
                ?: throw Exception("selector is null")

            queryProofPubSignals.add(selector)

            // currentDate
            val currentDate = LocalDateTime.now().toString()

            queryProofPubSignals.add(currentDate)

            // timestampLowerbound
            val timestampLowerbound = queryProofParametersRequest.value?.data?.attributes?.timestamp_lower_bound
                ?: throw Exception("timestampLowerbound is null")

            queryProofPubSignals.add(timestampLowerbound)

            // timestampUpperbound
            val timestampUpperbound = queryProofParametersRequest.value?.data?.attributes?.timestamp_upper_bound
                ?: throw Exception("timestampUpperbound is null")

            queryProofPubSignals.add(timestampUpperbound)

            // identityCounterLowerbound
            val identityCounterLowerbound = queryProofParametersRequest.value?.data?.attributes?.identity_counter_lower_bound
                ?: throw Exception("identityCounterLowerbound is null")

            queryProofPubSignals.add(identityCounterLowerbound.toString())

            // identityCounterUpperbound
            val identityCounterUpperbound = queryProofParametersRequest.value?.data?.attributes?.identity_counter_upper_bound
                ?: throw Exception("identityCounterUpperbound is null")

            queryProofPubSignals.add(identityCounterUpperbound.toString())

            // birthDateLowerbound
            val birthDateLowerbound = queryProofParametersRequest.value?.data?.attributes?.birth_date_lower_bound
                ?: throw Exception("birthDateLowerbound is null")

            queryProofPubSignals.add(birthDateLowerbound)

            // birthDateUpperbound
            val birthDateUpperbound = queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound
                ?: throw Exception("birthDateUpperbound is null")

            queryProofPubSignals.add(birthDateUpperbound)

            // expirationDateLowerbound
            val expirationDateLowerbound = queryProofParametersRequest.value?.data?.attributes?.expiration_date_lower_bound
                ?: throw Exception("expirationDateLowerbound is null")

            queryProofPubSignals.add(expirationDateLowerbound)

            // expirationDateUpperbound
            val expirationDateUpperbound = queryProofParametersRequest.value?.data?.attributes?.expiration_date_upper_bound
                ?: throw Exception("expirationDateUpperbound is null")

            queryProofPubSignals.add(expirationDateUpperbound)

            // citizenshipMask
            val citizenshipMask = queryProofParametersRequest.value?.data?.attributes?.citizenship_mask
                ?: throw Exception("citizenshipMask is null")

            queryProofPubSignals.add(citizenshipMask)
        } ?: run {
            throw Exception("Query Proof parameters are null")
        }

        val signature = Identity.signPubSignalsWithSecp256k1(
            identityManager.privateKey.value,
            Gson().toJson(queryProofPubSignals).toByteArray()
        )

//        val eventDataHex = queryProofParametersRequest.value?.data?.attributes?.event_data
//            ?: throw Exception("Event ID is null")
//
//        val hashedEventData = eventDataHex.hashedWithSha256()
//
//        val lightVerificationSKBytes = Numeric.hexStringToByteArray(Keys.lightVerificationSKHex)
//
//        val ecKeyPairInstance = ECKeyPair.create(lightVerificationSKBytes)
//
//        val signedEventData = Sign.signMessage(
//            Numeric.hexStringToByteArray(hashedEventData),
//            ecKeyPairInstance,
//            false
//        )
//
//        val signature = signedEventData.r + signedEventData.s + signedEventData.v

        extIntegratorApiManager.lightSignatureCallback(
            queryProofParametersRequest.value!!.data.attributes.callback_url,
            signature.removePrefix("0x"),
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