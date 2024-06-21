package com.rarilabs.rarime.modules.rewards.event_item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.modules.rewards.components.RewardAmountPreview
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.modules.wallet.WalletRouteLayout
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun RewardsEventItemCommon(
    pointsEvent: PointsEventData,
    onBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    WalletRouteLayout(
        headerModifier = Modifier
            .padding(horizontal = 20.dp),
        onBack = onBack,
        action = {
            // TODO: implement
            AppIcon(id = R.drawable.ic_share, size = 18.dp, tint = RarimeTheme.colors.textPrimary)
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
                    .weight(1f)
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
                            text = pointsEvent.attributes.meta.static.title,
                            style = RarimeTheme.typography.subtitle2,
                            color = RarimeTheme.colors.textPrimary,
                        )

                        Row (
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RewardAmountPreview(amount = pointsEvent.attributes.meta.static.reward)

                            pointsEvent.attributes.meta.static.expiresAt?.let {
                                Text(
                                    text = DateUtil.formatDateString(it),
                                    style = RarimeTheme.typography.caption2,
                                    color = RarimeTheme.colors.textSecondary,
                                )
                            }
                        }
                    }

                    Column (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = pointsEvent.attributes.meta.static.logo?.let {
                                rememberAsyncImagePainter(it)
                                // TODO: change event_stub
                            } ?: painterResource(id = R.drawable.event_stub),
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

                Column (
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    MarkdownText(
                        markdown = pointsEvent.attributes.meta.static.description
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
                        onClick = {
                            pointsEvent.attributes.meta.static.actionUrl?.let { uriHandler.openUri(it) }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsEventItemCommonPreview() {
    RewardsEventItemCommon(
        pointsEvent = CONST_MOCKED_EVENTS_LIST[0],
        onBack = {},
    )
}