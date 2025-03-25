package com.rarilabs.rarime.modules.you

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.ProofGenerationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IdentityCardBottomBarViewModel @Inject constructor(
    private val proofGenerationManager: ProofGenerationManager,
    private val passportManager: PassportManager
) : ViewModel()

