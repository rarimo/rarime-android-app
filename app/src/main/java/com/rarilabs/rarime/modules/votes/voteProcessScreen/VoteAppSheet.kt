package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.api.voting.models.PollResult
import com.rarilabs.rarime.api.voting.models.UserInPoll
import com.rarilabs.rarime.manager.VoteError
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.launch

private enum class VoteAppSheetState {
    LOADING_VOTE,
    INFO_VOTE,
    SELECT_OPTION_VOTE,
    RANKING_BASED_VOTE,
    PROCESSING_VOTE,
    ERROR_VOTE,
    FINISH_VOTE
}

@Composable
fun VotingAppSheet(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    voteSheetState: AppSheetState,
    selectedPoll: UserInPoll?,
    viewModel: VoteAppSheetViewModel = hiltViewModel(),
) {

    var currentState by remember {
        mutableStateOf(if (selectedPoll == null) VoteAppSheetState.LOADING_VOTE else VoteAppSheetState.INFO_VOTE)
    }

    val currentSchema by viewModel.currentSchema.collectAsState()

    LaunchedEffect(selectedPoll) {
        if (selectedPoll != null && currentState == VoteAppSheetState.LOADING_VOTE) {
            currentState = VoteAppSheetState.INFO_VOTE
        }
    }

    LaunchedEffect(voteSheetState.showSheet) {
        if (!voteSheetState.showSheet) {
            viewModel.setSelectedPoll(null)
        }
    }


    val context = LocalContext.current

    var error by remember {
        mutableStateOf<VoteError?>(null)
    }

    val scope = rememberCoroutineScope()

    fun vote(voteOption: List<PollResult>) {
        currentState = VoteAppSheetState.PROCESSING_VOTE
        scope.launch {
            try {
                viewModel.vote(voteOption, context)

                currentState = VoteAppSheetState.FINISH_VOTE
                return@launch
            } catch (e: VoteError) {
                ErrorHandler.logError("Voting", e.message.toString(), e)
                error = e
            } catch (e: Exception) {
                ErrorHandler.logError("Voting", e.message.toString(), e)
                error = VoteError.UnknownError(e.message.toString())
            }

            currentState = VoteAppSheetState.ERROR_VOTE
        }
    }

    AppBottomSheet(
        state = voteSheetState,
        isHeaderEnabled = false,
        scrimColor = Color.Transparent,
        fullScreen = true,
    ) {

        when (currentState) {

            VoteAppSheetState.LOADING_VOTE -> {
                VoteLoadingScreen(Modifier.fillMaxSize())
            }

            VoteAppSheetState.INFO_VOTE -> {
                VoteProcessInfoScreen(
                    userInPoll = selectedPoll!!,
                    onClose = {
                        voteSheetState.hide()
                        viewModel.setSelectedPoll(null)
                    },
                    onClick = {
                        currentState = if (selectedPoll.poll.isRankingBased)
                            VoteAppSheetState.RANKING_BASED_VOTE
                        else
                            VoteAppSheetState.SELECT_OPTION_VOTE
                    },
                    checkIsVoted = viewModel.checkIsVoted,
                    colorMode = currentSchema
                )
            }

            VoteAppSheetState.SELECT_OPTION_VOTE -> {
                VoteProcessScreen(
                    selectedPoll = selectedPoll!!.poll,

                    onBackClick = {
                        currentState = VoteAppSheetState.INFO_VOTE
                    },
                    onVote = {
                        scope.launch {
                            currentState = VoteAppSheetState.PROCESSING_VOTE
                            vote(it)
                        }
                    }
                )
            }

            VoteAppSheetState.RANKING_BASED_VOTE -> {
                VoteRankingBasedScreen(
                    selectedPoll = selectedPoll!!.poll,

                    onBackClick = {
                        currentState = VoteAppSheetState.INFO_VOTE
                    },
                    onClick = {
                        scope.launch {
                            currentState = VoteAppSheetState.PROCESSING_VOTE
                            vote(it)
                        }
                    }
                )
            }

            VoteAppSheetState.PROCESSING_VOTE -> {
                SendingVoteScreen()
            }

            VoteAppSheetState.ERROR_VOTE -> {
                ErrorSendVoteScreen(
                    navigate = navigate,
                    error = error,
                )
            }

            VoteAppSheetState.FINISH_VOTE -> {
                PollsItemVoteFinishedScreen {
                    voteSheetState.hide()
                }
            }
        }
    }
}
