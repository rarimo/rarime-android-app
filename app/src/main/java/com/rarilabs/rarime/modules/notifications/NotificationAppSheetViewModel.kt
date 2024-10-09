package com.rarilabs.rarime.modules.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.points.models.PointsEventBody
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationAppSheetViewModel @Inject constructor(private val pointsManager: PointsManager) :
    ViewModel() {
    suspend fun checkIfRewarded(eventName: String?): Boolean {

        if (eventName == null) {
            return true
        }
        val events = pointsManager.getActiveEventsByName(eventName)
        val currentEvent = events.data.firstOrNull()
        if (currentEvent == null) {
            Log.i("claimRewardsEvent", "events.data is empty")
            return true
        }
        return false
    }

    suspend fun claimRewardsEvent(eventName: String): PointsEventBody? {
        val events = pointsManager.getActiveEventsByName(eventName)
        val currentEvent = events.data.firstOrNull()
        if (currentEvent == null) {
            Log.i("claimRewardsEvent", "events.data is empty")
            throw IllegalStateException()
//            return null
        }

        return pointsManager.claimPointsByEventId(currentEvent.id, "claim_event")
    }

}