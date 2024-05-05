package com.distributedLab.rarime.data.manager

import com.distributedLab.rarime.domain.data.AirdropRequest
import com.distributedLab.rarime.domain.data.AirdropResponse
import com.distributedLab.rarime.domain.data.CosmosSpendableBalancesResponse
import com.distributedLab.rarime.domain.data.EvmTxResponse
import com.distributedLab.rarime.domain.data.RegisterRequest
import com.distributedLab.rarime.domain.manager.APIServiceManager
import javax.inject.Inject

class ApiServiceRemoteData @Inject constructor(private val apiServiceManager: APIServiceManager) {
    suspend fun sendRegistration(registerRequest: RegisterRequest): EvmTxResponse? {
        val response = apiServiceManager.registerRequest(registerRequest)

        if (response.isSuccessful) {
            return response.body()!!
        }
        return null
    }


    suspend fun sendQuery(query: AirdropRequest): AirdropResponse? {
        val response = apiServiceManager.airdrop(query)

        if (response.isSuccessful) {
            return response.body()!!
        }

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

