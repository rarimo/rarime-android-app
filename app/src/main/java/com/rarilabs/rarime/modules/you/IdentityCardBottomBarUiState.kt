package com.rarilabs.rarime.modules.you

import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.manager.PassportProofState

data class IdentityCardBottomBarUiState(
    val loadingState: PassportProofState = PassportProofState.READING_DATA,
    val proofError: Exception? = null,
    val passportStatus: PassportStatus? = null
)