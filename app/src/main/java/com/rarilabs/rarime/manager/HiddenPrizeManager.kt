package com.rarilabs.rarime.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApiManager
import com.rarilabs.rarime.api.hiddenPrize.models.CelebrityStatus
import com.rarilabs.rarime.api.hiddenPrize.models.IncludedItem
import javax.inject.Inject

data class UserStats(
    val resetTime: Long, val extraAttemptsLeft: Int, val totalAttemptsCount: Int
)

data class Celebrity(
    val title: String,
    val description: String,
    val status: CelebrityStatus,
    val image: String,
    val hint: String
)

class HiddenPrizeManager @Inject constructor(
    private val apiManager: HiddenPrizeApiManager, private val identityManager: IdentityManager
) {
    var referralsLimit by mutableIntStateOf(0)
        private set

    var referralsCount by mutableIntStateOf(0)
        private set

    var socialShare by mutableStateOf(false)
        private set

    var referralCode by mutableStateOf<String?>(null)
        private set

    var userStats by mutableStateOf<UserStats?>(null)
        private set

    var celebrity by mutableStateOf<Celebrity?>(null)
        private set

    suspend fun createUser(referredBy: String = "") {
        val res = apiManager.createNewUser(referredBy)
        val attrs = res.data.attributes

        referralsLimit = attrs.referrals_limit
        referralsCount = attrs.referrals_count
        socialShare = attrs.social_share
    }

    suspend fun loadUserInfo() {
        val nullifier = identityManager.getNullifierForFaceLikeness()
        val res = apiManager.getUserInfo(nullifier)
        val a = res.data.attributes

        socialShare = a.social_share
        referralsCount = a.referrals_count
        referralsLimit = a.referrals_limit
        referralCode = a.referral_code

        userStats =
            res.included.filterIsInstance<IncludedItem.Stats>().firstOrNull()?.userStats?.let {
                UserStats(
                    resetTime = it.attributes.reset_time,
                    extraAttemptsLeft = it.attributes.extra_attempts_left,
                    totalAttemptsCount = it.attributes.total_attempts_count
                )
            }

        celebrity = res.included.filterIsInstance<IncludedItem.CelebrityItem>()
            .firstOrNull()?.celebrity?.attributes?.let {
                Celebrity(
                    title = it.title,
                    description = it.description,
                    status = it.status,
                    image = it.image,
                    hint = it.hint
                )
            }
    }

    suspend fun submitCelebrityGuess(faceFeatures: List<Float>) {
        val nullifier = identityManager.getNullifierForFaceLikeness()
        val res = apiManager.submitCelebrityGuess(faceFeatures, nullifier)

        userStats =
            res.included.filterIsInstance<IncludedItem.Stats>().firstOrNull()?.userStats?.let {
                UserStats(
                    resetTime = it.attributes.reset_time,
                    extraAttemptsLeft = it.attributes.extra_attempts_left,
                    totalAttemptsCount = it.attributes.total_attempts_count
                )
            }

        celebrity = res.included.filterIsInstance<IncludedItem.CelebrityItem>()
            .firstOrNull()?.celebrity?.attributes?.let {
                Celebrity(
                    title = it.title,
                    description = it.description,
                    status = it.status,
                    image = it.image,
                    hint = it.hint
                )
            }
    }
}
