package com.rarilabs.rarime.api.registration

import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.registration.models.RegisterBody
import com.rarilabs.rarime.api.registration.models.RegisterData
import com.rarilabs.rarime.api.registration.models.RegisterResponseBody
import com.rarilabs.rarime.util.ErrorHandler
import javax.inject.Inject

class UserAlreadyRegistered() : Exception()
class UserAlreadyRevoked() : Exception()
class PassportAlreadyRegisteredByOtherPK() : Exception()

class RegistrationAPIManager @Inject constructor(
    private val registrationAPI: RegistrationAPI
) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun register(callData: ByteArray, destination: String): RegisterResponseBody {
        val response = registrationAPI.register(
            RegisterBody(
                data = RegisterData(
                    tx_data = "0x" + callData.toHexString(),
                    destination = destination
                )
            )
        )

        if (response.isSuccessful) {
            return response.body()!!
        }

        val errorBody = response.errorBody()?.string()
        val errorCode = response.code()

        ErrorHandler.logError("RegistrationAPIManager", errorBody.toString())

        if (errorCode == 400 && errorBody?.contains("already registered") == true) {
            throw PassportAlreadyRegisteredByOtherPK()
        } else if (errorBody?.contains("already revoked") == true || errorBody?.contains("the leaf does not match") == true) {
            throw UserAlreadyRevoked()
        } else {
            throw Exception("Registration failed")
        }
    }
}