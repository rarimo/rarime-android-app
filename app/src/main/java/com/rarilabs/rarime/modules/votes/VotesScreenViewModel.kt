package com.rarilabs.rarime.modules.votes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VoteData(
    val title: String,
    val description: String,
    val durationMillis: Long,
    val participantsCount: Int,
    val options: List<VoteOption>,
    val endDate: Long
)

data class VoteOption(
    val id: String,
    val title: String,
    val votedCount: Double
)

class VotesScreenViewModel @Inject constructor(): ViewModel() {
    // Active votes
    private val _activeVotes = MutableStateFlow<List<VoteData>>(emptyList())
    val activeVotes: StateFlow<List<VoteData>> = _activeVotes.asStateFlow()

    private val _isLoadingActive = MutableStateFlow(false)
    val isLoadingActive: StateFlow<Boolean> = _isLoadingActive.asStateFlow()

    // History votes
    private val _historyVotes = MutableStateFlow<List<VoteData>>(emptyList())
    val historyVotes: StateFlow<List<VoteData>> = _historyVotes.asStateFlow()

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    init {
        loadActiveVotes()
        loadHistoryVotes()
    }

    private fun loadActiveVotes() {
        viewModelScope.launch {
            _isLoadingActive.value = true
            delay(800) // Simulate network delay

            _activeVotes.value = listOf(
                VoteData(
                    title = "Protocol Update Proposal",
                    description = "Vote on the proposed update to the network protocol",
                    durationMillis = 86400000 * 3, // 3 days
                    participantsCount = 320,
                    options = listOf(
                        VoteOption("1", "Approve", 180.0),
                        VoteOption("2", "Reject", 87.0),
                        VoteOption("3", "Abstain", 53.0)
                    ),
                    endDate = System.currentTimeMillis() + 86400000 * 2 // Ends in 2 days
                ),
                VoteData(
                    title = "Treasury Allocation",
                    description = "Vote on allocating treasury funds for development",
                    durationMillis = 86400000 * 7, // 7 days
                    participantsCount = 412,
                    options = listOf(
                        VoteOption("1", "Approve", 205.0),
                        VoteOption("2", "Reject", 102.0),
                        VoteOption("3", "Abstain", 105.0)
                    ),
                    endDate = System.currentTimeMillis() + 86400000 * 5 // Ends in 5 days
                )
            )
            _isLoadingActive.value = false
        }
    }

    private fun loadHistoryVotes() {
        viewModelScope.launch {
            _isLoadingHistory.value = true
            delay(800) // Simulate network delay

            _historyVotes.value = listOf(
                VoteData(
                    title = "Governance Structure Change",
                    description = "Vote on proposed changes to governance structure",
                    durationMillis = 86400000 * 5, // 5 days (already ended)
                    participantsCount = 275,
                    options = listOf(
                        VoteOption("1", "Approve", 145.0),
                        VoteOption("2", "Reject", 110.0),
                        VoteOption("3", "Abstain", 20.0)
                    ),
                    endDate = System.currentTimeMillis() - 86400000 * 2 // Ended 2 days ago
                ),
                VoteData(
                    title = "Fee Structure Update",
                    description = "Vote on proposed changes to transaction fee structure",
                    durationMillis = 86400000 * 4, // 4 days (already ended)
                    participantsCount = 390,
                    options = listOf(
                        VoteOption("1", "Approve", 250.0),
                        VoteOption("2", "Reject", 130.0),
                        VoteOption("3", "Abstain", 10.0)
                    ),
                    endDate = System.currentTimeMillis() - 86400000 * 7 // Ended 7 days ago
                )
            )
            _isLoadingHistory.value = false
        }
    }
}