package com.rarilabs.rarime.api.registration

import javax.inject.Inject

class RegistrationManager @Inject constructor(
    private val registrationAPIManager: RegistrationAPIManager
) {
    suspend fun register(callData: ByteArray) = registrationAPIManager.register(callData)
}