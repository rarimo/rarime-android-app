package com.rarilabs.rarime.api.ext_integrator

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
            try {
                extIntegratorAPI.queryProofCallback(
                    url,
                    QueryProofGenCallbackRequest(
                        data = QueryProofGenCallbackRequestData(
                            id = userIdHash,
                            attributes = QueryProofGenCallbackRequestAttributes(
                                proof = proof
                            )
                        )
                    )
                )
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }
    suspend fun lightSignatureCallback(url: String, pubSignals: List<String>, signature: String, userIdHash: String) {
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
}