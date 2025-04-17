package com.rarilabs.rarime.api.voting

import com.rarilabs.rarime.api.voting.models.IPFSResponseData
import com.rarilabs.rarime.api.voting.models.LatestOperationResponse
import com.rarilabs.rarime.api.voting.models.QRCodeVotingResponse
import com.rarilabs.rarime.api.voting.models.SendTransactionRequest
import com.rarilabs.rarime.api.voting.models.VoteCountResponse
import com.rarilabs.rarime.api.voting.models.VoteRequest
import com.rarilabs.rarime.api.voting.models.VoteResponse
import com.rarilabs.rarime.api.voting.models.VoteV2Response
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url


interface VotingApi {

    @GET("/integrations/proof-verification-relayer/v2/count-remaining-votes/{voting_id}")
    suspend fun countRemainingVotes(@Path("voting_id") voteId: String): Response<VoteCountResponse>



    @POST("/integrations/proof-verification-relayer/v2/vote")
    suspend fun voteV2(@Body body: SendTransactionRequest): Response<VoteV2Response>


    @GET
    suspend fun getDataFromIPFS(@Url url: String): Response<IPFSResponseData>

    @GET
    suspend fun getVotingInfo(@Url url: String): Response<QRCodeVotingResponse>

    @POST("/integrations/voting-relayer/v1/vote")
    suspend fun vote(@Body payload: VoteRequest): Response<VoteResponse>


    @GET("/integrations/voting-relayer/v1/operations/latest")
    suspend fun getLatestOperation(): Response<LatestOperationResponse>
}

