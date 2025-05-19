package com.rarilabs.rarime.modules.hiddenPrize

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApiError
import com.rarilabs.rarime.manager.HiddenPrizeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiddenPrizeViewModel @Inject constructor(
    private val hiddenPrizeManager: HiddenPrizeManager
) : ViewModel() {


    val downloadProgress = hiddenPrizeManager.downloadProgressZkey
    val celebrity = hiddenPrizeManager.celebrity


    init {
        viewModelScope.launch {
            loadUserInfo()
        }
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
            hiddenPrizeManager.createUser()
            hiddenPrizeManager.loadUserInfo()
        }
    }
}