package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.api.voting.models.PollCriteria
import com.rarilabs.rarime.api.voting.models.PollCriteriaStatus
import com.rarilabs.rarime.api.voting.models.UserInPoll
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun VoteProcessInfoScreen(
    modifier: Modifier = Modifier,
    userInPoll: UserInPoll,
    onClose: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {

        BaseIconButton(
            modifier = Modifier.align(Alignment.End),
            onClick = onClose,
            icon = R.drawable.ic_close,
            colors = ButtonDefaults.buttonColors(
                containerColor = RarimeTheme.colors.componentPrimary,
                contentColor = RarimeTheme.colors.textPrimary
            ),
        )

        AsyncImage(
            modifier = Modifier.height(300.dp),
            model = userInPoll.poll.imageUrl?.isNotEmpty()
                ?: "https://ipfs.rarimo.com/ipfs/QmQmC3XsggXEsYHFP6cB7k3BJjaJxg27kWoooRc6HRTRm5",
            contentDescription = null,
        )

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
                    text = "${userInPoll.poll.voteEndDate!! / (1000 * 60 * 60)} hours",
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
                    text = "participantsCount",
                    style = RarimeTheme.typography.subtitle7,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }

        Text(text = userInPoll.poll.description)
        Text(text = userInPoll.poll.questionList.size.toString() + " questions")
        HorizontalDivider()
        Text("Criteria")
        userInPoll.pollCriteriaList.map {
            Row {
                AppIcon(
                    id = if (it.accomplished) R.drawable.ic_checkbox_circle_fill else R.drawable.ic_close_circle_fill,
                    tint = if (it.accomplished) RarimeTheme.colors.secondaryMain else RarimeTheme.colors.errorMain
                )
                Text(it.title)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(modifier = Modifier.fillMaxWidth(), text = "sad", onClick = {})
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
                        accomplished = false
                    )
                )
            ),
            onClose = {}
        )
    }

}