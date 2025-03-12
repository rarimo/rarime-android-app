package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rarilabs.rarime.modules.votes.QuestionAnswerVariant
import com.rarilabs.rarime.modules.votes.VoteData
import com.rarilabs.rarime.modules.votes.VoteQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoteProcessScreenViewModel @Inject constructor() : ViewModel() {
    private val _voteData = MutableStateFlow<VoteData?>(null)
    val voteData: StateFlow<VoteData?> = _voteData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadVoteData(voteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // In a real app, fetch data from repository based on voteId
            delay(1000) // Simulate network request
            _voteData.value = VoteData(
                title = "Vote #$voteId",
                description = "This is a vote about proposal #$voteId",
                durationMillis = 86400000, // 24 hours
                participantsCount = 150,
                questions = listOf(
                    VoteQuestion(
                        "1",
                        "Question 1",
                        variants = listOf(
                            QuestionAnswerVariant(
                                "1",
                                "Yes",
                                100.0,
                            ),
                            QuestionAnswerVariant(
                                "2",
                                "No",
                                144.0,
                            ),
                            QuestionAnswerVariant(
                                "3",
                                "Abstain",
                                64.0,
                            ),
                        ),
                    ),
                    VoteQuestion(
                        "2",
                        "Question 2",
                        variants = listOf(
                            QuestionAnswerVariant(
                                "1",
                                "Yes",
                                100.0,
                            ),
                            QuestionAnswerVariant(
                                "2",
                                "No",
                                144.0,
                            ),
                            QuestionAnswerVariant(
                                "3",
                                "Abstain",
                                64.0,
                            ),
                        ),
                    ),
                ),
                endDate = System.currentTimeMillis() + 86400000
            )
            _isLoading.value = false
        }
    }

    fun vote(voteOptionId: Map<String, String>) {
        // TODO: implement me
    }
}