package com.distributedLab.rarime.api.registration

import com.distributedLab.rarime.api.registration.models.RegisterBody
import com.distributedLab.rarime.api.registration.models.RegisterData
import com.distributedLab.rarime.api.registration.models.RegisterResponseBody
import javax.inject.Inject

class RegistrationAPIManager @Inject constructor(
    private val registrationAPI: RegistrationAPI
) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun register(callData: ByteArray): RegisterResponseBody? {

        val payload = RegisterBody(
            data = RegisterData(
                tx_data = "0x" + callData.toHexString()
            )
        )
        val response = registrationAPI.register(payload)

        if (response.isSuccessful) {
            return response.body()!!
        }
        return null
    }
}