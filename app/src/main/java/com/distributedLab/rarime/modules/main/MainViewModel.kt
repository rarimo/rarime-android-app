package com.distributedLab.rarime.modules.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) : ViewModel() {
    var isIntroFinished = mutableStateOf(dataStoreManager.readIsIntroFinished())
        private set

    fun finishIntro() {
        isIntroFinished.value = true
        dataStoreManager.saveIsIntroFinished(true)
    }
}