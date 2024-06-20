package com.rarilabs.rarime.api.auth

import coil.network.HttpException
import com.rarilabs.rarime.api.auth.models.AuthChallengeBody
import com.rarilabs.rarime.api.auth.models.RequestAuthorizeBody
import com.rarilabs.rarime.api.auth.models.RequestAuthorizeResponseBody
import com.rarilabs.rarime.api.auth.models.ValidateResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthAPIManager @Inject constructor(
    private val authAPI: AuthAPI
) {
    suspend fun authorize(body: RequestAuthorizeBody): RequestAuthorizeResponseBody {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.authorize(body)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getChallenge(nullifier: String): AuthChallengeBody {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.getChallenge(nullifier)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun refresh(authorization: String): RequestAuthorizeResponseBody {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.refresh(authorization)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun validate(authorization: String): ValidateResponseBody {
        return withContext(Dispatchers.IO) {
            try {
                authAPI.validate(authorization)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }
}