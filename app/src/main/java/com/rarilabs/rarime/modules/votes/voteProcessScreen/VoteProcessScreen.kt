package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VoteProcessScreen(
    voteId: String,
    viewModel: VoteProcessScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {

}