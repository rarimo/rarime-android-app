package com.rarilabs.rarime.api.cosmos

import com.rarilabs.rarime.api.cosmos.models.CosmosSpendableBalancesResponse
import javax.inject.Inject

class CosmosAPIManager @Inject constructor(
    private val cosmosAPI: CosmosAPI
) {
    suspend fun getBalance(address: String): CosmosSpendableBalancesResponse {
        val response = cosmosAPI.getBalance(address)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }
}