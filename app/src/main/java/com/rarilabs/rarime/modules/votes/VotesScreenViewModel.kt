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

data class QuestionAnswerVariant(
    val id: String,
    val title: String,

    val votedCount: Double
)

data class VoteQuestion(
    val id: String,
    val title: String,

    val variants: List<QuestionAnswerVariant>,
)

data class VoteData(
    val title: String,
    val description: String,
    val durationMillis: Long,
    val participantsCount: Int,
    val questions: List<VoteQuestion>,
    val endDate: Long
)

@HiltViewModel
class VotesScreenViewModel @Inject constructor(
    private val votingManager: VotingManager,
    private val testContractManager: TestContractManager,
) : ViewModel() {


    private val _activeVotes = MutableStateFlow<List<Poll>>(emptyList())
    val activeVotes: StateFlow<List<Poll>> = _activeVotes.asStateFlow()

    private val _isLoadingActive = MutableStateFlow(false)
    val isLoadingActive: StateFlow<Boolean> = _isLoadingActive.asStateFlow()

    // History votes
    private val _historyVotes = MutableStateFlow<List<Poll>>(emptyList())
    val historyVotes: StateFlow<List<Poll>> = _historyVotes.asStateFlow()

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    val selectedVote = votingManager.selectedPoll

    fun setSelectedPoll(poll: Poll?) {
        votingManager.setSelectedPoll(poll)
    }

    val vote = votingManager::vote

    suspend fun loadPolls(isRefresh: Boolean = false) {
        withContext(Dispatchers.IO) {
            val allPolls = votingManager.loadLocalVotePolls()
            _activeVotes.value = allPolls.filter { !it.isEnded }
            _historyVotes.value = allPolls.filter { it.isEnded }
//            val allPolls = votingManager.loadVotePolls(isRefresh)
//            _activeVotes.value = allPolls.filter { !it.isEnded }
//            _historyVotes.value = allPolls.filter { it.isEnded }
        }
    }
}