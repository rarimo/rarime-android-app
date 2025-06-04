package com.rarilabs.rarime.modules.manageWidgets

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.HiddenPrizeManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.modules.home.v3.model.CardType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ManageWidgetsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val widgetsManager: ManageWidgetsManager
) : ViewModel() {
    val colorScheme = settingsManager.colorScheme
    val managedCards = widgetsManager.managedCards
    val visisbleCards = widgetsManager.visibleCards

    fun isVisible(cardType:CardType): Boolean{
        return (cardType in visisbleCards.value)
    }

    fun setVisibleCard(){
        widgetsManager.setVisibleCard(managedCards.value.filter {cardType -> isVisible(cardType) })
    }

    fun remove(cardType: CardType){
        widgetsManager.remove(cardType)
    }
    fun add(cardType: CardType){
        widgetsManager.add(cardType)
    }
}
