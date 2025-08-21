package com.rarilabs.rarime.modules.votes

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.api.voting.models.MOCKED_RANKING_BASED_VOTE_ITEM
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.HorizontalPageIndicator
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil.convertToDate

private enum class PollType {
    CHOICE_BASED,
    RANKING_BASED,
}

@Composable
fun VoteResultsCard(
    voteData: Poll, onCLick: (Poll) -> Unit
) {

    val pageState = rememberPagerState { voteData.proposalResults.size }
    val pollType = if (voteData.isRankingBased) PollType.RANKING_BASED else PollType.CHOICE_BASED

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(width = 1.dp, color = RarimeTheme.colors.textSecondary)

    ) {
        CardContainer(
            backgroundColor = RarimeTheme.colors.backgroundSurface1,
            modifier = Modifier

                .clickable { onCLick.invoke(voteData) }

        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box {
                    AsyncImage(
                        model = "https://ipfs.rarimo.com/ipfs/" + voteData.imageUrl,
                        contentDescription = null,
                        clipToBounds = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.FillBounds


                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = voteData.title,
                            style = RarimeTheme.typography.h5,
                            color = RarimeTheme.colors.baseWhite
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppIcon(
                                    id = R.drawable.ic_calendar_line,
                                    tint = RarimeTheme.colors.textSecondary
                                )
                                Text(
                                    text = convertToDate(
                                        value = voteData.voteStartDate,
                                        pattern = "dd MMM"
                                    ) + " - " + convertToDate(
                                        value = voteData.voteEndDate,
                                        pattern = "dd MMM"
                                    ),
                                    style = RarimeTheme.typography.subtitle7,
                                    color = RarimeTheme.colors.textSecondary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppIcon(
                                    id = R.drawable.ic_group_line,
                                    tint = RarimeTheme.colors.textSecondary
                                )
                                Text(
                                    text = voteData.proposalResults[0].sum().toString(),
                                    style = RarimeTheme.typography.subtitle7,
                                    color = RarimeTheme.colors.textSecondary
                                )
                            }
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
                    when (pollType) {
                        PollType.CHOICE_BASED -> {
                            HorizontalPager(state = pageState, pageSpacing = 12.dp) {
                                Column {
                                    Text(
                                        text = voteData.questionList[pageState.currentPage].title,
                                        style = RarimeTheme.typography.body4,
                                        color = RarimeTheme.colors.textPrimary,
                                        modifier = Modifier.padding(vertical = 16.dp)

                                    )
                                    OptionBasedVoteResultsCardStatistics(
                                        variants = voteData.questionList[pageState.currentPage].variants.mapIndexed { index, it ->
                                            mapOf(
                                                it to voteData.proposalResults[pageState.currentPage][index].toDouble()
                                            )
                                        })
                                }
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

                        PollType.RANKING_BASED -> {

                            Column {
                                Text(
                                    text = voteData.questionList[0].title,
                                    style = RarimeTheme.typography.body4,
                                    color = RarimeTheme.colors.textPrimary,
                                    modifier = Modifier.padding(vertical = 16.dp)

                                )
                                RankingBasedVoteResultsCardStatistics(
                                    variants = voteData.questionList.mapIndexed { questionIndex, question ->
                                        question.variants.mapIndexed { variantIndex, variant ->
                                            mapOf(variant to voteData.proposalResults[questionIndex][variantIndex].toDouble())
                                        }
                                    },
                                    pageState = pageState
                                )

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
        }
    }
}

@Composable
fun RankingBasedVoteResultsCardStatistics(
    variants: List<List<Map<String, Double>>>,
    pageState: PagerState
) {
    val candidateScores = remember(variants) {
        val scores = mutableMapOf<String, Double>()
        val totalRanks = variants.size

        variants.forEachIndexed { rankIndex, rankVotes ->
            val scoreForRank = (totalRanks - rankIndex).toDouble()

            rankVotes.forEach { voteMap ->
                val (candidateName, count) = voteMap.entries.first()
                val candidateScore = count * scoreForRank

                scores[candidateName] = scores.getOrDefault(candidateName, 0.0) + candidateScore
            }
        }
        scores
    }

    val sortedResults = candidateScores.entries.sortedByDescending { it.value }
    val totalScore = sortedResults.sumOf { it.value }

    HorizontalPager(
        state = pageState,
        pageSpacing = 12.dp,
    ) { page ->
        val currentResult = sortedResults[page]

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                val percentage = (currentResult.value / totalScore) * 100.0
                val progressWidth = (percentage / 100f).toFloat()
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
                        text = currentResult.key,
                        color = RarimeTheme.colors.textPrimary,
                        style = RarimeTheme.typography.overline1
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${percentage.toInt()}%",
                            color = RarimeTheme.colors.textPrimary,
                            style = RarimeTheme.typography.subtitle6
                        )
                        Text(
                            text = currentResult.value.toInt().toString(),
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
fun OptionBasedVoteResultsCardStatistics(
    variants: List<Map<String, Double>>,
) {
    val totalVotes = variants.sumOf { it.values.first() }

    fun getPercentageOfOverallVotes(amount: Double): Double {
        val percentage = (amount.toFloat() / totalVotes.toFloat()) * 100.0

        return percentage
    }

    val largestOption = variants.maxBy { it.values.first() }


    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(16.dp))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {

            val percentage = getPercentageOfOverallVotes(largestOption.values.first())

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
                    text = largestOption.keys.first(),
                    color = RarimeTheme.colors.textPrimary,
                    style = RarimeTheme.typography.overline1
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = percentage.toInt().toString() + "%",
                        color = RarimeTheme.colors.textPrimary,
                        style = RarimeTheme.typography.subtitle6
                    )
                    Text(
                        text = largestOption.values.first().toInt().toString(),
                        color = RarimeTheme.colors.textSecondary,
                        style = RarimeTheme.typography.caption3
                    )
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
                                .padding(top = 4.dp)
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    // Progress bar background
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.6f)
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


@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED)
@Composable
fun VoteResultsChoseBasedCardPreview() {
    AppTheme {
        VoteResultsCard(
            voteData = MOCKED_POLL_ITEM
        ) {}
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED)
@Composable
fun VoteResultsRankedBasedCardPreview() {
    AppTheme {
        VoteResultsCard(
            voteData = MOCKED_RANKING_BASED_VOTE_ITEM
        ) {}
    }
}


@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED)
@Composable
fun VotesLoadingSkeletonPreview() {
    VotesLoadingSkeleton()
}