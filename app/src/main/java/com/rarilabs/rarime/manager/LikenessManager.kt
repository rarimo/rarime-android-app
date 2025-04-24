package com.rarilabs.rarime.manager

import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class LikenessRule(val value: Int) {
    ALWAYS_ALLOW(0), REJECT(1), ASK_EVERYTIME(2);

    companion object {
        fun fromInt(value: Int) = LikenessRule.entries.first { it.value == value }
    }
}

@Singleton
class LikenessManager @Inject constructor(
    val sharedPrefsManager: SecureSharedPrefsManager
) {

    private val _selectedRule =
        MutableStateFlow<LikenessRule>(sharedPrefsManager.getSelectedLikenessRule())

    private val _isScanned = MutableStateFlow<Boolean>(sharedPrefsManager.getIsLikenessScanned())

    val isScanned: StateFlow<Boolean>
        get() = _isScanned.asStateFlow()

    val selectedRule: StateFlow<LikenessRule>
        get() = _selectedRule.asStateFlow()

    fun setSelectedRule(selectedRule: LikenessRule) {
        sharedPrefsManager.saveSelectedLikenessRule(selectedRule)
        _selectedRule.value = selectedRule
    }

    fun setIsLikenessScanned(isScanned: Boolean) {
        sharedPrefsManager.saveIsLikenessScanned(isScanned)
        _isScanned.value = isScanned
    }
}