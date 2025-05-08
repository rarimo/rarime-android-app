package com.rarilabs.rarime.modules.digitalLikeness

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.LikenessManager
import com.rarilabs.rarime.util.data.ZkProof
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DigitalLikenessViewModel @Inject constructor(
    private val likenessManager: LikenessManager
) : ViewModel() {

    val isLivenessScanned = likenessManager.isScanned

    val selectedRule = likenessManager.selectedRule

    val livenessState = likenessManager.state

    val setSelectedRule = likenessManager::setSelectedRule

    val setIsLivenessScanned = likenessManager::setIsLikenessScanned

    val faceImage = likenessManager.faceImage
    val saveFaceImage = likenessManager::saveFaceImage

    val downloadProgress = likenessManager.downloadProgress

    suspend fun processImage(bitmap: Bitmap): ZkProof {
        return likenessManager.livenessProofGeneration(bitmap = bitmap)
    }
}