package com.rarilabs.rarime.manager

import android.graphics.Bitmap
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class LikenessRule(val value: Int) {
    NOT_SELECTED(0), ALWAYS_ALLOW(1), REJECT(2), ASK_EVERYTIME(3);

    companion object {
        fun fromInt(value: Int) = LikenessRule.entries.first { it.value == value }
    }
}

@Singleton
class LikenessManager @Inject constructor(
    val sharedPrefsManager: SecureSharedPrefsManager
) {

    private val _selectedRule = MutableStateFlow(sharedPrefsManager.getSelectedLikenessRule())

    private val _isScanned = MutableStateFlow(sharedPrefsManager.getIsLikenessScanned())


    private val _faceImage: MutableStateFlow<Bitmap?> = MutableStateFlow(loadFaceImage())

    val faceImage: StateFlow<Bitmap?> = _faceImage.asStateFlow()

    val isScanned: StateFlow<Boolean>
        get() = _isScanned.asStateFlow()

    val selectedRule: StateFlow<LikenessRule?>
        get() = _selectedRule.asStateFlow()

    fun setSelectedRule(selectedRule: LikenessRule) {
        sharedPrefsManager.saveSelectedLikenessRule(selectedRule)
        _selectedRule.value = selectedRule
    }

    fun saveFaceImage(face: Bitmap) {
        sharedPrefsManager.saveLikenessFace(face)
        _faceImage.value = face
    }

    private fun loadFaceImage(): Bitmap? = sharedPrefsManager.getLikenessFace()


    fun setIsLikenessScanned(isScanned: Boolean) {
        sharedPrefsManager.saveIsLikenessScanned(isScanned)
        _isScanned.value = isScanned
    }

}