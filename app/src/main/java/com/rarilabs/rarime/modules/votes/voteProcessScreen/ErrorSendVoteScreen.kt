package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.api.voting.VoteError
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.ErrorView
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.util.Screen


@Composable
fun ErrorSendVoteScreen(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    error: Exception?
) {

    val (errorTitle, errorDescription) = remember(error) {
        when (error) {
            is VoteError -> {
                when (error) {
                    is VoteError.NetworkError ->
                        "Network Error" to "Can't connect. Check your connection."

                    is VoteError.NotEnoughTokens ->
                        "Insufficient Tokens" to "Not enough tokens."

                    is VoteError.UnknownError ->
                        "Unexpected Error" to "Something went wrong. Try again."

                    is VoteError.ZKPError ->
                        "Validation Error" to "Validation failed. Try again."
                }
            }

            else ->
                "Error" to "Something went wrong. Try again."
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        ErrorView(
            title = errorTitle,
            subtitle = errorDescription
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            text = "Home Page",
            onClick = { navigate(Screen.Main.Home.route) })
        Spacer(modifier = Modifier.weight(0.1f))
    }

}


@Preview(showBackground = true)
@Composable
private fun ErrorSendVoteScreenPreview(modifier: Modifier = Modifier) {
    ErrorSendVoteScreen(error = Exception("exception"), navigate = {})
}