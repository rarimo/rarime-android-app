package com.rarilabs.rarime.modules.manageWidgets

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ManageWidgetsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val widgetsManager: ManageWidgetsManager,

    ) : ViewModel() {
    val colorScheme = settingsManager.colorScheme
    val managedWidgets = widgetsManager.managedWidgets
    val visibleWidgets = widgetsManager.visibleWidgets

    fun isVisible(widgetType: WidgetType): Boolean {
        return (widgetType in visibleWidgets.value)
    }

    fun setVisibleWidgets() {
        widgetsManager.setVisibleWidgets(managedWidgets.value.filter { widgetType ->
            isVisible(
                widgetType
            )
        })
    }

    fun remove(widgetType: WidgetType) {
        widgetsManager.remove(widgetType)
    }

    fun add(widgetType: WidgetType) {
        widgetsManager.add(widgetType)
    }
}
