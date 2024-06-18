package com.distributedLab.rarime.api.airdrop

import com.distributedLab.rarime.api.airdrop.models.AirDropResponseBody
import com.distributedLab.rarime.api.airdrop.models.AirdropEventParamsBody
import com.distributedLab.rarime.api.airdrop.models.CreateAirDropBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class AirDropAPIManager @Inject constructor(private val airDropAPI: AirDropAPI) {
    suspend fun createAirDrop(body: CreateAirDropBody): AirDropResponseBody? {
        return withContext(Dispatchers.IO) {
            try {
                airDropAPI.createAirDrop(body)
            } catch (e: HttpException) {
                if (e.response()?.code() != 409) {
                    throw Exception(e.toString())
                }

                null
            }
        }
    }

    suspend fun getAirDropByNullifier(nullifier: String): AirDropResponseBody {
        try {
            return airDropAPI.getAirDropByNullifier(nullifier)
        } catch (e: Exception) {
            throw Exception(e.toString())
        }
    }

    suspend fun getAirDropParams(): AirdropEventParamsBody {
        return withContext(Dispatchers.IO) {
            try {
                airDropAPI.getAirDropParams()
            } catch (e: Exception) {
                throw Exception(e.toString())
            }
        }
    }
}