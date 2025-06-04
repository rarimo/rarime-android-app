package com.rarilabs.rarime.modules.manageWidgets

import com.rarilabs.rarime.modules.home.v3.model.CardType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageWidgetsManager @Inject constructor(

) {
    private var _visibleCards = MutableStateFlow<List<CardType>>(CardType.values().asList())

    val visibleCards: StateFlow<List<CardType>>
        get() = _visibleCards.asStateFlow()


    fun getManagedCards(): List<CardType> {
        return listOf(
            CardType.FREEDOMTOOL,
            CardType.LIKENESS,
            CardType.HIDDEN_PRIZE,
            CardType.RECOVERY_METHOD
        )
    }

    fun isVisible(cardType: CardType): Boolean {
        return cardType in _visibleCards.value
    }

    fun setVisibleCard(visibleCards: List<CardType>) {
        _visibleCards.value = (listOf(CardType.IDENTITY, CardType.CLAIM) + visibleCards).distinct()
            .sortedBy { it.layoutId }
    }

    fun remove(cardType: CardType) {
        setVisibleCard(_visibleCards.value.filter { it != cardType })
    }

    fun add(cardType: CardType) {
        setVisibleCard((_visibleCards.value + cardType))
    }
}

