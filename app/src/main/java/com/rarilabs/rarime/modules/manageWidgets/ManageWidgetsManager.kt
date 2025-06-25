package com.rarilabs.rarime.modules.manageWidgets

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageWidgetsManager @Inject constructor(
    private val sharedPrefsManager: SecureSharedPrefsManager,
    walletManager: WalletManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val pointsToken = walletManager.pointsToken

    private val _visibleWidgets = MutableStateFlow<List<WidgetType>>(emptyList())
    val visibleWidgets: StateFlow<List<WidgetType>> get() = _visibleWidgets.asStateFlow()

    private val allManaged = listOf(
        WidgetType.FREEDOMTOOL,
        WidgetType.HIDDEN_PRIZE,
        WidgetType.RECOVERY_METHOD
    )
    val managedWidgets: StateFlow<List<WidgetType>> =
        MutableStateFlow(allManaged.filter { it != WidgetType.HIDDEN_PRIZE }).asStateFlow()

    init {
        scope.launch {
            loadInitialWidgets()
        }
        scope.launch {

            pointsToken
                .map { it?.balanceDetails?.attributes?.amount ?: 0L }
                .distinctUntilChanged()
                .filter { it > 0L }
                .first()
                .also { add(WidgetType.EARN) }
        }
    }

    private suspend fun loadInitialWidgets() {
        val stored = sharedPrefsManager.readVisibleWidgets()
            .orEmpty()
            .filter { it != WidgetType.HIDDEN_PRIZE }
            .toMutableList()

        if (stored.isEmpty()) {
            stored += WidgetType.RECOVERY_METHOD
            if ((pointsToken.value?.balanceDetails?.attributes?.amount ?: 0L) > 0L) {
                stored += WidgetType.EARN
            }
        }

        updateAndPersist(stored)
    }


    fun setVisibleWidgets(newVisible: List<WidgetType>) {
        updateAndPersist(newVisible)
    }

    @Synchronized
    fun add(widget: WidgetType) {
        val updated = (_visibleWidgets.value + widget)
            .distinct()
        updateAndPersist(updated)
    }

    @Synchronized
    fun remove(widget: WidgetType) {
        val updated = _visibleWidgets.value - widget
        updateAndPersist(updated)
    }

    private fun updateAndPersist(list: List<WidgetType>) {
        val processed = list
            .distinct()
            .sortedBy { it.layoutId }
            .toMutableSet()


        if (pointsToken.value?.balanceDetails?.attributes?.amount != null && pointsToken.value?.balanceDetails?.attributes?.amount != 0L) {
            processed.add(WidgetType.EARN)
        }

        val processedSortedList = processed.sortedBy { it.layoutId }

        if (_visibleWidgets.value != processed) {
            _visibleWidgets.value = processedSortedList
        }
        sharedPrefsManager.saveVisibleWidgets(processedSortedList)

    }
}
