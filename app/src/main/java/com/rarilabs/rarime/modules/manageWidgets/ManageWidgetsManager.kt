package com.rarilabs.rarime.modules.manageWidgets

import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageWidgetsManager @Inject constructor(
    private val sharedPrefsManager: SecureSharedPrefsManager
) {
    private var _visibleWidgets = MutableStateFlow<List<WidgetType>>(emptyList())

    val visibleWidgets: StateFlow<List<WidgetType>>
        get() = _visibleWidgets.asStateFlow()

    init {
        val visibleCardsStored = sharedPrefsManager.readVisibleWidgets()
        if (visibleCardsStored.isNullOrEmpty()) {

            setVisibleWidgets(listOf(WidgetType.EARN))
        } else {
            _visibleWidgets.value = visibleCardsStored
        }

    }

    private var _managedWidgets = MutableStateFlow<List<WidgetType>>(
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
        _visibleWidgets.value = (listOf(WidgetType.EARN) + visibleWidgets).distinct()
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

