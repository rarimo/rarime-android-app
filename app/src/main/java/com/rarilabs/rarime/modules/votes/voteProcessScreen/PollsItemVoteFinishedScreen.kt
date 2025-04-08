package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen

@Composable
fun PollsItemVoteFinishedScreen(
    navigate: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.75f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.ic_checkbox_circle_fill),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Poll finished",
                style = RarimeTheme.typography.h5,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Thank you for participating",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Home Page",
                onClick = { navigate(Screen.Main.Home.route) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PollsItemVoteFinishedScreenContentUnverifiedPreview() {
    PollsItemVoteFinishedScreen(
        navigate = {},
    )
}
