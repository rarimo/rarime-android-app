package com.rarilabs.rarime.api.voting

import com.rarilabs.rarime.api.voting.models.IPFSResponseData
import com.rarilabs.rarime.api.voting.models.LatestOperationResponse
import com.rarilabs.rarime.api.voting.models.QRCodeVotingResponse
import com.rarilabs.rarime.api.voting.models.SendTransactionAttributes
import com.rarilabs.rarime.api.voting.models.SendTransactionData
import com.rarilabs.rarime.api.voting.models.SendTransactionRequest
import com.rarilabs.rarime.api.voting.models.VoteRequest
import com.rarilabs.rarime.api.voting.models.VoteResponse
import com.rarilabs.rarime.api.voting.models.VoteV2Response
import com.rarilabs.rarime.manager.VoteError
import javax.inject.Inject

class VotingApiManager @Inject constructor(
    private val votingApi: VotingApi
) {

//    suspend fun countRemainingVotes(
//        votingId: String
//    ): Long {
//
//        val response = votingApi.countRemainingVotes(votingId)
//
//        if (response.isSuccessful) {
//            response.body()?.data?.attributes?.voteCount ?: 0
//        }
//
//        throw Exception(response.errorBody()?.string())
//    }


    suspend fun getIPFSData(url: String): IPFSResponseData {
        val response = votingApi.getDataFromIPFS("https://ipfs.rarimo.com/ipfs/$url")

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

    suspend fun getVotingInfo(url: String): QRCodeVotingResponse {
        val response = votingApi.getVotingInfo(url)

        if (response.isSuccessful) {
            return response.body()!!
        }

        if (response.code() == 404) {
            throw VoteError.NotFound(response.errorBody()?.string().toString())
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
                        tx_data = txData,
                        destination = destination
                    )
                )
            )
        )

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty body of response")
        }

        if (response.errorBody()?.string()?.contains("gas") == true) {
            throw VoteError.NotEnoughTokens(details = response.errorBody()!!.string())
        }

        if (response.code() == 403) {
            throw VoteError.NotEnoughTokens(response.errorBody()?.string().toString())
        }


        throw VoteError.NetworkError(response.errorBody()?.string().toString())
    }


}