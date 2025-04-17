package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun VoteLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(80.dp))
    }
}


@Preview
@Composable
private fun VoteLoadingScreenPreview() {
    Surface {
        VoteLoadingScreen(Modifier.fillMaxSize())
    }
}