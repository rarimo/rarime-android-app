package com.rarilabs.rarime.modules.you

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ZkIdentityNoPassportViewModel @Inject constructor(val passportManager: PassportManager) :
    ViewModel() {

    fun setJsonEDocument(eDocument: EDocument) {

        passportManager.setPassport(eDocument)
        passportManager.updatePassportStatus(status = PassportStatus.UNREGISTERED)

    }

}