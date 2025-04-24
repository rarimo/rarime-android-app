package com.rarilabs.rarime.modules.digitalLikeness

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.LikenessManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DigitalLikenessViewModel @Inject constructor(
    private val likenessManager: LikenessManager
) : ViewModel() {

    val isLivenessScanned = likenessManager.isScanned

    val selectedRule = likenessManager.selectedRule


    val setSelectedRule = likenessManager::setSelectedRule

    val setIsLivenessScanned = likenessManager::setIsLikenessScanned

    val faceImage = likenessManager.faceImage
    val saveFaceImage = likenessManager::saveFaceImage
}