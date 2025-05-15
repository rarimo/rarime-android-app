package com.rarilabs.rarime.modules.hiddenPrize

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.HiddenPrizeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HiddenPrizeViewModel @Inject constructor(
    private val hiddenPrizeManager: HiddenPrizeManager
) : ViewModel() {


    val getFaceFeatures = hiddenPrizeManager::generateFaceFeatures

    val claimTokens = hiddenPrizeManager::claimTokens
}