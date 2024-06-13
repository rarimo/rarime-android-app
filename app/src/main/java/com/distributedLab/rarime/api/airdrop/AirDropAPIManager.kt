package com.distributedLab.rarime.api.airdrop

import android.util.Log
import com.distributedLab.rarime.api.airdrop.models.AirDropResponseBody
import com.distributedLab.rarime.api.airdrop.models.AirdropEventParamsBody
import com.distributedLab.rarime.api.airdrop.models.CreateAirDropBody
import com.google.gson.Gson
import javax.inject.Inject

class AirDropAPIManager @Inject constructor(private val airDropAPI: AirDropAPI) {
    suspend fun createAirDrop(body: CreateAirDropBody): AirDropResponseBody {
        val response = airDropAPI.createAirDrop(body)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString() ?: "Unknown error")
    }

    suspend fun getAirDropByNullifier(nullifier: String): AirDropResponseBody {
        val response = airDropAPI.getAirDropByNullifier(nullifier)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString() ?: "Unknown error")
    }

    suspend fun getAirDropParams(): AirdropEventParamsBody {
        val response = airDropAPI.getAirDropParams()

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString() ?: "Unknown error")
    }
}