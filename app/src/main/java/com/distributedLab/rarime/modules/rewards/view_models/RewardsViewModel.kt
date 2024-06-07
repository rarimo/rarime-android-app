package com.distributedLab.rarime.modules.rewards.view_models

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.tokens.PointsToken
import com.distributedLab.rarime.domain.points.PointsEvent
import com.distributedLab.rarime.domain.points.PointsEventMeta
import com.distributedLab.rarime.domain.points.PointsEventMetaDynamic
import com.distributedLab.rarime.domain.points.PointsEventMetaStatic
import com.distributedLab.rarime.domain.points.PointsEventStatuses
import com.distributedLab.rarime.modules.common.PointsManager
import com.distributedLab.rarime.modules.common.WalletManager
import com.distributedLab.rarime.ui.components.MARKDOWN_CONTENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

val CONST_MOCKED_EVENTS_LIST = listOf(
    PointsEvent(
        status = PointsEventStatuses.OPEN,
        createdAt = 0,
        updatedAt = 0,
        meta = PointsEventMeta(
            static = PointsEventMetaStatic(
                name = "Lorem ipsum 1",
                reward = 5,
                title = "Lorem ipsum 1",
                description = MARKDOWN_CONTENT,
                shortDescription = "Lorem ipsum dolor sit amet!",
                frequency = "",
                startsAt = "",
                expiresAt = "",
                actionUrl = "",
                logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            ),
            dynamic = PointsEventMetaDynamic(
                id = "",
            ),
        ),
        pointsAmount = 0,
        balance = null,
    ),
    PointsEvent(
        status = PointsEventStatuses.CLAIMED,
        createdAt = 0,
        updatedAt = 0,
        meta = PointsEventMeta(
            static = PointsEventMetaStatic(
                name = "Lorem ipsum 2",
                reward = 50,
                title = "Lorem ipsum 2",
                description = MARKDOWN_CONTENT,
                shortDescription = "Lorem ipsum dolor sit amet concestetur!",
                frequency = "",
                startsAt = "",
                expiresAt = "",
                actionUrl = "",
                logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            ),
            dynamic = PointsEventMetaDynamic(
                id = "",
            ),
        ),
        pointsAmount = 0,
        balance = null,
    ),
    PointsEvent(
        status = PointsEventStatuses.FULFILLED,
        createdAt = 0,
        updatedAt = 0,
        meta = PointsEventMeta(
            static = PointsEventMetaStatic(
                name = "Lorem ipsum 3",
                reward = 500,
                title = "Lorem ipsum 3",
                description = MARKDOWN_CONTENT,
                shortDescription = "Lorem ipsum dolor sit amet concestetur! Lorem ipsum dolor sit amet concestetur!",
                frequency = "",
                startsAt = "",
                expiresAt = "",
                actionUrl = "",
                logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            ),
            dynamic = PointsEventMetaDynamic(
                id = "",
            ),
        ),
        pointsAmount = 0,
        balance = null,
    ),
)

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val walletManager: WalletManager,
//    private val pointsManager: PointsManager,
) : ViewModel() {
    val pointsWalletAsset = walletManager.walletAssets.value.find { it.token is PointsToken }!!

    var _limitedTimeEvents = MutableStateFlow<List<PointsEvent>?>(null)
        private set

    val limitedTimeEvents: StateFlow<List<PointsEvent>?>
        get() = _limitedTimeEvents.asStateFlow()

    var _activeTasksEvents = MutableStateFlow<List<PointsEvent>?>(null)
        private set

    val activeTasksEvents: StateFlow<List<PointsEvent>?>
        get() = _activeTasksEvents.asStateFlow()

    suspend fun loadPointsEvents() {
        _limitedTimeEvents.value = CONST_MOCKED_EVENTS_LIST.subList(0, 2)
        _activeTasksEvents.value = CONST_MOCKED_EVENTS_LIST.subList(0, 2)
    }
}