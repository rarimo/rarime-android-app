package com.rarilabs.rarime.api.cosmos

import javax.inject.Inject

class CosmosManager @Inject constructor(
    private val cosmosAPIManager: CosmosAPIManager
) {
    suspend fun getBalance(address: String) = cosmosAPIManager.getBalance(address)
}