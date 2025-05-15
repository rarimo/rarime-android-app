package com.rarilabs.rarime.modules.rewards.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.BaseEvents
import com.rarilabs.rarime.api.points.models.PointsEventAttributes
import com.rarilabs.rarime.api.points.models.PointsEventBody
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.api.points.models.PointsEventMeta
import com.rarilabs.rarime.api.points.models.PointsEventMetaDynamic
import com.rarilabs.rarime.api.points.models.PointsEventMetaStatic
import com.rarilabs.rarime.api.points.models.PointsEventStatuses
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.manager.AuthManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.ui.components.MARKDOWN_CONTENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

val CONST_MOCKED_EVENTS_LIST = listOf(
    PointsEventData(
        id = "1",
        type = "balance",
        attributes = PointsEventAttributes(
            status = PointsEventStatuses.OPEN.value,
            createdAt = 0,
            updatedAt = 0,
            meta = PointsEventMeta(
                static = PointsEventMetaStatic(
                    name = BaseEvents.PASSPORT_SCAN.value,
                    reward = 50,
                    title = "Lorem ipsum 1",
                    description = MARKDOWN_CONTENT,
                    shortDescription = "Lorem ipsum dolor sit amet!",
                    frequency = "",
                    startsAt = null,
                    expiresAt = null,
                    actionUrl = "",
                    logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    flag = "",
                ),
                dynamic = PointsEventMetaDynamic(
                    id = "",
                ),
            ),
            pointsAmount = 0,
            balance = null,
        )
    ),
    PointsEventData(
        id = "2",
        type = "balance",
        attributes = PointsEventAttributes(
            status = PointsEventStatuses.CLAIMED.value,
            createdAt = 0,
            updatedAt = 0,
            meta = PointsEventMeta(
                static = PointsEventMetaStatic(
                    name = BaseEvents.REFERRAL_COMMON.value,
                    reward = 50,
                    title = "Lorem ipsum 2",
                    description = MARKDOWN_CONTENT,
                    shortDescription = "Lorem ipsum dolor sit amet concestetur!",
                    frequency = "",
                    startsAt = null,
                    expiresAt = "",
                    actionUrl = "",
                    logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    flag = ""
                ),
                dynamic = PointsEventMetaDynamic(
                    id = "",
                ),
            ),
            pointsAmount = 0,
            balance = null,
        ),
    ),
    PointsEventData(
        id = "3",
        type = "balance",
        attributes = PointsEventAttributes(
            status = PointsEventStatuses.FULFILLED.value,
            createdAt = 0,
            updatedAt = 0,
            meta = PointsEventMeta(
                static = PointsEventMetaStatic(
                    name = BaseEvents.FREE_WEEKLY.value,
                    reward = 500,
                    title = "Lorem ipsum 3",
                    description = MARKDOWN_CONTENT,
                    shortDescription = "Lorem ipsum dolor sit amet concestetur! Lorem ipsum dolor sit amet concestetur!",
                    frequency = "",
                    startsAt = null,
                    expiresAt = "",
                    actionUrl = "",
                    logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    flag = ""
                ),
                dynamic = PointsEventMetaDynamic(
                    id = "",
                ),
            ),
            pointsAmount = 0,
            balance = null,
        )
    ),
)

/**
 * Better to keep this class, in order to development process
 * and minimal leaderboard object requirements
 */
data class LeaderBoardItem(
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
    private val passportManager: PassportManager,
    private val authManager: AuthManager,
    private val pointsManager: PointsManager,

    ) : ViewModel() {
    val passportStatus = passportManager.passportStatus

    val isAuthorized = authManager.isAuthorized

    private fun getPointsWalletAsset(): WalletAsset? {
        return walletManager.walletAssets.value.find { it.token is PointsToken }
    }

    var _pointsWalletAsset = MutableStateFlow(getPointsWalletAsset())
        private set

    val pointsWalletAsset: StateFlow<WalletAsset?>
        get() = _pointsWalletAsset.asStateFlow()

    val pointsToken = walletManager.pointsToken

    var _limitedTimeEvents = MutableStateFlow<List<PointsEventData>?>(null)
        private set

    val limitedTimeEvents: StateFlow<List<PointsEventData>?>
        get() = _limitedTimeEvents.asStateFlow()

    var _activeTasksEvents = MutableStateFlow<List<PointsEventData>?>(null)
        private set

    val activeTasksEvents: StateFlow<List<PointsEventData>?>
        get() = _activeTasksEvents.asStateFlow()

    var _leaderBoardList = MutableStateFlow(listOf<LeaderBoardItem>())
        private set

    val leaderBoardList: StateFlow<List<LeaderBoardItem>>
        get() = _leaderBoardList.asStateFlow()

    fun getUserLeaderBoardItem(): LeaderBoardItem {
        return LeaderBoardItem(
            number = pointsToken.value?.balanceDetails?.attributes?.rank?.toInt() ?: 0,
            address = pointsToken.value?.balanceDetails?.id ?: "",
            balance = pointsToken.value?.balanceDetails?.attributes?.amount?.toDouble() ?: 0.0,
            tokenIcon = R.drawable.ic_rarimo
        )
    }

    var _userLeaderBoardItem = MutableStateFlow(getUserLeaderBoardItem())
    val userLeaderBoardItem: StateFlow<LeaderBoardItem>
        get() = _userLeaderBoardItem.asStateFlow()

    fun getNationality(): String? {
        return passportManager.getIsoCode()
    }


    suspend fun claimRewardsEvent(eventName: String): PointsEventBody {
        val events = pointsManager.getActiveEventsByName(eventName)
        val currentEvent = events.data.firstOrNull()
        if (currentEvent == null) {
            Log.i("claimRewardsEvent", "events.data is empty")
            throw IllegalStateException("Event is Null")
        }

        return pointsManager.claimPointsByEventId(currentEvent.id, "claim_event")
    }

    suspend fun refreshEvents() = coroutineScope {
        val activeTasksEventsDeferred = async { pointsManager.getActiveEvents().data }
        _activeTasksEvents.value = activeTasksEventsDeferred.await()
    }

    suspend fun init() = coroutineScope {
        val limitedTimeEventsDeferred = async { pointsManager.getTimeLimitedEvents().data }
        val activeTasksEventsDeferred = async { pointsManager.getActiveEvents().data }
        val loadBalancesJob = launch { walletManager.loadBalances() }
        val pointsWalletAssetDeferred = async { getPointsWalletAsset() }
        val leaderBoardDeferred = async {
            val response = pointsManager.getLeaderBoard()
            response.data.mapIndexed { idx, it ->
                LeaderBoardItem(
                    number = it.attributes.rank?.toInt() ?: (idx + 1),
                    address = it.id,
                    balance = it.attributes.amount.toDouble(),
                    tokenIcon = R.drawable.ic_rarimo
                )
            }
        }
        val userLeaderBoardItemDeferred = async { getUserLeaderBoardItem() }

        _limitedTimeEvents.value = limitedTimeEventsDeferred.await()
        _activeTasksEvents.value = activeTasksEventsDeferred.await()
        loadBalancesJob.join()
        _pointsWalletAsset.value = pointsWalletAssetDeferred.await()
        _leaderBoardList.value = leaderBoardDeferred.await()
        _userLeaderBoardItem.value = userLeaderBoardItemDeferred.await()
    }

    suspend fun login() {
        authManager.login()
    }
}