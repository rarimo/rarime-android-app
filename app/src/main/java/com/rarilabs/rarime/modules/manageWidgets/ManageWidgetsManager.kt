package com.rarilabs.rarime.modules.manageWidgets

import android.util.Log
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageWidgetsManager @Inject constructor(
    private val sharedPrefsManager: SecureSharedPrefsManager,
    private val walletManager: WalletManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val pointsToken = walletManager.pointsToken

    private val _visibleWidgets = MutableStateFlow<List<WidgetType>>(emptyList())
    val visibleWidgets: StateFlow<List<WidgetType>>
        get() = _visibleWidgets.asStateFlow()

    private val allManagedWidgets = listOf(
        WidgetType.FREEDOMTOOL,
        //WidgetType.LIKENESS,
        WidgetType.HIDDEN_PRIZE,
        WidgetType.RECOVERY_METHOD,
        //WidgetType.EARN
    )
    val managedWidgets: StateFlow<List<WidgetType>> =
        MutableStateFlow(allManagedWidgets.filter { it != WidgetType.HIDDEN_PRIZE }).asStateFlow()

    init {
        initializeVisibleWidgets()
        scope.launch {
            pointsToken.filter { it?.balanceDetails?.attributes?.amount != null && it.balanceDetails?.attributes?.amount != 0L }
                .distinctUntilChanged().collect {
                    Log.i("initializeVisibleWidgets", "SOSAL?")
                    initializeVisibleWidgets()
                }
        }
    }

    private fun initializeVisibleWidgets() {
        val visibleCardsStored = sharedPrefsManager.readVisibleWidgets()

        if (visibleCardsStored.isNullOrEmpty()) {
            val defaultWidgets = mutableListOf(WidgetType.RECOVERY_METHOD)
            val pointBalance = pointsToken.value?.balanceDetails


            if (pointBalance != null && pointBalance.attributes.amount != 0L) {
                defaultWidgets.add(WidgetType.EARN)
            }
            setVisibleWidgets(defaultWidgets)
        } else {
            if (pointsToken.value?.balanceDetails?.attributes?.amount != null && pointsToken.value?.balanceDetails?.attributes?.amount != 0L) {
                val tempCards = visibleCardsStored.toMutableList()
                tempCards.add(WidgetType.EARN)
                setVisibleWidgets(tempCards.filter { it != WidgetType.HIDDEN_PRIZE })
                return
            }

            setVisibleWidgets(visibleCardsStored.filter { it != WidgetType.HIDDEN_PRIZE })
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
