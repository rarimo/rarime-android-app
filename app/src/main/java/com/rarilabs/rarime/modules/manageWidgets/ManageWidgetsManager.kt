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

    private var _managedCards = MutableStateFlow<List<CardType>>(
        listOf(
            CardType.FREEDOMTOOL,
            CardType.LIKENESS,
            CardType.HIDDEN_PRIZE,
            CardType.RECOVERY_METHOD
        )
    )//todo implement other manage cards

    val managedCards: StateFlow<List<CardType>>
        get() = _managedCards.asStateFlow()


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

