package com.rarilabs.rarime.modules.digitalLikeness

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


enum class LikenessRule(val value: Int) {
    ALWAYS_ALLOW(0), REJECT(0), ASK_EVERYTIME(0);

    companion object {
        fun fromInt(value: Int) = LikenessRule.entries.first { it.value == value }
    }
}

@HiltViewModel
class DigitalLikenessViewModel @Inject constructor(
    val sharedPrefsManager: SecureSharedPrefsManager
) : ViewModel() {
    private val _selectedRule =
        MutableStateFlow<LikenessRule>(sharedPrefsManager.getSelectedLikenessRule())

    private val _isScanned = MutableStateFlow<Boolean>(sharedPrefsManager.getIsLikenessScanned())


    val isScanned: StateFlow<Boolean>
        get() = _isScanned.asStateFlow()

    val selectedRule: StateFlow<LikenessRule>
        get() = _selectedRule.asStateFlow()

    fun setSelectedOption(selectedRule: LikenessRule) {
        sharedPrefsManager.saveSelectedLikenessRule(selectedRule)
        _selectedRule.value = selectedRule
    }

    fun setIsLikenessScanned(isScanned: Boolean) {
        sharedPrefsManager.saveIsLikenessScanned(isScanned)
        _isScanned.value = isScanned
    }


}