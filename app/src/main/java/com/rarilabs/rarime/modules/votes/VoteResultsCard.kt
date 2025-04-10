package com.rarilabs.rarime.modules.votes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.HorizontalPageIndicator
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil.getDateMessage


@Composable
fun VoteResultsCard(
    voteData: Poll, onCLick: (Poll) -> Unit
) {
    val context = LocalContext.current

    val pageState = rememberPagerState { voteData.proposalResults.size }

    CardContainer(
        backgroundColor = RarimeTheme.colors.backgroundPrimary,
        modifier = Modifier.clickable { onCLick.invoke(voteData) }) {
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
                            text = getDateMessage(voteData, context),
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
                            text = voteData.proposalResults[0].sum().toString(),
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                HorizontalPager(state = pageState, pageSpacing = 12.dp) {
                    VoteResultsCardStatistics(
                        variants = voteData.questionList[0].variants.mapIndexed { index, it ->
                            mapOf(
                                it to voteData.proposalResults[pageState.currentPage][index].toDouble()
                            )
                        })
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalPageIndicator(
                    numberOfPages = pageState.pageCount,
                    selectedPage = pageState.currentPage,
                    selectedColor = RarimeTheme.colors.primaryMain,
                    defaultRadius = 6.dp,
                    selectedLength = 16.dp,
                    space = 8.dp
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

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(16.dp))
    ) {
        variants.forEach {
            Box(
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

                Row(
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
                            text = if (percentage > 0.0) "%.2f%%".format(percentage) else "0.0%",
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
        voteData = MOCKED_POLL_ITEM
    ) {}
}

@Preview
@Composable
fun VotesLoadingSkeletonPreview() {
    VotesLoadingSkeleton()
}