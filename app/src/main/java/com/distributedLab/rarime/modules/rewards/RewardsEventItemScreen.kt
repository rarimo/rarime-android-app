package com.distributedLab.rarime.modules.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.rewards.components.RewardAmountPreview
import com.distributedLab.rarime.modules.wallet.WalletRouteLayout
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RewardsEventItemScreen(
    onBack: () -> Unit,
) {
    WalletRouteLayout(
        headerModifier = Modifier
            .padding(horizontal = 20.dp),
        onBack = onBack,
        action = {
            AppIcon(id = R.drawable.ic_share, size = 18.dp)
        }
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth(0.75f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text (
                            text = "Initial setup of identity credentials",
                            style = RarimeTheme.typography.subtitle2,
                            color = RarimeTheme.colors.textPrimary,
                        )

                        Row (
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RewardAmountPreview()

                            Text(
                                text = "Exp: 24 sep, 2024, 10:00am",
                                style = RarimeTheme.typography.caption2,
                                color = RarimeTheme.colors.textSecondary,
                            )
                        }
                    }

                    Column (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            // painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
                            painter = painterResource(id = R.drawable.event_stub),
                            contentDescription = "Limited time event",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(64.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                            ,
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

                Column {
                    Text (
                        text = "lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  lorem ipsum dolor sit amet concestetur!  "
                    )
                }
            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                HorizontalDivider()

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                ) {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Let's start",
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardsEventItemScreenPreview() {
    RewardsEventItemScreen(onBack = {})
}