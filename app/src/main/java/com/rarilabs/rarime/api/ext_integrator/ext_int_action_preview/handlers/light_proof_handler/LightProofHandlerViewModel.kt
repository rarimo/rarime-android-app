package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
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
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneOffset
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

    suspend fun signHashedEventId() {
        val queryProofPubSignals = mutableListOf<String>()

        queryProofParametersRequest.value?.data?.attributes?.let {
            // nullifier
            val nullifier = identityManager.getProfiler().calculateEventNullifierInt(
                it.event_id
            )
            queryProofPubSignals.add(nullifier)

            // birthDate
            val birthDate = "0x303030303030"

            val birthDateBN = BigInteger(Numeric.hexStringToByteArray(birthDate))

            queryProofPubSignals.add(birthDateBN.toString())

            // expirationDate
            val expirationDate = "0x303030303030"

            val expirationDateBN = BigInteger(Numeric.hexStringToByteArray(expirationDate))

            queryProofPubSignals.add(expirationDateBN.toString())

            // name
            queryProofPubSignals.add("0")

            // nameResidual
            queryProofPubSignals.add("0")

            // nationality
            val nationality = "0"

            queryProofPubSignals.add(nationality)

            // citizenship
            val citizenship = passportManager.passport.value?.personDetails?.issuerAuthority
                ?: throw Exception("Citizenship is null")

            val citizenshipBN = BigInteger(citizenship.toByteArray())

            queryProofPubSignals.add(citizenshipBN.toString())

            // sex
            val sex = passportManager.passport.value?.personDetails?.gender ?: throw Exception("sex is null")

            val sexBN = BigInteger(sex.toByteArray())

            queryProofPubSignals.add(sexBN.toString())

            // documentNumber
            val documentNumber = "0"

            queryProofPubSignals.add(documentNumber)

            // eventID
            val eventID = it.event_id

            queryProofPubSignals.add(eventID)

            // eventData
            val eventData = it.event_data

            val eventDataBN = BigInteger(Numeric.hexStringToByteArray(eventData))

            queryProofPubSignals.add(eventDataBN.toString())

            // idStateRoot
            val idStateRoot = "0"

            queryProofPubSignals.add(idStateRoot)

            // selector
            val selector = it.selector

            queryProofPubSignals.add(selector)

            // currentDate
            val currentDate = LocalDateTime.now()

            val currentDateUnix = currentDate.toEpochSecond(ZoneOffset.UTC)

            queryProofPubSignals.add(currentDateUnix.toString())

            // timestampLowerbound
            val timestampLowerbound = it.timestamp_lower_bound

            queryProofPubSignals.add(timestampLowerbound)

            // timestampUpperbound
            val timestampUpperbound = it.timestamp_upper_bound

            queryProofPubSignals.add(timestampUpperbound)

            // identityCounterLowerbound
            val identityCounterLowerbound = it.identity_counter_lower_bound

            queryProofPubSignals.add(identityCounterLowerbound.toString())

            // identityCounterUpperbound
            val identityCounterUpperbound = it.identity_counter_upper_bound

            queryProofPubSignals.add(identityCounterUpperbound.toString())

            // birthDateLowerbound
            val birthDateLowerbound = it.birth_date_lower_bound

            val birthDateLowerboundBN = BigInteger(Numeric.hexStringToByteArray(birthDateLowerbound))

            queryProofPubSignals.add(birthDateLowerboundBN.toString())

            // birthDateUpperbound
            val birthDateUpperbound = it.birth_date_upper_bound

            val birthDateUpperboundBN = BigInteger(Numeric.hexStringToByteArray(birthDateUpperbound))

            queryProofPubSignals.add(birthDateUpperboundBN.toString())

            // expirationDateLowerbound
            val expirationDateLowerbound = it.expiration_date_lower_bound

            queryProofPubSignals.add(expirationDateLowerbound)

            // expirationDateUpperbound
            val expirationDateUpperbound = it.expiration_date_upper_bound

            queryProofPubSignals.add(expirationDateUpperbound)

            // citizenshipMask
            val citizenshipMask = it.citizenship_mask

            val citizenshipMaskBN = BigInteger(Numeric.hexStringToByteArray(citizenshipMask))

            queryProofPubSignals.add(citizenshipMaskBN.toString())
        } ?: run {
            throw Exception("Query Proof parameters are null")
        }

        Log.i("queryProofPubSignals", Gson().toJson(queryProofPubSignals).toString())

        val signature = Identity.signPubSignalsWithSecp256k1(
            BaseConfig.lightVerificationSKHex,
            Gson().toJson(queryProofPubSignals).toByteArray()
        )

        extIntegratorApiManager.lightSignatureCallback(
            queryProofParametersRequest.value!!.data.attributes.callback_url,
            pubSignals = queryProofPubSignals,
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