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
    private var _visibleCards = MutableStateFlow<List<WidgetType>>(emptyList())

    val visibleCards: StateFlow<List<WidgetType>>
        get() = _visibleCards.asStateFlow()

    init {
        val visibleCardsStored = sharedPrefsManager.readVisibleCards()
        if (visibleCardsStored.isNullOrEmpty()) {
            setVisibleCards(WidgetType.values().toList())
        } else {
            _visibleCards.value = visibleCardsStored
        }

    }

    private var _managedCards = MutableStateFlow<List<WidgetType>>(
        listOf(
            WidgetType.FREEDOMTOOL,
            WidgetType.LIKENESS,
            WidgetType.HIDDEN_PRIZE,
            WidgetType.RECOVERY_METHOD
        )
    )//todo implement other manage cards

    val managedCards: StateFlow<List<WidgetType>>
        get() = _managedCards.asStateFlow()


    fun setVisibleCards(visibleCards: List<WidgetType>) {
        _visibleCards.value = (listOf(WidgetType.IDENTITY, WidgetType.CLAIM) + visibleCards).distinct()
            .sortedBy { it.layoutId }
        sharedPrefsManager.saveVisibleCards(_visibleCards.value)
    }

    fun remove(widgetType: WidgetType) {
        setVisibleCards(_visibleCards.value.filter { it != widgetType })
    }

    fun add(widgetType: WidgetType) {
        setVisibleCards((_visibleCards.value + widgetType))
    }
}

