package com.rarilabs.rarime.modules.you

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarilabs.rarime.manager.PassportProofState
import com.rarilabs.rarime.manager.ProofGenerationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IdentityCardBottomBarViewModel(
    private val proofGenerationManager: ProofGenerationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdentityCardBottomBarUiState())
    val uiState: StateFlow<IdentityCardBottomBarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            proofGenerationManager.state.collect { newState ->
                _uiState.value = _uiState.value.copy(state = newState)
            }
        }
    }
}
