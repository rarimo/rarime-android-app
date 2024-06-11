package com.distributedLab.rarime.modules.rewards.view_models

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.tokens.PointsToken
import com.distributedLab.rarime.domain.points.PointsEvent
import com.distributedLab.rarime.domain.points.PointsEventMeta
import com.distributedLab.rarime.domain.points.PointsEventMetaDynamic
import com.distributedLab.rarime.domain.points.PointsEventMetaStatic
import com.distributedLab.rarime.domain.points.PointsEventStatuses
import com.distributedLab.rarime.modules.common.PassportManager
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.common.WalletManager
import com.distributedLab.rarime.ui.components.MARKDOWN_CONTENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

/**
 * Better to keep this class, in order to development process
 * and minimal leaderboard object requirements
 */
data class LeaderBoardItem (
    val number: Int,
    val address: String,
    val balance: Double,
    val tokenIcon: Int,
)

val MOCKED_LEADER_BOARD_LIST = listOf(
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 1235566777888.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 122343.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.0,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 1.0,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "memememememememememememememememememmemememe",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
    LeaderBoardItem(
        number = 0,
        address = "0x1234567890abcdef",
        balance = 123.45,
        tokenIcon = R.drawable.ic_rarimo
    ),
).mapIndexed { idx, it -> it.copy(number = idx + 1, address = "${it.address}${idx}") }

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val walletManager: WalletManager,
    private val passportManager: PassportManager
) : ViewModel() {
    val passportStatus = passportManager.passportStatus

    val levelProgress = 0.36f

    private fun getPointsWalletAsset (): WalletAsset? {
        return walletManager.walletAssets.value.find { it.token is PointsToken }
    }

    var _pointsWalletAsset = MutableStateFlow(getPointsWalletAsset())
        private set

    val pointsWalletAsset: StateFlow<WalletAsset?>
        get() = _pointsWalletAsset.asStateFlow()

    var _limitedTimeEvents = MutableStateFlow<List<PointsEvent>?>(null)
        private set

    val limitedTimeEvents: StateFlow<List<PointsEvent>?>
        get() = _limitedTimeEvents.asStateFlow()

    var _activeTasksEvents = MutableStateFlow<List<PointsEvent>?>(null)
        private set

    val activeTasksEvents: StateFlow<List<PointsEvent>?>
        get() = _activeTasksEvents.asStateFlow()

    var _leaderBoardList = MutableStateFlow(listOf<LeaderBoardItem>())
        private set

    val leaderBoardList: StateFlow<List<LeaderBoardItem>>
        get() = _leaderBoardList.asStateFlow()

    suspend fun init() {
        delay(1000L * 3)

        _limitedTimeEvents.value = CONST_MOCKED_EVENTS_LIST.subList(0, 2)
        _activeTasksEvents.value = CONST_MOCKED_EVENTS_LIST.subList(0, 2)

        _pointsWalletAsset.value = getPointsWalletAsset()
        _leaderBoardList.value = MOCKED_LEADER_BOARD_LIST.mapIndexed { idx, it ->
            if (idx == 0) {
                pointsWalletAsset.value?.userAddress?.let { userAddress ->
                    it.copy(address = userAddress)
                } ?: it
            } else {
                it
            }
        }
    }
}