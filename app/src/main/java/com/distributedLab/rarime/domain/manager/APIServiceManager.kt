package com.distributedLab.rarime.domain.manager

import com.distributedLab.rarime.BaseConfig.COSMOS_RPC_URL
import com.distributedLab.rarime.BaseConfig.RELAYER_URL
import com.distributedLab.rarime.domain.data.AirdropRequest
import com.distributedLab.rarime.domain.data.AirdropResponse
import com.distributedLab.rarime.domain.data.CosmosSpendableBalancesResponse
import com.distributedLab.rarime.domain.data.EvmTxResponse
import com.distributedLab.rarime.domain.data.GetAirdropParamsResponse
import com.distributedLab.rarime.domain.data.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIServiceManager {
    @POST("$RELAYER_URL/integrations/registration-relayer/v1/register")
    suspend fun registerRequest(@Body body: RegisterRequest): Response<EvmTxResponse>

    @POST("$RELAYER_URL/integrations/airdrop-svc/airdrops")
    suspend fun airdrop( @Body body: AirdropRequest) : Response<AirdropResponse>


    @GET("$COSMOS_RPC_URL/cosmos/bank/v1beta1/spendable_balances/{address}")
    suspend fun fetchBalance(@Path("address") address: String) : Response<CosmosSpendableBalancesResponse>

    @GET("$RELAYER_URL/integrations/airdrop-svc/airdrops/params")
    suspend fun airdropParams(): Response<GetAirdropParamsResponse>
}