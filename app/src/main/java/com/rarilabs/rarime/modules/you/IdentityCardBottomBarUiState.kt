package com.rarilabs.rarime.modules.you

import com.rarilabs.rarime.manager.PassportProofState

data class IdentityCardBottomBarUiState(
    val state: PassportProofState = PassportProofState.READING_DATA,
)