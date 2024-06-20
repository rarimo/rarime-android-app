package com.rarilabs.rarime.modules.rewards.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RewardsEventItemViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val pointsManager: PointsManager,
    private val walletManager: WalletManager,
): ViewModel() {
    val itemId: String = checkNotNull(savedStateHandle["item_id"])

    var _pointsEventData = MutableStateFlow<PointsEventData?>(null)
        private set

    val pointsEventData: StateFlow<PointsEventData?>
        get() = _pointsEventData.asStateFlow()

    val pointsToken = walletManager.pointsToken

    suspend fun loadPointsEvent () {
        _pointsEventData.value = pointsManager.getEventById(itemId)?.data
    }
}