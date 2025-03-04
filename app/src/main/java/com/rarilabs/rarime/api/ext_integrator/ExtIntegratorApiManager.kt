package com.rarilabs.rarime.api.ext_integrator

import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.api.ext_integrator.models.LightSignatureCallbackRequest
import com.rarilabs.rarime.api.ext_integrator.models.LightSignatureCallbackRequestAttributes
import com.rarilabs.rarime.api.ext_integrator.models.LightSignatureCallbackRequestData
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenCallbackRequest
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenCallbackRequestAttributes
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenCallbackRequestData
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.util.data.ZkProof
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExtIntegratorApiManager @Inject constructor(
    private val extIntegratorAPI: ExtIntegratorAPI
) {
    suspend fun queryProofCallback(url: String, proof: ZkProof, userIdHash: String) {
        return withContext(Dispatchers.IO) {
            val payload = QueryProofGenCallbackRequest(
                data = QueryProofGenCallbackRequestData(
                    id = userIdHash,
                    attributes = QueryProofGenCallbackRequestAttributes(
                        proof = proof
                    )
                )
            )
            val str = Gson().toJson(payload)
            Log.i("payload", str)
            try {
                extIntegratorAPI.queryProofCallback(
                    url,
                    payload
                )
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun lightSignatureCallback(
        url: String,
        pubSignals: List<String>,
        signature: String,
        userIdHash: String
    ) {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.lightSignatureCallback(
                    url,
                    LightSignatureCallbackRequest(
                        data = LightSignatureCallbackRequestData(
                            id = userIdHash,
                            attributes = LightSignatureCallbackRequestAttributes(
                                pub_signals = pubSignals,
                                signature = signature
                            )
                        )
                    )
                )
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun queryProofData(url: String): QueryProofGenResponse {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.queryProofData(url)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun queryIpfsData(url: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = extIntegratorAPI.queryIpfsData(url)

                Gson().toJson(response)
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }
}