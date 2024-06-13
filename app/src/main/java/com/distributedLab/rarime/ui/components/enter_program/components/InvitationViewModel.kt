package com.distributedLab.rarime.ui.components.enter_program.components

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.points.PointsAPIManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val pointsAPIManager: PointsAPIManager
): ViewModel() {
    suspend fun createBalance(referralCode: String) {
        pointsAPIManager.createPointsBalance(referralCode)
    }
}