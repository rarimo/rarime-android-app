package com.distributedLab.rarime.api.cosmos

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.cosmos.models.CosmosSpendableBalancesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CosmosAPI {
    @GET("${BaseConfig.COSMOS_RPC_URL}/cosmos/bank/v1beta1/spendable_balances/{address}")
    suspend fun getBalance(@Path("address") address: String) : Response<CosmosSpendableBalancesResponse>
}