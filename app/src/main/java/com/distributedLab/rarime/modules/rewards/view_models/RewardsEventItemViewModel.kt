package com.distributedLab.rarime.modules.rewards.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class RewardsEventItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val itemId: String = checkNotNull(savedStateHandle["item_id"])
}