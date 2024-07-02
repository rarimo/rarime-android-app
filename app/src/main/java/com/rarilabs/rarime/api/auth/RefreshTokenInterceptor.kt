package com.rarilabs.rarime.api.auth

import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class RefreshTokenInterceptor @Inject constructor(
    private val authManager: dagger.Lazy<AuthManager>,
    private val refreshRetrofit: Retrofit
) : Interceptor {
    private var isRefreshing = AtomicBoolean(false)

    private suspend fun refresh() {
        authManager.get().refreshToken.value?.let {
            val refreshAuthAPIManager = AuthAPIManager(refreshRetrofit.create(AuthAPI::class.java))

            val response = refreshAuthAPIManager.refresh(
                authorization = "Bearer $it"
            )

            authManager.get().updateTokens(
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
                try {
                    // Check if the token was already refreshed to avoid multiple refreshes
                    runBlocking {
                        if (!isRefreshing.get()) {
                            isRefreshing.set(true)
                            try {
                                runBlocking {
                                    refresh()
                                }
                            } catch (e: Exception) {
                                ErrorHandler.logError("RefreshTokenInterceptor", "Error refreshing token", e)
                            } finally {
                                isRefreshing.set(false)
                            }
                        }
                    }

                    val newAccessToken = authManager.get().accessToken.value

                    if (newAccessToken?.isNotEmpty() == true) {
                        // Retry the request with the new token
                        val newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                        return chain.proceed(newRequest)
                    } else {
                        // Handle the case where the token refresh failed (e.g., logout user)
                        runBlocking {
                            // authManager.get().logout() // Implement logout if needed
                        }
                    }
                } catch (e: Exception) {
                    ErrorHandler.logError("interceptor", "Error refreshing token", e)
                }
            }
        }

        return response
    }
}
