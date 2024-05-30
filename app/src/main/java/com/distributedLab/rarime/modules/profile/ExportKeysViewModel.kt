package com.distributedLab.rarime.modules.profile

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.IdentityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExportKeysViewModel @Inject constructor(
    identityManager: IdentityManager
) : ViewModel() {
    val privateKey = identityManager.privateKey!!
}