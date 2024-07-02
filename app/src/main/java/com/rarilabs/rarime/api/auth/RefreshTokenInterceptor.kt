package com.rarilabs.rarime.api.auth

import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RefreshTokenInterceptor @Inject constructor(
    private val authManager: AuthManager,
    private val refreshAuthAPIManager: AuthAPIManager
) : Interceptor {
    private var isRefreshing = false

    private suspend fun refreshToken() {
        authManager.accessToken.value?.let {
            val response = refreshAuthAPIManager.refresh(
                authorization = it
            )

            authManager.updateTokens(
                accessToken = response.data.attributes.access_token.token,
                refreshToken = response.data.attributes.refresh_token.token
            )
        } ?: throw IllegalStateException("Access token is not set")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            synchronized(this) {
                if (!isRefreshing) {
                    isRefreshing = true
                    try {
                        runBlocking {
                            refreshToken()
                        }
                    } catch (e: Exception) {
                        ErrorHandler.logError("RefreshTokenInterceptor", "Error refreshing token", e)
                    } finally {
                        isRefreshing = false
                    }
                }
            }

            val newAccessToken = authManager.accessToken.value
            return if (newAccessToken?.isNotEmpty() == true) {
                // Retry the request with the new token
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                chain.proceed(newRequest)
            } else {
                // Handle the case where the token refresh failed (e.g., logout user)
                response
            }
        }

        return response
    }
}
