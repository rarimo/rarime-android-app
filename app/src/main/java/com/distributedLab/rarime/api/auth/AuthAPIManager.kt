package com.distributedLab.rarime.api.auth

import coil.network.HttpException
import com.distributedLab.rarime.api.auth.models.AuthChallenge
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeBody
import com.distributedLab.rarime.api.auth.models.RequestAuthorizePayload
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeResponse
import com.distributedLab.rarime.api.auth.models.ValidateResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthAPIManager @Inject constructor(
    private val authAPI: AuthAPI
) {
    suspend fun authorize(body: RequestAuthorizeBody): RequestAuthorizeResponse {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.authorize(body)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getChallenge(nullifier: String): AuthChallenge {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.getChallenge(nullifier)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun refresh(authorization: String): RequestAuthorizePayload {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.refresh(authorization)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun validate(authorization: String): ValidateResponse {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.validate(authorization)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }
}