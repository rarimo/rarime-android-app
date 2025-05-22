package com.rarilabs.rarime.api.hiddenPrize

import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserRequest
import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserRequestData
import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserRequestData.CreateUserAttributes
import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserResponse
import com.rarilabs.rarime.api.hiddenPrize.models.HiddenPrizeClaimData
import com.rarilabs.rarime.api.hiddenPrize.models.HiddenPrizeClaimRequest
import com.rarilabs.rarime.api.hiddenPrize.models.HiddenPrizeClaimResponse
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessRequest
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessRequestData
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessResponse
import com.rarilabs.rarime.manager.AuthManager
import javax.inject.Inject


sealed class HiddenPrizeApiError : Exception() {
    class NotFound : HiddenPrizeApiError()
}

class HiddenPrizeApiManager @Inject constructor(
    private val api: HiddenPrizeApi, private val authManager: AuthManager
) {


    suspend fun claimTokens(
        calldata: String, noSend: Boolean = false
    ): HiddenPrizeClaimResponse {
        val request = HiddenPrizeClaimRequest(
            data = HiddenPrizeClaimData(
                tx_data = calldata,
                no_send = noSend,
                destination = BaseConfig.GUESS_CELEBRITY_CONTRACT_ADDRESS
            )
        )

        val response = api.claimTokens(request, "Bearer ${authManager.accessToken}")

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }


    suspend fun createNewUser(referredBy: String?, nullifier: String): CreateUserResponse {
        val response = api.createNewUser(
            body = CreateUserRequest(
                data = CreateUserRequestData(
                    id = nullifier,
                    attributes = CreateUserAttributes(
                        referred_by = referredBy
                    ),
                )
            ), "Bearer ${authManager.accessToken}"
        )

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()!!.string())
    }

    suspend fun getUserInfo(nullifier: String): CreateUserResponse {
        val response = api.getUserInfo(nullifier, "Bearer ${authManager.accessToken}")

        if (response.isSuccessful) {
            return response.body()!!
        }

        if (response.code() == 401) {
            throw HiddenPrizeApiError.NotFound()
        }

        if (response.code() == 404) {
            throw HiddenPrizeApiError.NotFound()
        }

        throw Exception(response.errorBody()!!.string())
    }

    suspend fun addExtraAttemptSocialShare(nullifier: String) {

        val response =
            api.addExtraAttemptSocialShare(nullifier, "Bearer ${authManager.accessToken}")

        if (!response.isSuccessful) {
            throw Exception(response.errorBody()!!.string())
        }
    }

    suspend fun submitCelebrityGuess(
        features: List<Float>, nullifier: String
    ): SubmitGuessResponse {

        val request = SubmitGuessRequest(
            data = SubmitGuessRequestData(
                attributes = SubmitGuessRequestData.Attributes(
                    features = features
                )
            )
        )


        val response =
            api.submitCelebrityGuess(nullifier, request, "Bearer ${authManager.accessToken}")

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()!!.string())
    }
}