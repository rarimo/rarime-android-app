package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.votes.VoteResultsCard
import com.rarilabs.rarime.modules.votes.VoteResultsCardStatistics
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun VoteProcessScreen(
    voteId: String,
    viewModel: VoteProcessScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val mainViewModel = LocalMainViewModel.current
    val screenInsets by mainViewModel.screenInsets.collectAsState()

    LaunchedEffect(voteId) {
        viewModel.loadVoteData(voteId)
    }

    val voteData by viewModel.voteData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    VoteProcessScreenContent(
        screenInsets = mapOf(
            ScreenInsets.TOP to screenInsets.get(ScreenInsets.TOP),
            ScreenInsets.BOTTOM to screenInsets.get(ScreenInsets.BOTTOM)
        ),
        voteData = voteData,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onVote = { optionId -> optionId?.let { viewModel.vote(it) } }
    )
}

@Composable
fun VoteProcessScreenContent(
    screenInsets: Map<ScreenInsets, Number?>,
    voteData: VoteData? = null,
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onVote: (String?) -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    val isVoteEnded = voteData?.endDate?.let { it < System.currentTimeMillis() } ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .absolutePadding(
                top = (screenInsets.get(ScreenInsets.TOP)?.toFloat() ?: 0f).dp,
                bottom = (screenInsets.get(ScreenInsets.BOTTOM)?.toFloat() ?: 0f).dp,
                left = 20.dp,
                right = 20.dp,
            )
    ) {
        // Back button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) {
                    onBackClick()
                }
                .padding(8.dp)
        ) {
            AppIcon(
                id = R.drawable.ic_arrow_left,
                tint = RarimeTheme.colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RarimeTheme.colors.textPrimary)
            }
        } else if (voteData != null) {
            // Title
            Text(
                text = voteData.title,
                style = RarimeTheme.typography.h3,
                color = RarimeTheme.colors.textPrimary
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

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = voteData.description,
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            if (isVoteEnded) {
                Column (
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    VoteResultsCardStatistics(
                        options = voteData.options
                            .sortedByDescending { it.votedCount }
                            .map { option -> mapOf(option.title to option.votedCount)
                        }
                    )
                }
            } else {
                // Options
                Text(
                    text = "Select Answer",
                    style = RarimeTheme.typography.subtitle5,
                    color = RarimeTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    voteData.options.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true)
                                ) { selectedOption = option.id }
                                .background(
                                    if (selectedOption === option.id) Color.Transparent
                                    else RarimeTheme.colors.componentPrimary
                                )
                                .border(
                                    1.dp,
                                    if (selectedOption === option.id) RarimeTheme.colors.textPrimary
                                    else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 16.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (selectedOption === option.id) {
                                AppIcon(
                                    id = R.drawable.ic_check,
                                    tint = RarimeTheme.colors.textPrimary
                                )
                            } else {
                                Text(
                                    text = (index + 1).toString(),
                                    style = RarimeTheme.typography.buttonMedium,
                                    color = RarimeTheme.colors.textPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }

                            VerticalDivider()

                            Text(
                                text = option.title,
                                style = RarimeTheme.typography.buttonMedium,
                                color = RarimeTheme.colors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Submit",
                    size = ButtonSize.Large,
                    onClick = {
                        onVote(selectedOption)
                    }
                )
            }
        } else {
            // Error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Failed to load vote data",
                    style = RarimeTheme.typography.body1,
                    color = RarimeTheme.colors.errorMain
                )
            }
        }
    }
}

@Preview
@Composable
fun VoteProcessScreenContentPreview() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        VoteProcessScreenContent(
            screenInsets = mapOf(
                ScreenInsets.TOP to 12,
                ScreenInsets.BOTTOM to 12
            ),
            voteData = VoteData(
                title = "Sample Vote",
                description = "This is a sample vote for preview purposes",
                durationMillis = 86400000,
                participantsCount = 150,
                options = listOf(
                    VoteOption("1", "Lorem", 100.0),
                    VoteOption("2", "Ipsum", 200.0),
                    VoteOption("3", "Dolor", 300.0),
                    VoteOption("4", "Sit", 400.0),
                    VoteOption("5", "Amet", 500.0),
                    VoteOption("6", "Consectetur", 600.0),
                    VoteOption("7", "Adipiscing", 700.0),
                    VoteOption("8", "Elit", 800.0),
                    VoteOption("9", "Sed", 900.0),
                    VoteOption("10", "Do", 1000.0),
                    VoteOption("11", "Eiusmod", 1100.0),
                    VoteOption("12", "Tempor", 1200.0),
                    VoteOption("13", "Incididunt", 1300.0),
                    VoteOption("14", "Labore", 1400.0),
                    VoteOption("15", "Et", 1500.0),
                    VoteOption("16", "Dolore", 1600.0),
                    VoteOption("17", "Magna", 1700.0),
                    VoteOption("18", "Aliqua", 1800.0),
                    VoteOption("19", "Ut", 1900.0),
                    VoteOption("20", "Enim", 2000.0),
                ),
                endDate = (1741092332000).toLong()
            )
        )
    }
}