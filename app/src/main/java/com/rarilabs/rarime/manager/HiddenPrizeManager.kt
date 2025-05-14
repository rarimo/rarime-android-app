package com.rarilabs.rarime.manager

import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApiManager
import javax.inject.Inject

class HiddenPrizeManager @Inject constructor(
    private val apiManager: HiddenPrizeApiManager,
    private val identityManager: IdentityManager
) {

    suspend fun createUser(referredBy: String = "") {
        val res = apiManager.createNewUser(referredBy)

        res.data.attributes.referrals_limit
        res.data.attributes.referrals_count
        res.data.attributes.created_at

        res.data.attributes.updated_at
        res.data.attributes.social_share

        res.data.attributes.social_share
    }


    suspend fun submitCelebrityGuess(faceFeatures: List<Float>) {
        val nullifier = identityManager.getNullifierForFaceLikeness()

        val response = apiManager.submitCelebrityGuess(faceFeatures, nullifier)

        if (!response.data.attributes.success) {
            //TODO NO
            return
        }


        val originalFeatures = response.data.attributes.original_feature_vector


    }


    suspend fun getUserInfo() {
        val nullifier = identityManager.getNullifierForFaceLikeness()


        val userResponse = apiManager.getUserInfo(nullifier)

        userResponse.data.attributes
    }
}