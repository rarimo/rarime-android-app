package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.ext_integrator.models.NoPassport
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.api.ext_integrator.models.YourAgeDoesNotMeetTheRequirements
import com.rarilabs.rarime.api.ext_integrator.models.YourCitizenshipDoesNotMeetTheRequirements
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.calculateAgeFromBirthDate
import com.rarilabs.rarime.util.Country
import com.rarilabs.rarime.util.DateUtil
import com.rarilabs.rarime.util.decodeHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
@HiltViewModel
class LightProofHandlerViewModel @Inject constructor(
    private val extIntegratorApiManager: ExtIntegratorApiManager,
    private val passportManager: PassportManager,
    private val contractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
) : ViewModel() {
    private var _queryProofParametersRequest = MutableStateFlow<QueryProofGenResponse?>(null)
    val queryProofParametersRequest: StateFlow<QueryProofGenResponse?>
        get() = _queryProofParametersRequest.asStateFlow()

    private val _requestMinimumAge = MutableStateFlow(0)
    val requestMinimumAge: StateFlow<Int>
        get() = _requestMinimumAge.asStateFlow()

    private val _requestCitizenship = MutableStateFlow("")
    val requestCitizenship: StateFlow<String>
        get() = _requestCitizenship.asStateFlow()

    suspend fun signHashedEventId() {
        val queryProofPubSignals = mutableListOf<String>()

        queryProofParametersRequest.value?.data?.attributes?.let {
            // citizenship
            val citizenship = passportManager.passport.value?.personDetails?.issuerAuthority
                ?: throw Exception("Citizenship is null")

            if (requestCitizenship.value.isNotEmpty() && requestCitizenship.value != citizenship) {
                throw YourCitizenshipDoesNotMeetTheRequirements()
            }

            val birthDate = passportManager.passport.value?.personDetails?.birthDate
                ?: throw Exception("Birth date is null")
            val age = calculateAgeFromBirthDate(birthDate)

            if (requestMinimumAge.value > 0 && age < requestMinimumAge.value) {
                throw YourAgeDoesNotMeetTheRequirements()
            }

            // nullifier
            val nullifier = identityManager.getProfiler().calculateEventNullifierInt(
                it.event_id
            )
            queryProofPubSignals.add(nullifier)

            // birthDate
            val birthDateBN = BigInteger(Numeric.hexStringToByteArray("0x303030303030"))
            queryProofPubSignals.add(birthDateBN.toString())

            // expirationDate
            val expirationDateBN = BigInteger(Numeric.hexStringToByteArray("0x303030303030"))
            queryProofPubSignals.add(expirationDateBN.toString())

            // name
            queryProofPubSignals.add("0")

            // nameResidual
            queryProofPubSignals.add("0")

            // nationality
            val nationality = "0"

            queryProofPubSignals.add(nationality)

            val citizenshipBN = BigInteger(citizenship.toByteArray())
            queryProofPubSignals.add(citizenshipBN.toString())

            // sex
            val sexRaw = passportManager.passport.value?.personDetails?.gender
                ?: throw Exception("sex is null")

            val sex = when (sexRaw) {
                "MALE" -> "M"
                "FEMALE" -> "F"
                else -> "O"
            }

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

            val eventDataBN = BigInteger(1, Numeric.hexStringToByteArray(eventData))

            queryProofPubSignals.add(eventDataBN.toString())


            // idStateRoot
            val anonymousId = Identity.calculateAnonymousID(
                passportManager.passport.value!!.dg1!!.decodeHexString(), BaseConfig.POINTS_SVC_ID
            )

            queryProofPubSignals.add(BigInteger(anonymousId).toString())

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

            val birthDateLowerboundBN =
                BigInteger(Numeric.hexStringToByteArray(birthDateLowerbound))

            queryProofPubSignals.add(birthDateLowerboundBN.toString())

            // birthDateUpperbound
            val birthDateUpperbound = it.birth_date_upper_bound

            val birthDateUpperboundBN =
                BigInteger(Numeric.hexStringToByteArray(birthDateUpperbound))

            queryProofPubSignals.add(birthDateUpperboundBN.toString())

            // expirationDateLowerbound
            val expirationDateLowerbound = it.expiration_date_lower_bound

            queryProofPubSignals.add(expirationDateLowerbound)

            // expirationDateUpperbound
            val expirationDateUpperbound = it.expiration_date_upper_bound

            queryProofPubSignals.add(expirationDateUpperbound)

            // citizenshipMask
            val citizenshipMask = it.citizenship_mask

            val citizenshipMaskBN =
                if (citizenshipMask.isEmpty() || citizenshipMask == "0x") BigInteger(
                    Numeric.hexStringToByteArray(
                        "0x303030303030"
                    )
                ) else BigInteger(
                    Numeric.hexStringToByteArray(citizenshipMask)
                )

            queryProofPubSignals.add(citizenshipMaskBN.toString())
        } ?: run {
            throw Exception("Query Proof parameters are null")
        }

        val signature = Identity.signPubSignalsWithSecp256k1(
            BaseConfig.lightVerificationSKHex,
            Gson().toJson(queryProofPubSignals).toByteArray()
        )

        extIntegratorApiManager.lightSignatureCallback(
            queryProofParametersRequest.value!!.data.attributes.callback_url,
            pubSignals = queryProofPubSignals,
            signature.removePrefix("0x"),
            userIdHash = queryProofParametersRequest.value!!.data.attributes.callback_url.split("/")
                .last()
        )
    }

    suspend fun loadDetails(proofParamsUrl: String, redirectUrl: String?): Map<String, String> {

        if (passportManager.passport.value == null) {
            throw NoPassport()
        }

        _queryProofParametersRequest.value = extIntegratorApiManager.queryProofData(proofParamsUrl)

        val tempMap = mutableMapOf<String, String>()

        try {
            val ageLowerBoundYears = if (
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

            _requestMinimumAge.value = ageLowerBoundYears

            if (ageLowerBoundYears > 0) {
                tempMap["Age"] = "${ageLowerBoundYears}+"
            }
        } catch (e: Exception) {
            Log.e("age_lower_bound_years", e.message, e)
        }

        try {
            val uniqueness =
                queryProofParametersRequest.value?.data?.attributes?.timestamp_upper_bound?.toLong() != 0L ||
                        queryProofParametersRequest.value?.data?.attributes?.identity_counter_upper_bound?.toLong() != 0L
            tempMap["Uniqueness"] = if (uniqueness) {
                "Yes"
            } else {
                "No"
            }
        } catch (e: Exception) {
            Log.e("uniqueness", e.message, e)
        }

        try {
            val nationality = if (
                queryProofParametersRequest.value?.data?.attributes?.citizenship_mask != null && queryProofParametersRequest.value?.data?.attributes?.citizenship_mask != "0x"
            ) {
                Numeric.hexStringToByteArray(queryProofParametersRequest.value?.data?.attributes?.citizenship_mask)
                    .decodeToString()
            } else {
                ""
            }

            _requestCitizenship.value = nationality

            if (nationality.isNotEmpty()) {
                val country = Country.fromISOCode(nationality)
                tempMap["Nationality"] = "${country.localizedName} ${country.flag}"
            }
        } catch (e: Exception) {
            Log.e("nationality", e.message, e)
        }

        if (redirectUrl != null) {
            tempMap["Redirection URL"] = redirectUrl
        }


        return tempMap
    }
}