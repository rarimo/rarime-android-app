package com.rarilabs.rarime.api.voting

import com.rarilabs.rarime.api.voting.models.IPFSResponseData
import com.rarilabs.rarime.api.voting.models.LatestOperationResponse
import com.rarilabs.rarime.api.voting.models.SendTransactionAttributes
import com.rarilabs.rarime.api.voting.models.SendTransactionData
import com.rarilabs.rarime.api.voting.models.SendTransactionRequest
import com.rarilabs.rarime.api.voting.models.VoteRequest
import com.rarilabs.rarime.api.voting.models.VoteResponse
import com.rarilabs.rarime.api.voting.models.VoteV2Response
import javax.inject.Inject

class VotingApiManager @Inject constructor(
    private val votingApi: VotingApi
) {

    suspend fun countRemainingVotes(
        votingId: String
    ): Long {

        val response = votingApi.countRemainingVotes(votingId)

        if (response.isSuccessful) {
            response.body()?.data?.attributes?.voteCount ?: 0
        }

        throw Exception(response.errorBody()?.string())
    }


    suspend fun getIPFSData(url: String): IPFSResponseData {
        val response = votingApi.getDataFromIPFS(url)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string())
    }

    suspend fun vote(request: VoteRequest): VoteResponse {
        val response = votingApi.vote(request)
        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string())
    }

    suspend fun getLatestOperation(): LatestOperationResponse {
        val response = votingApi.getLatestOperation()

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string())
    }


//    suspend fun getVotingData(
//        address: String?,
//        citizenship: String?,
//        age: String?,
//        votingId: String?
//    ): List<VotingInfoData> {
//
//        val response = votingApi.getVotingData(address, citizenship, age, votingId)
//
//        if (response.isSuccessful) {
//            return response.body()?.data ?: emptyList()
//        }
//
//        throw Exception(response.errorBody()?.string())
//    }

    suspend fun voteV2(
        txData: String,
        destination: String
    ): VoteV2Response {
        val response = votingApi.voteV2(
            SendTransactionRequest(
                data = SendTransactionData(
                    type = "send_transaction",
                    attributes = SendTransactionAttributes(
                        txData = txData,
                        destination = destination
                    )
                )
            )
        )

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty body of response")
        }

        throw Exception(response.errorBody()?.string())
    }

}