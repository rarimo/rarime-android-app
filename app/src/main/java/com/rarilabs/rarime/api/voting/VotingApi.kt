package com.rarilabs.rarime.api.voting

import com.rarilabs.rarime.api.voting.models.SendTransactionRequest
import com.rarilabs.rarime.api.voting.models.VoteCountResponse
import com.rarilabs.rarime.api.voting.models.VoteV2Response
import com.rarilabs.rarime.api.voting.models.VotingInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface VotingApi {

    @GET("/integrations/proof-verification-relayer/v2/count-remaining-votes/{voting_id}")
    suspend fun countRemainingVotes(@Path("voting_id") voteId: String): Response<VoteCountResponse>

    @GET("/integrations/proof-verification-relayer/v2/voting-info")
    suspend fun getVotingData(
        @Query("creator") address: String?,
        @Query("citizenship") citizenship: String?,
        @Query("age") age: String?,
        @Query("voting_id") votingId: String?
    ): Response<VotingInfoResponse>


    @POST("/integrations/proof-verification-relayer/v2/vote")
    suspend fun voteV2(@Body body: SendTransactionRequest): Response<VoteV2Response>
}

