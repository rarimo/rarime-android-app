package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.api.voting.models.PollCriteria
import com.rarilabs.rarime.api.voting.models.PollCriteriaStatus
import com.rarilabs.rarime.api.voting.models.UserInPoll
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil.getDateMessage
import kotlinx.coroutines.launch

private enum class VotingStatus {
    LOADING, ALREADY_VOTED, ALLOWED, NOT_STARTED
}

@Composable
fun VoteProcessInfoScreen(
    modifier: Modifier = Modifier,
    userInPoll: UserInPoll,
    onClose: () -> Unit,
    onClick: () -> Unit,
    checkIsVoted: suspend () -> Boolean,
) {

    val isEligible = remember {
        userInPoll.userVerificationStatus == PollCriteriaStatus.VERIFIED && userInPoll.pollCriteriaList.none { !it.accomplished }
    }

    val context = LocalContext.current

    var voteState by remember {
        mutableStateOf(VotingStatus.LOADING)
    }

    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        scope.launch {
            if (!userInPoll.poll.isStarted) {
                voteState = VotingStatus.NOT_STARTED
                return@launch
            }
            if (!isEligible) {
                return@launch
            }
            val isVoted = checkIsVoted()
            voteState = if (isVoted) {
                VotingStatus.ALREADY_VOTED
            } else {
                VotingStatus.ALLOWED
            }
        }
    }

    Column(
        modifier = Modifier
            .then(modifier)
    ) {

        Box {
            BaseIconButton(
                modifier = Modifier
                    .padding(top = 20.dp, end = 20.dp)
                    .align(Alignment.TopEnd)
                    .zIndex(2f),
                onClick = onClose,
                icon = R.drawable.ic_close,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.componentPrimary,
                    contentColor = RarimeTheme.colors.textPrimary
                ),
            )

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1572.0f / 912.0f),
                //TODO FIX IT
                model = if (!userInPoll.poll.imageUrl.isNullOrEmpty()) "https://ipfs.rarimo.com/ipfs/" + userInPoll.poll.imageUrl else
                    "https://ipfs.rarimo.com/ipfs/QmQmC3XsggXEsYHFP6cB7k3BJjaJxg27kWoooRc6HRTRm5",
                contentDescription = null,
            )
        }


        Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 24.dp)) {
            Text(
                text = userInPoll.poll.title,
                color = RarimeTheme.colors.textPrimary,
                style = RarimeTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppIcon(id = R.drawable.ic_timer_line)
                    Text(
                        text = getDateMessage(poll = userInPoll.poll, context),
                        style = RarimeTheme.typography.subtitle7,
                        color = RarimeTheme.colors.textSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppIcon(id = R.drawable.ic_group_line)
                    Text(
                        text = userInPoll.poll.proposalResults[0].sum().toString(),
                        style = RarimeTheme.typography.subtitle7,
                        color = RarimeTheme.colors.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userInPoll.poll.description,
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userInPoll.poll.questionList.size.toString() + " questions",
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()

            Spacer(modifier = Modifier.height(24.dp))
            if (
                userInPoll.pollCriteriaList.isNotEmpty()
            ) {
                Text(
                    "Criteria",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    userInPoll.pollCriteriaList.map {
                        Row {
                            AppIcon(
                                id = if (it.accomplished) R.drawable.ic_checkbox_circle_fill else R.drawable.ic_close_circle_fill,
                                tint = if (it.accomplished) RarimeTheme.colors.secondaryMain else RarimeTheme.colors.errorMain
                            )
                            Text(it.title)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            when (voteState) {
                VotingStatus.LOADING ->
                    PrimaryButton(
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        text = if (isEligible) "Loading..." else "Not eligible",
                        onClick = onClick,
                        size = ButtonSize.Large
                    )

                VotingStatus.ALREADY_VOTED ->
                    PrimaryButton(
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        text = "Already voted",
                        onClick = onClick,
                        size = ButtonSize.Large
                    )

                VotingStatus.ALLOWED -> {
                    PrimaryButton(
                        enabled = isEligible,
                        modifier = Modifier.fillMaxWidth(),
                        text = if (isEligible) "Let’s start" else "Not eligible",
                        onClick = onClick,
                        size = ButtonSize.Large
                    )
                }

                VotingStatus.NOT_STARTED ->
                    PrimaryButton(
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        text = "Voting has not started",
                        onClick = onClick,
                        size = ButtonSize.Large
                    )
            }
        }
    }
}

@Preview
@Composable
private fun VoteProcessInfoScreenPreview() {
    Surface {
        VoteProcessInfoScreen(
            userInPoll = UserInPoll(
                poll = MOCKED_POLL_ITEM,
                userVerificationStatus = PollCriteriaStatus.VERIFIED,
                pollCriteriaList = listOf(
                    PollCriteria(
                        title = "More than 18 years",
                        accomplished = true
                    ),
                    PollCriteria(
                        title = "Citizen of Georgia",
                        accomplished = true
                    )
                )
            ),
            onClose = {},
            onClick = {},
            checkIsVoted = { false }
        )
    }

}