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
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun VoteResultsCard() {
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
                    text = "Usa election 2024",
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
                            text = "1 days",
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
                            text = "1 days",
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

            VoteResultsCardStatistics(
                options = listOf(
                    mapOf("Donald Trump" to 45.0),
                    mapOf("Joe Biden" to 35.0),
                    mapOf("Kanye West" to 20.0),
                )
            )
        }
    }
}

@Composable
fun VoteResultsCardStatistics(
    options: List<Map<String, Double>>,
) {
    val totalVotes = options.sumOf { it.values.first() }

    fun getPercentageOfOverallVotes(amount: Double): Double {
        val percentage = (amount.toFloat() / totalVotes.toFloat()) * 100.0

        return percentage
    }

    fun getIsLargestOption(amount: Number): Boolean {
        val largestOption = options.maxByOrNull { it.values.first() }

        return largestOption?.values?.first() == amount
    }

    Column (
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(16.dp))
    ) {
        options.forEach {
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

@Preview
@Composable
fun VoteResultsCardPreview() {
    VoteResultsCard()
}