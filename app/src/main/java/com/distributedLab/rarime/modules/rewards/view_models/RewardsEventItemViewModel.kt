package com.distributedLab.rarime.modules.rewards.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.points.models.PointsEventData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class RewardsEventItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
//    private val pointsManager: PointsManager,
): ViewModel() {
    val itemId: String = checkNotNull(savedStateHandle["item_id"])

    var _pointsEventData = MutableStateFlow<PointsEventData?>(null)
        private set

    val pointsEventData: StateFlow<PointsEventData?>
        get() = _pointsEventData.asStateFlow()

    suspend fun loadPointsEvent () {
        // _pointsEvent.value = pointsManager.getEvent(itemId)
        _pointsEventData.value = CONST_MOCKED_EVENTS_LIST[0]
    }
}