package com.distributedLab.rarime.ui.components.enter_program.components

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.points.PointsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val pointsManager: PointsManager
): ViewModel() {
    suspend fun createBalance(referralCode: String) {
        pointsManager.createPointsBalance(referralCode)
    }
}