package com.rarilabs.rarime.modules.intro

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.IdentityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val identityManager: IdentityManager,
) : ViewModel() {


    fun savePrivateKey() {
        val pk = identityManager.genPrivateKey()
        identityManager.savePrivateKey(pk)
    }

}