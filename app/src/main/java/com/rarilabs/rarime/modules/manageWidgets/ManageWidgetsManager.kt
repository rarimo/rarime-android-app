package com.rarilabs.rarime.modules.manageWidgets

import com.rarilabs.rarime.api.points.models.PointsBalanceBody
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageWidgetsManager @Inject constructor(
    private val sharedPrefsManager: SecureSharedPrefsManager,
    private val pointsManager: PointsManager
) {
    private var _visibleWidgets = MutableStateFlow<List<WidgetType>>(emptyList())

    val visibleWidgets: StateFlow<List<WidgetType>>
        get() = _visibleWidgets.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val pointBalance: PointsBalanceBody? = withContext(Dispatchers.IO) {
                pointsManager.getPointsBalance()
            }
            val visibleCardsStored = sharedPrefsManager.readVisibleWidgets()
            if (visibleCardsStored.isNullOrEmpty()) {
                setVisibleWidgets(listOf(WidgetType.HIDDEN_PRIZE, WidgetType.RECOVERY_METHOD))
                if (pointBalance != null && pointBalance.data.attributes.amount != 0L) {
                    add(widgetType = WidgetType.EARN)
                }
            } else {
                _visibleWidgets.value = visibleCardsStored
            }
        }

    }

    private var _managedWidgets = MutableStateFlow(
        listOf(
            WidgetType.FREEDOMTOOL,
            WidgetType.LIKENESS,
            WidgetType.HIDDEN_PRIZE,
            WidgetType.RECOVERY_METHOD
        )
    )//todo implement other manage cards

    val managedWidgets: StateFlow<List<WidgetType>>
        get() = _managedWidgets.asStateFlow()


    fun setVisibleWidgets(visibleWidgets: List<WidgetType>) {
        _visibleWidgets.value = visibleWidgets.distinct()
            .sortedBy { it.layoutId }
        sharedPrefsManager.saveVisibleWidgets(_visibleWidgets.value)
    }

    fun remove(widgetType: WidgetType) {
        setVisibleWidgets(_visibleWidgets.value.filter { it != widgetType })
    }

    fun add(widgetType: WidgetType) {
        setVisibleWidgets((_visibleWidgets.value + widgetType))
    }
}

