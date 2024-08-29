package com.rarilabs.rarime.api.ext_integrator

import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.rarilabs.rarime.api.ext_integrator.models.RequestDataResponse
import javax.inject.Inject

class ExtIntegratorApiManager @Inject constructor(
    private val extIntegratorAPI: ExtIntegratorAPI
) {
    suspend fun callback(url: String, body: String) {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.callback(url, body)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getRequestData(url: String): RequestDataResponse {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.getRequestData(url)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }
}