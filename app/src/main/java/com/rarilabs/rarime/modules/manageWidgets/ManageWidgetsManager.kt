package com.rarilabs.rarime.modules.manageWidgets

import com.rarilabs.rarime.api.points.models.PointsBalanceBody
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageWidgetsManager @Inject constructor(
    private val sharedPrefsManager: SecureSharedPrefsManager,
    private val pointsManager: PointsManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _visibleWidgets = MutableStateFlow<List<WidgetType>>(emptyList())
    val visibleWidgets: StateFlow<List<WidgetType>> = _visibleWidgets.asStateFlow()

    private val allManagedWidgets = listOf(
        WidgetType.FREEDOMTOOL,
        //WidgetType.LIKENESS,
        WidgetType.HIDDEN_PRIZE,
        WidgetType.RECOVERY_METHOD,
        //WidgetType.EARN
    )
    val managedWidgets: StateFlow<List<WidgetType>> =
        MutableStateFlow(allManagedWidgets).asStateFlow()

    init {
        scope.launch {
            initializeVisibleWidgets()
        }
    }

    private suspend fun initializeVisibleWidgets() {
        val visibleCardsStored = sharedPrefsManager.readVisibleWidgets()

        if (visibleCardsStored.isNullOrEmpty()) {
            val defaultWidgets = mutableListOf(WidgetType.HIDDEN_PRIZE, WidgetType.RECOVERY_METHOD)
            val pointBalance: PointsBalanceBody? = pointsManager.getPointsBalance()

            if (pointBalance != null && pointBalance.data.attributes.amount != 0L) {
                defaultWidgets.add(WidgetType.EARN)
            }
            setVisibleWidgets(defaultWidgets)
        } else {
            _visibleWidgets.value = visibleCardsStored
        }
    }

    fun setVisibleWidgets(newVisibleWidgets: List<WidgetType>) {
        val processedList = newVisibleWidgets.distinct().sortedBy { it.layoutId }

        if (_visibleWidgets.value != processedList) {
            _visibleWidgets.value = processedList
            scope.launch {
                sharedPrefsManager.saveVisibleWidgets(processedList)
            }
        }
    }

    fun remove(widgetType: WidgetType) {
        setVisibleWidgets(_visibleWidgets.value - widgetType)
    }

    fun add(widgetType: WidgetType) {
        setVisibleWidgets(_visibleWidgets.value + widgetType)
    }
}
