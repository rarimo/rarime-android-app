package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.rarime.api.ext_integrator.models.NoActiveIdentity
import com.rarilabs.rarime.api.ext_integrator.models.NoPassport
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.api.ext_integrator.models.YourAgeDoesNotMeetTheRequirements
import com.rarilabs.rarime.api.ext_integrator.models.YourCitizenshipDoesNotMeetTheRequirements
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.modules.passportScan.calculateAgeFromBirthDate
import com.rarilabs.rarime.util.Country
import com.rarilabs.rarime.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val identityManager: IdentityManager
) : ViewModel() {
    private var _queryProofParametersRequest = MutableStateFlow<QueryProofGenResponse?>(null)
    val queryProofParametersRequest: StateFlow<QueryProofGenResponse?>
        get() = _queryProofParametersRequest.asStateFlow()

    private var _fieldsParams = MutableStateFlow<MutableMap<String, String>>(mutableMapOf())
    val fieldsParams: StateFlow<Map<String, String>>
        get() = _fieldsParams.asStateFlow()


    suspend fun loadDetails(proofParamsUrl: String, redirectUrl: String?) {


        _queryProofParametersRequest.value = withContext(Dispatchers.IO) {
            extIntegratorApiManager.queryProofData(proofParamsUrl)
        }

        extIntegratorApiManager.loadPassportInfo()

        val tempMap = mutableMapOf<String, String>()

        try {
            val ageLowerBoundYears =
                if (queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound != null && queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound != "0x303030303030") {
                    val birthDateUpperBoundBytes = Numeric.hexStringToByteArray(
                        queryProofParametersRequest.value?.data?.attributes?.birth_date_upper_bound
                    ).decodeToString()

                    val mrzParsedDate = DateUtil.convertFromMrzDate(birthDateUpperBoundBytes)

                    calculateAgeFromBirthDate(mrzParsedDate)
                } else {
                    0
                }

            if (ageLowerBoundYears > 0) {
                tempMap["Age"] = "${ageLowerBoundYears}+"
            }

            val birthDate = passportManager.passport.value?.personDetails?.birthDate
                ?: throw Exception("Birth date is null")
            val age = calculateAgeFromBirthDate(birthDate)

            if (ageLowerBoundYears > 0 && ageLowerBoundYears > age) {
                throw YourAgeDoesNotMeetTheRequirements()
            }

        } catch (e: Exception) {
            Log.e("ageLowerBoundYears", e.message, e)
        }

        try {
            val uniqueness =
                queryProofParametersRequest.value?.data?.attributes?.timestamp_upper_bound?.toLong() != 0L || queryProofParametersRequest.value?.data?.attributes?.identity_counter_upper_bound?.toLong() != 0L
            tempMap["Uniqueness"] = if (uniqueness) {
                "Yes"
            } else {
                "No"
            }
        } catch (e: Exception) {
            Log.e("uniqueness", e.message, e)
        }

        try {
            val nationality =
                if (queryProofParametersRequest.value?.data?.attributes?.citizenship_mask != null && queryProofParametersRequest.value?.data?.attributes?.citizenship_mask != "0x") {
                    val nationality =
                        Numeric.hexStringToByteArray(queryProofParametersRequest.value?.data?.attributes?.citizenship_mask)
                            .decodeToString()

                    val country = Country.fromISOCode(nationality)

                    "${country.localizedName} ${country.flag}"
                } else {
                    ""
                }

            if (nationality.isNotEmpty()) {
                tempMap["Nationality"] = nationality
            }

            val citizenship = passportManager.passport.value?.personDetails?.issuerAuthority
                ?: throw Exception("Citizenship is null")

            if (nationality != citizenship) {
                throw YourCitizenshipDoesNotMeetTheRequirements()
            }

        } catch (e: Exception) {
            Log.e("nationality", e.message, e)
        }

        if (redirectUrl != null) {
            tempMap["Redirection URL"] = redirectUrl
        }

        _fieldsParams.value = tempMap
    }

    suspend fun generateQueryProof(context: Context) {

        if (passportManager.passport.value == null) {
            throw NoPassport()
        }

        if (identityManager.registrationProof.value == null) {
            throw NoActiveIdentity()
        }

        val queryProof =
            extIntegratorApiManager.generateQueryProof(context, queryProofParametersRequest.value!!)
                ?: return

        extIntegratorApiManager.queryProofCallback(
            queryProofParametersRequest.value!!.data.attributes.callback_url,
            queryProof,
            userIdHash = queryProofParametersRequest.value!!.data.attributes.callback_url.split("/")
                .last()
        )
    }
}
