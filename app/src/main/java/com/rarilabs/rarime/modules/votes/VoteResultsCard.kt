package com.rarilabs.rarime.modules.votes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun VoteResultsCard(
    voteData: VoteData,
) {
    CardContainer(
        backgroundColor = RarimeTheme.colors.backgroundPrimary
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = voteData.title,
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppIcon(id = R.drawable.ic_timer_line)
                        Text(
                            text = "${voteData.durationMillis / (1000 * 60 * 60)} hours",
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
                            text = voteData.participantsCount.toString(),
                            style = RarimeTheme.typography.subtitle7,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .background(RarimeTheme.colors.componentPrimary)
                    .fillMaxWidth()
                    .height(1.dp)
            )

            voteData.questions.forEach { question ->
                VoteResultsCardStatistics(
                    variants = question.variants.map {
                        mapOf(
                            it.title to it.votedCount
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun VoteResultsCardStatistics(
    variants: List<Map<String, Double>>,
) {
    val totalVotes = variants.sumOf { it.values.first() }

    fun getPercentageOfOverallVotes(amount: Double): Double {
        val percentage = (amount.toFloat() / totalVotes.toFloat()) * 100.0

        return percentage
    }

    fun getIsLargestOption(amount: Number): Boolean {
        val largestOption = variants.maxByOrNull { it.values.first() }

        return largestOption?.values?.first() == amount
    }

    Column (
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(16.dp))
    ) {
        variants.forEach {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                val amount = it.values.first()
                val percentage = getPercentageOfOverallVotes(amount)
                val isWinner = getIsLargestOption(amount)

                val progressWidth = (percentage.toFloat() / 100f)

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressWidth)
                        .background(RarimeTheme.colors.successLight)
                )

                Row (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it.keys.first(),
                        color = RarimeTheme.colors.textPrimary,
                        style = if (isWinner) RarimeTheme.typography.overline1 else RarimeTheme.typography.body4
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "%.2f%%".format(percentage),
                            color = RarimeTheme.colors.textPrimary,
                            style = RarimeTheme.typography.subtitle6
                        )
                        Text(
                            text = amount.toString(),
                            color = RarimeTheme.colors.textSecondary,
                            style = RarimeTheme.typography.caption3
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VotesLoadingSkeleton() {
    CardContainer(
        backgroundColor = RarimeTheme.colors.backgroundPrimary
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title skeleton
                AppSkeleton(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(RarimeTheme.colors.componentDisabled)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duration skeleton
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Icon placeholder
                        AppSkeleton(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(RarimeTheme.colors.componentDisabled)
                        )
                        // Text placeholder
                        AppSkeleton(
                            modifier = Modifier
                                .height(16.dp)
                                .width(50.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(RarimeTheme.colors.componentDisabled)
                        )
                    }

                    // Participants skeleton
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Icon placeholder
                        AppSkeleton(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(RarimeTheme.colors.componentDisabled)
                        )
                        // Text placeholder
                        AppSkeleton(
                            modifier = Modifier
                                .height(16.dp)
                                .width(30.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(RarimeTheme.colors.componentDisabled)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .background(RarimeTheme.colors.componentPrimary)
                    .fillMaxWidth()
                    .height(1.dp)
            )

            // Statistics skeleton
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(16.dp))
            ) {
                // Create 3 skeleton option rows
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        // Progress bar background
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(index * 0.3f + 0.2f)
                                .background(RarimeTheme.colors.componentDisabled.copy(alpha = 0.3f))
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Option title placeholder
                            Box(
                                modifier = Modifier
                                    .height(16.dp)
                                    .width(80.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(RarimeTheme.colors.componentDisabled)
                            )

                            // Option values placeholder
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                AppSkeleton(
                                    modifier = Modifier
                                        .height(14.dp)
                                        .width(40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(RarimeTheme.colors.componentDisabled)
                                )
                                AppSkeleton(
                                    modifier = Modifier
                                        .height(12.dp)
                                        .width(30.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(RarimeTheme.colors.componentDisabled)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun VoteResultsCardPreview() {
    VoteResultsCard(
        voteData = VoteData(
            title = "Sample Vote",
            description = "This is a sample vote for preview purposes",
            durationMillis = 86400000,
            participantsCount = 150,
            questions = listOf(
                VoteQuestion(
                    "1",
                    "Question 1",
                    listOf(
                        QuestionAnswerVariant("1", "Lorem", 100.0),
                        QuestionAnswerVariant("2", "Ipsum", 200.0),
                        QuestionAnswerVariant("3", "Dolor", 300.0),
                        QuestionAnswerVariant("4", "Sit", 400.0),
                        QuestionAnswerVariant("5", "Amet", 500.0),
                        QuestionAnswerVariant("6", "Consectetur", 600.0),
                        QuestionAnswerVariant("7", "Adipiscing", 700.0),
                        QuestionAnswerVariant("8", "Elit", 800.0),
                        QuestionAnswerVariant("9", "Sed", 900.0),
                    ),
                ),
                VoteQuestion(
                    "2",
                    "Question 2",
                    listOf(
                        QuestionAnswerVariant("10", "Do", 1000.0),
                        QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                        QuestionAnswerVariant("12", "Tempor", 1200.0),
                        QuestionAnswerVariant("13", "Incididunt", 1300.0),
                        QuestionAnswerVariant("14", "Labore", 1400.0),
                        QuestionAnswerVariant("15", "Et", 1500.0),
                        QuestionAnswerVariant("16", "Dolore", 1600.0),
                        QuestionAnswerVariant("17", "Magna", 1700.0),
                        QuestionAnswerVariant("18", "Aliqua", 1800.0),
                        QuestionAnswerVariant("19", "Ut", 1900.0),
                        QuestionAnswerVariant("20", "Enim", 2000.0),
                    )
                ),
            ),
            endDate = (1741092332000).toLong()
        )
    )
}

@Preview
@Composable
fun VotesLoadingSkeletonPreview() {
    VotesLoadingSkeleton()
}