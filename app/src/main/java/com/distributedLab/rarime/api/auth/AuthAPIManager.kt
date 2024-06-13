package com.distributedLab.rarime.api.auth

import com.distributedLab.rarime.api.auth.models.AuthChallenge
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeBody
import com.distributedLab.rarime.api.auth.models.RequestAuthorizePayload
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeResponse
import com.distributedLab.rarime.api.auth.models.ValidateResponse
import javax.inject.Inject

class AuthAPIManager @Inject constructor(
    private val authAPI: AuthAPI
) {
    suspend fun authorize(payload: RequestAuthorizePayload): RequestAuthorizeResponse? {
        val response = authAPI.authorize(
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
        val response = authAPI.getChallenge(nullifier)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun refresh(authorize: String): RequestAuthorizePayload? {
        val response = authAPI.refresh(authorize)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun validate(authorize: String): ValidateResponse? {
        val response = authAPI.validate(authorize)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}