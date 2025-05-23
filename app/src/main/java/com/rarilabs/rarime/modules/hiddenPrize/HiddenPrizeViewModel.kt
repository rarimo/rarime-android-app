package com.rarilabs.rarime.modules.hiddenPrize

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApiError
import com.rarilabs.rarime.manager.HiddenPrizeManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.util.bionet.BinetAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HiddenPrizeViewModel @Inject constructor(
    private val hiddenPrizeManager: HiddenPrizeManager,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val downloadProgress = hiddenPrizeManager.downloadProgressZkey
    val celebrity = hiddenPrizeManager.celebrity
    val referralCode = hiddenPrizeManager.referralCode
    val userStats = hiddenPrizeManager.userStats
    val shares = hiddenPrizeManager.shares

    val colorScheme = settingsManager.colorScheme

    suspend fun addExtraAttempt() {
        hiddenPrizeManager.addExtraAttempts()
    }

    suspend fun checkCrop(bitmap: Bitmap): Bitmap? {
        val bionet = BinetAnalyzer()

        return bionet.checkBound(bitmap)
    }

    suspend fun generateFaceFeatures(bitmap: Bitmap): List<Float> {
        return hiddenPrizeManager.generateFaceFeatures(bitmap)
    }

    suspend fun claimTokens(bitmap: Bitmap, features: List<Float>) {
        hiddenPrizeManager.claimTokens(features, bitmap)
    }

    suspend fun loadUserInfo() {
        try {
            hiddenPrizeManager.loadUserInfo()
        } catch (e: HiddenPrizeApiError.NotFound) {
            hiddenPrizeManager.createUser(hiddenPrizeManager.getReferralCode())
            hiddenPrizeManager.loadUserInfo()
        }
    }
}