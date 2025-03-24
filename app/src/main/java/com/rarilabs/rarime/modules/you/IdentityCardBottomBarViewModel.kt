package com.rarilabs.rarime.modules.you

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.ProofGenerationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdentityCardBottomBarViewModel @Inject constructor(
    private val proofGenerationManager: ProofGenerationManager,
    private val passportManager: PassportManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdentityCardBottomBarUiState())
    val uiState: StateFlow<IdentityCardBottomBarUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            launch {
                proofGenerationManager.state.collect { newState ->
                    _uiState.value = _uiState.value.copy(loadingState = newState)
                }
            }

            launch {
                proofGenerationManager.proofError.collect { newError ->
                    _uiState.value = _uiState.value.copy(proofError = newError)
                }
            }

            launch {
                passportManager.passportStatus.collect() { newStatus ->
                    _uiState.value = _uiState.value.copy(passportStatus = newStatus)
                }
            }
        }
    }

    fun retryRegistration() {
        viewModelScope.launch {
            passportManager.passport.let {
                proofGenerationManager.performRegistration(passportManager.passport.value!!)
            }
        }
    }
}

