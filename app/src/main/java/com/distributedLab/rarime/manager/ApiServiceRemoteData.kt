package com.distributedLab.rarime.manager

import android.util.Log
import com.distributedLab.rarime.domain.data.AirdropRequest
import com.distributedLab.rarime.domain.data.AirdropResponse
import com.distributedLab.rarime.domain.data.CosmosSpendableBalancesResponse
import com.distributedLab.rarime.domain.data.EvmTxResponse
import com.distributedLab.rarime.domain.data.GetAirdropParamsResponse
import com.distributedLab.rarime.domain.data.RegisterRequest
import com.distributedLab.rarime.domain.data.RegisterRequestData
import com.distributedLab.rarime.domain.manager.APIServiceManager
import com.google.gson.Gson
import javax.inject.Inject

class ApiServiceRemoteData @Inject constructor(private val apiServiceManager: APIServiceManager) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun sendRegistration(callData: ByteArray): EvmTxResponse? {

        val payload =
            RegisterRequest(data = RegisterRequestData(tx_data = "0x" + callData.toHexString()))
        Log.i("PAYLOAD", Gson().toJson(payload))
        val response = apiServiceManager.registerRequest(payload)

        if (response.isSuccessful) {
            return response.body()!!
        }
        response.errorBody()?.string()?.let { Log.e("ERROR", it) }
        return null
    }


    suspend fun getAirdropParams(): GetAirdropParamsResponse? {
        val response = apiServiceManager.airdropParams()
        if (response.isSuccessful) {
            return response.body()!!
        }
        Log.i("AIRDROP PARAMS", response.errorBody()?.string().toString())
        return null
    }


    suspend fun sendQuery(query: AirdropRequest): AirdropResponse? {
        val response = apiServiceManager.airdrop(query)
        if (response.isSuccessful) {
            return response.body()!!
        }
        Log.e("AIR DROP ERROR", response.errorBody()?.string().toString())
        return null
    }


    suspend fun fetchBalance(address: String): CosmosSpendableBalancesResponse? {
        val response = apiServiceManager.fetchBalance(address)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

}

