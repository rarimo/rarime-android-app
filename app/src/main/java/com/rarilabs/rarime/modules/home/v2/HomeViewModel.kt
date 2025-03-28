package com.rarilabs.rarime.modules.home.v2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rarilabs.rarime.api.airdrop.AirDropManager
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.points.models.BaseEvents
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.manager.NotificationManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val passportManager: PassportManager,
    private val airDropManager: AirDropManager,
    private val walletManager: WalletManager,
    private val pointsManager: PointsManager,
    private val notificationManager: NotificationManager,
    private val registrationManager: RegistrationManager,
    private val sharedPrefsManager: SecureSharedPrefsManager
) : AndroidViewModel(app) {

    val pointsToken = walletManager.pointsToken

    val passport = passportManager.passport

    private var _pointsEventData = MutableStateFlow<PointsEventData?>(null)

    val notifications = notificationManager.notificationList

    val pointsEventData: StateFlow<PointsEventData?>
        get() = _pointsEventData.asStateFlow()


    suspend fun initHomeData() = withContext(Dispatchers.IO) {
        coroutineScope {
            try {
                val pointsDeferred = async { loadPointsEvent() }
                pointsDeferred.await()
            } catch (e: Exception) {

            }
        }
    }

    private suspend fun loadPointsEvent() {
        val activeTasksEvents = pointsManager.getActiveEvents().data

        val points = activeTasksEvents.filter {
            it.attributes.meta.static.name == BaseEvents.REFERRAL_COMMON.value
        }

        _pointsEventData.value = points[0]
    }

}