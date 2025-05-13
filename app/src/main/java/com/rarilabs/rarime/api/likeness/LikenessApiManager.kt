package com.rarilabs.rarime.api.likeness

import com.rarilabs.rarime.api.likeness.models.LikenessRequest
import com.rarilabs.rarime.api.likeness.models.LikenessResponse
import com.rarilabs.rarime.api.likeness.models.RegisterRequestData
import javax.inject.Inject

class LikenessApiManager @Inject constructor(private val likenessApi: LikenessApi) {
    suspend fun likenessRegistry(calldata: String, noSend: Boolean = false): LikenessResponse {
        val request = LikenessRequest(
            data = RegisterRequestData(
                tx_data = calldata, no_send = noSend
            )
        )

        val response = likenessApi.likenessRegistry(request)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

}