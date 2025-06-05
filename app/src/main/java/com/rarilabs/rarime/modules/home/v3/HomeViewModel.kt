package com.rarilabs.rarime.modules.home.v3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rarilabs.rarime.api.points.models.BaseEvents
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.manager.NotificationManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.VotingManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.modules.manageWidgets.ManageWidgetsManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    app: Application,
    passportManager: PassportManager,
    private val walletManager: WalletManager,
    private val pointsManager: PointsManager,
    widgetsManager: ManageWidgetsManager,
    notificationManager: NotificationManager,
    votingManager: VotingManager,
    settingsManager: SettingsManager,
    private val sharedPrefsManager: SecureSharedPrefsManager
) : AndroidViewModel(app) {

    val pointsToken = walletManager.pointsToken

    private val allUserVotes: StateFlow<List<Poll>> = votingManager.allVotesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val colorScheme = settingsManager.colorScheme
    var visibleCards = widgetsManager.visibleCards
    val hasVotes: StateFlow<Boolean> =
        allUserVotes.map { it.isNotEmpty() }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val passport = passportManager.passport

    private var _pointsEventData = MutableStateFlow<PointsEventData?>(null)

    val notifications = notificationManager.notificationList

    val pointsEventData: StateFlow<PointsEventData?>
        get() = _pointsEventData.asStateFlow()


    suspend fun initHomeData() = withContext(Dispatchers.IO) {
        coroutineScope {
            try {
                walletManager.loadBalances()
                val pointsDeferred = async { loadPointsEvent() }
                pointsDeferred.await()
            } catch (e: Exception) {

            }
        }
    }


    fun saveIsShownWelcome(boolean: Boolean) {
        sharedPrefsManager.saveIsShownWelcome(boolean)
    }

    fun getIsShownWelcome(): Boolean {
        return sharedPrefsManager.getIsShownWelcome()
    }

    private suspend fun loadPointsEvent() {
        val activeTasksEvents = pointsManager.getActiveEvents().data

        val points = activeTasksEvents.filter {
            it.attributes.meta.static.name == BaseEvents.REFERRAL_COMMON.value
        }

        _pointsEventData.value = points.getOrNull(0)
    }

}