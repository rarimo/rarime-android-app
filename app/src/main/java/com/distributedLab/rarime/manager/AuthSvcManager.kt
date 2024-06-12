package com.distributedLab.rarime.manager

import com.distributedLab.rarime.domain.auth.AuthChallenge
import com.distributedLab.rarime.domain.auth.JsonApiAuthSvcManager
import com.distributedLab.rarime.domain.auth.RequestAuthorizeBody
import com.distributedLab.rarime.domain.auth.RequestAuthorizePayload
import com.distributedLab.rarime.domain.auth.RequestAuthorizeResponse
import com.distributedLab.rarime.domain.auth.ValidateResponse
import javax.inject.Inject

class AuthSvcManager @Inject constructor(
    private val jsonApiAuthSvcManager: JsonApiAuthSvcManager
) {
    suspend fun authorize(payload: RequestAuthorizePayload): RequestAuthorizeResponse? {
        val response = jsonApiAuthSvcManager.authorize(
            RequestAuthorizeBody(
                data = payload
            )
        )

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun getChallenge(nullifier: String): AuthChallenge? {
        val response = jsonApiAuthSvcManager.getChallenge(nullifier)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun refresh(authorize: String): RequestAuthorizePayload? {
        val response = jsonApiAuthSvcManager.refresh(authorize)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun validate(authorize: String): ValidateResponse? {
        val response = jsonApiAuthSvcManager.validate(authorize)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}