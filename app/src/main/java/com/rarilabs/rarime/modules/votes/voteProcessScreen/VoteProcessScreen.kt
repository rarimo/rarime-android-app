package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.api.voting.models.PollResult
import com.rarilabs.rarime.api.voting.models.Question
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Composable
fun VoteProcessScreen(
    modifier: Modifier = Modifier, selectedPoll: Poll, onBackClick: () -> Unit,
    onVote: (List<PollResult>) -> Unit
) {

    val selectedAnswers: MutableList<PollResult> = remember {
        mutableStateListOf()
    }

    var passedCount by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, end = 20.dp, start = 20.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Question: ${passedCount + 1}/${selectedPoll.questionList.size}")
            BaseIconButton(
                modifier = Modifier.zIndex(2f),
                onClick = { onBackClick.invoke() },
                icon = R.drawable.ic_close,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.componentPrimary,
                    contentColor = RarimeTheme.colors.textPrimary
                ),
            )
        }

        VoteProcessCard(
            voteOption = selectedPoll.questionList[passedCount],
            isLastOption = passedCount + 1 == selectedPoll.questionList.size
        ) {
            selectedAnswers.add(it)
            val currentQuestion = passedCount + 1
            if (currentQuestion >= selectedPoll.questionList.size) {
                onVote(selectedAnswers.toList())
            } else {
                passedCount++
            }
        }

    }
}

@Composable
fun VoteProcessCard(
    modifier: Modifier = Modifier,
    isLastOption: Boolean,
    voteOption: Question,
    onClick: (PollResult) -> Unit,

    ) {

    var selectedOption by remember {
        mutableStateOf<PollResult?>(null)
    }

    Column {
        Column(
            Modifier
                .background(
                    RarimeTheme.colors.backgroundSurface1, shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
                .then(modifier)
        ) {
            Text(
                voteOption.title,
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Select Answer",
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                voteOption.variants.forEachIndexed { idx, it ->

                    val isSelectedModifier = Modifier.border(
                        1.dp, RarimeTheme.colors.primaryMain, RoundedCornerShape(16.dp)
                    )


                    val isNotSelectedModifier =
                        Modifier.background(RarimeTheme.colors.componentPrimary)

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .then(if (selectedOption?.answerIndex == idx) isSelectedModifier else isNotSelectedModifier)
                            .fillMaxWidth()
                            .clickable {
                                selectedOption = PollResult(
                                    questionIndex = voteOption.id.toInt(), answerIndex = idx
                                )
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        if (selectedOption?.answerIndex == idx) {
                            AppIcon(id = R.drawable.ic_check, tint = RarimeTheme.colors.textPrimary)
                        } else {
                            Box(
                                modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (idx + 1).toString(),
                                    color = RarimeTheme.colors.textSecondary,
                                    style = RarimeTheme.typography.overline2,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        VerticalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .height(20.dp)
                        )
                        Text(it)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        ) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (isLastOption) "Confirm" else "Next",
                onClick = {
                    onClick.invoke(selectedOption!!)
                    selectedOption = null
                },
                size = ButtonSize.Large,
                rightIcon = if (isLastOption) null else R.drawable.ic_arrow_right,
                enabled = selectedOption != null
            )
        }
    }
}


@Preview
@Composable
private fun VoteProcessScreenPreview() {
    Surface {
        VoteProcessScreen(selectedPoll = MOCKED_POLL_ITEM, onBackClick = {}, onVote = {})
    }
}