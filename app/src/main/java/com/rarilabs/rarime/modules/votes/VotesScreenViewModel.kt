package com.rarilabs.rarime.modules.votes

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.voting.VotingManager
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.manager.TestContractManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class VotesScreenViewModel @Inject constructor(
    private val votingManager: VotingManager,
    private val testContractManager: TestContractManager,
) : ViewModel() {
    private var _activePolls = MutableStateFlow(emptyList<Poll>())
    private var _finishedPolls = MutableStateFlow(emptyList<Poll>())

    fun setSelectedPoll(poll: Poll) {
        votingManager.setSelectedPoll(poll)
    }

    val activePolls: StateFlow<List<Poll>>
        get() = _activePolls.asStateFlow()

    val finishedPolls: StateFlow<List<Poll>>
        get() = _finishedPolls.asStateFlow()

    suspend fun loadPolls(isRefresh: Boolean = false) {
        withContext(Dispatchers.IO) {
            val allPolls = votingManager.loadVotePolls(isRefresh)
            _activePolls.value = allPolls.filter { !it.isEnded }
            _finishedPolls.value = allPolls.filter { it.isEnded }
        }
    }
}