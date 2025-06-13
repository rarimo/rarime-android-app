package com.rarilabs.rarime.modules.digitalLikeness

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.LikenessManager
import com.rarilabs.rarime.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DigitalLikenessViewModel @Inject constructor(
    private val likenessManager: LikenessManager,
    private val settigsManager: SettingsManager
) : ViewModel() {

    val isRegistered = likenessManager.isRegistered

    val selectedRule = likenessManager.selectedRule

    val livenessState = likenessManager.state

    val errorState = likenessManager.errorState

    val setSelectedRule = likenessManager::setSelectedRule

    val faceImage = likenessManager.faceImage
    val saveFaceImage = likenessManager::saveFaceImage

    val downloadProgress = likenessManager.downloadProgress

    val colorScheme = settigsManager.colorScheme

    suspend fun processImage(bitmap: Bitmap) {
        likenessManager.livenessProofGeneration(bitmap = bitmap)
    }
}