package com.distributedLab.rarime.modules.rewards

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.points.models.BaseEvents
import com.distributedLab.rarime.api.points.models.PointsEventData
import com.distributedLab.rarime.api.points.models.ReferralCode
import com.distributedLab.rarime.api.points.models.ReferralCodeStatuses
import com.distributedLab.rarime.modules.rewards.components.RewardAmountPreview
import com.distributedLab.rarime.modules.rewards.components.RewardsEventItemInvitesCard
import com.distributedLab.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.distributedLab.rarime.modules.rewards.view_models.RewardsEventItemViewModel
import com.distributedLab.rarime.modules.wallet.WalletRouteLayout
import com.distributedLab.rarime.ui.components.ActionCard
import com.distributedLab.rarime.ui.components.ActionCardVariants
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSkeleton
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.DateUtil
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun RewardsEventItemScreen(
    onBack: () -> Unit,
    rewardsEventItemViewModel: RewardsEventItemViewModel = hiltViewModel(),
) {
    val uriHandler = LocalUriHandler.current

    val _pointsEvent = rewardsEventItemViewModel.pointsEventData.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                rewardsEventItemViewModel.loadPointsEvent()
            } catch (e: Exception) {
                Log.e("RewardsEventItemScreen", "Error loading points event", e)
            }
        }
    }

    _pointsEvent.value?.let { pointsEvent ->
        when (pointsEvent.attributes.meta.static.name) {
            BaseEvents.REFERRAL_COMMON.value -> {
                RewardsEventItemInvites(pointsEvent = pointsEvent, onBack = onBack)
            }
            else -> {
                RewardsEventItemCommon(pointsEvent = pointsEvent, onBack = onBack)
            }
        }
    } ?: PageSkeleton()
}

@Composable
private fun RewardsEventItemInvites(
    onBack: () -> Unit,
    pointsEvent: PointsEventData,
) {
    WalletRouteLayout(
        headerModifier = Modifier
            .padding(horizontal = 20.dp),
        onBack = onBack,
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Invite Friends",
                    style = RarimeTheme.typography.h4,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Short description text here",
                    style = RarimeTheme.typography.body2,
                    color = RarimeTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            CardContainer(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Invited 4/5",
                        style = RarimeTheme.typography.subtitle3
                    )
                    Text(
                        text = "Short description text here",
                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Column {
                        RewardsEventItemInvitesCard(
                            code = ReferralCode(
                                id = "QrisPfszkps",
                                status = ReferralCodeStatuses.REWARDED.value
                            )
                        )

                        RewardsEventItemInvitesCard(
                            code = ReferralCode(
                                id = "QrisPfszkps",
                                status = ReferralCodeStatuses.CONSUMED.value
                            )
                        )
                        RewardsEventItemInvitesCard(
                            code = ReferralCode(
                                id = "QrisPfszkps",
                                status = ReferralCodeStatuses.ACTIVE.value
                            )
                        )

                        RewardsEventItemInvitesCard(
                            code = ReferralCode(
                                id = "QrisPfszkps",
                                status = ReferralCodeStatuses.BANNED.value
                            )
                        )
                        RewardsEventItemInvitesCard(
                            code = ReferralCode(
                                id = "QrisPfszkps",
                                status = ReferralCodeStatuses.LIMITED.value
                            )
                        )

                        RewardsEventItemInvitesCard(
                            code = ReferralCode(
                                id = "QrisPfszkps",
                                status = ReferralCodeStatuses.AWAITING.value
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardsEventItemCommon(
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

@Composable
private fun PageSkeleton() {
    WalletRouteLayout(
        headerModifier = Modifier
            .padding(horizontal = 20.dp),
        onBack = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppSkeleton(modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(32.dp))
            AppSkeleton(modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(16.dp))

            HorizontalDivider()

            AppSkeleton(modifier = Modifier.fillMaxWidth().weight(1f))

            repeat(7) {
                AppSkeleton(
                    modifier = Modifier
                        .fillMaxWidth(Random.nextFloat() * (1f - 0.75f) + 0.75f)
                        .height((Random.nextFloat() * (48 - 18) + 18).dp)
                )
            }

            AppSkeleton(modifier = Modifier.fillMaxWidth().height(52.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PageSkeletonPreview() {
    PageSkeleton()
}

@Preview(showBackground = true)
@Composable
private fun RewardsEventItemInvitesPreview() {
    RewardsEventItemInvites(
        onBack = {},
        pointsEvent = CONST_MOCKED_EVENTS_LIST[0]
    )
}

@Preview
@Composable
fun RewardsEventItemScreenPreview() {
    RewardsEventItemScreen(onBack = {})
}