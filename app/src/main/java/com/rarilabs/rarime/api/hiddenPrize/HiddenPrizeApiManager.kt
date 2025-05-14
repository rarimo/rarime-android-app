package com.rarilabs.rarime.api.hiddenPrize

import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserRequest
import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserResponse
import com.rarilabs.rarime.api.hiddenPrize.models.GetUserResponse
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessRequest
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessResponse
import javax.inject.Inject

class HiddenPrizeApiManager @Inject constructor(
    private val api: HiddenPrizeApi
) {


    suspend fun createNewUser(referredBy: String): CreateUserResponse {
        val response = api.createNewUser(
            body = CreateUserRequest(
                data = CreateUserRequest.Data(
                    attributes = CreateUserRequest.Data.Attributes(
                        referred_by = referredBy
                    )
                )
            )
        )

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()!!.string())
    }


    suspend fun getUserInfo(nullifier: String): GetUserResponse {
        val response = api.getUserInfo(nullifier)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()!!.string())
    }

    suspend fun addExtraAttemptSocialShare(nullifier: String) {
        val response = api.getUserInfo(nullifier)

        if (!response.isSuccessful) {
            throw Exception(response.errorBody()!!.string())
        }
    }

    suspend fun submitCelebrityGuess(
        features: List<Float>,
        nullifier: String
    ): SubmitGuessResponse {

        val request = SubmitGuessRequest(
            data = SubmitGuessRequest.Data(
                attributes = SubmitGuessRequest.Data.Attributes(
                    features = features
                )
            )
        )

        val response = api.submitCelebrityGuess(nullifier, request)

        if (response.isSuccessful) {
            return response.body()!!
        }



        throw Exception(response.errorBody()!!.string())
    }
}