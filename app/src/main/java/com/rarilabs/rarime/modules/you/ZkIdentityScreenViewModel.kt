package com.rarilabs.rarime.modules.you

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.PassportManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ZkIdentityScreenViewModel @Inject constructor(val passportManager: PassportManager) :
    ViewModel() {

    val passport = passportManager.passport

}