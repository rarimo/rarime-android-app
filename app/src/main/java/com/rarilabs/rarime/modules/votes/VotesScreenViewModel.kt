package com.rarilabs.rarime.modules.votes

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.voting.VotingManager
import com.rarilabs.rarime.api.voting.models.Poll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
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
) : ViewModel() {

    val activeVotes: StateFlow<List<Poll>> = votingManager.activeVotes

    val isLoading = votingManager.isVotesLoading

    val historyVotes: StateFlow<List<Poll>> = votingManager.historyVotes

    val selectedVote = votingManager.selectedPoll

    fun setSelectedPoll(poll: Poll?) {
        votingManager.setSelectedPoll(poll)
    }

    val checkIsVoted = votingManager::checkIsVoted

    val vote = votingManager::vote

    suspend fun loadPolls(isRefresh: Boolean = false) {
        withContext(Dispatchers.IO) {
            votingManager.loadLocalVotePolls()
//            val allPolls = votingManager.loadVotePolls(isRefresh)
//            _activeVotes.value = allPolls.filter { !it.isEnded }
//            _historyVotes.value = allPolls.filter { it.isEnded }
        }
    }
}