package com.distributedLab.rarime.modules.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSkeleton
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RewardsScreen() {
    Box {
        RewardsSkeleton()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPure.copy(alpha = 0.3f))
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 220.dp)
                .padding(horizontal = 24.dp)
        ) {
            AppIcon(
                id = R.drawable.ic_calendar_blank,
                size = 24.dp,
                tint = RarimeTheme.colors.primaryMain,
                modifier = Modifier
                    .background(RarimeTheme.colors.baseBlack, CircleShape)
                    .padding(24.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.rewards_stub_title),
                    style = RarimeTheme.typography.h5,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.rewards_stub_description),
                    textAlign = TextAlign.Center,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun RewardsSkeleton() {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 20.dp)
            .padding(horizontal = 12.dp)
            .blur(6.dp)
    ) {
        AppSkeleton(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp)
        )
        for (i in 0..4) {
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    AppSkeleton(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppSkeleton(
                            modifier = Modifier
                                .width(140.dp)
                                .height(30.dp)
                        )
                        AppSkeleton(
                            modifier = Modifier
                                .width(60.dp)
                                .height(20.dp)
                        )
                    }
                    AppSkeleton(
                        modifier = Modifier
                            .width(200.dp)
                            .height(12.dp)
                    )
                    AppSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RewardsScreenPreview() {
    RewardsScreen()
}
