package com.rarilabs.rarime.modules.earn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarilabs.rarime.api.points.models.PointsBalanceBody
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarnViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val pointsManager: PointsManager

) : ViewModel() {
    init {
        loadPointsBalance()
    }


    val colorScheme = settingsManager.colorScheme
    private val _pointBalanceBody = MutableStateFlow<PointsBalanceBody?>(null)
    val pointBalanceBody: StateFlow<PointsBalanceBody?>
        get() = _pointBalanceBody.asStateFlow()


    fun loadPointsBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = pointsManager.getPointsBalance()
            _pointBalanceBody.value = result
        }
    }

}