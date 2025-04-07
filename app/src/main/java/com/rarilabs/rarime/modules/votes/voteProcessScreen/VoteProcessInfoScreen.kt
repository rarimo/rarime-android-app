package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun VoteProcessInfoScreen(
    modifier: Modifier = Modifier,
    userInPoll: UserInPoll,
    onClose: () -> Unit,
    onClick: () -> Unit
) {

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
                modifier = Modifier.requiredHeight(140.dp),
                model = userInPoll.poll.imageUrl?.isNotEmpty()
                    ?: "https://ipfs.rarimo.com/ipfs/QmQmC3XsggXEsYHFP6cB7k3BJjaJxg27kWoooRc6HRTRm5",
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

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Let’s start",
                onClick = onClick,
                size = ButtonSize.Large
            )
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
                        accomplished = false
                    )
                )
            ),
            onClose = {}
        ) {}
    }

}