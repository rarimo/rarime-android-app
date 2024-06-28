package com.rarilabs.rarime.api.registration

import android.util.Log
import com.rarilabs.rarime.api.registration.models.RegisterBody
import com.rarilabs.rarime.api.registration.models.RegisterData
import com.rarilabs.rarime.api.registration.models.RegisterResponseBody
import javax.inject.Inject

class UserAlreadyRegistered() : Exception()
class UserAlreadyRevoked() : Exception()

class RegistrationAPIManager @Inject constructor(
    private val registrationAPI: RegistrationAPI
) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun register(callData: ByteArray): RegisterResponseBody {
        val response = registrationAPI.register(
            RegisterBody(
                data = RegisterData(
                    tx_data = "0x" + callData.toHexString()
                )
            )
        )

        if (response.isSuccessful) {
            return response.body()!!
        }

        val errorBody = response.errorBody()?.string()
        val errorCode = response.code()

        Log.e("RegistrationAPIManager", errorBody.toString())

        if (errorCode == 400 && errorBody?.contains("already registered") == true) {
            throw UserAlreadyRegistered()
        } else if (errorBody?.contains("already revoked") == true) {
            throw UserAlreadyRevoked()
        } else {
            throw Exception("Registration failed")
        }
    }
}