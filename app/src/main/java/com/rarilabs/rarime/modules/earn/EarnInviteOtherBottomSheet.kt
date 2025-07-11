package com.rarilabs.rarime.modules.earn

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsBalanceDataAttributes
import com.rarilabs.rarime.api.points.models.ReferralCode
import com.rarilabs.rarime.api.points.models.ReferralCodeStatuses
import com.rarilabs.rarime.modules.earn.components.RewardsEventItemInvitesCard
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme


val tempPointsBalances = PointsBalanceData(
    id = "12",
    type = "12",

    attributes = PointsBalanceDataAttributes(
        amount = 0,
        is_disabled = false,
        is_verified = true,
        created_at = 0,
        updated_at = 0,
        rank = 0,
        referral_codes = listOf(
            ReferralCode(
                id = "QrisPfszkps_1", status = ReferralCodeStatuses.ACTIVE.value
            ),
            ReferralCode(
                id = "QrisPfszkps_2",
                status = ReferralCodeStatuses.ACTIVE.value,
            ),
            ReferralCode(
                id = "QrisPfszkps_3", status = ReferralCodeStatuses.ACTIVE.value
            ),
            ReferralCode(
                id = "QrisPfszkps_4", status = ReferralCodeStatuses.ACTIVE.value
            ),
            ReferralCode(
                id = "QrisPfszkps_5", status = ReferralCodeStatuses.ACTIVE.value
            ),
//                    ReferralCode(
//                        id = "QrisPfszkps_6",
//                        status = ReferralCodeStatuses.AWAITING.value
//                    )
        ),
        level = 1,
    ),
)


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun InviteOthersContent(
    modifier: Modifier = Modifier,
    isVerifiedPointsBalance: Boolean,
    onClose: () -> Unit,
    referralCodes: List<ReferralCode>
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .then(modifier)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.earn_widget_invite_bottom_sheet_title),
                style = RarimeTheme.typography.h2,
                color = RarimeTheme.colors.textPrimary,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(top = 30.dp, bottom = 12.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .padding(end = 20.dp, top = 24.dp, bottom = 24.dp)
                    .size(24.dp)
            ) {
                AppIcon(
                    id = R.drawable.ic_close_fill,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }
        }
        Text(
            text = stringResource(R.string.earn_invite_bottom_sheet_description),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary

        )



        Column(
            modifier = Modifier.padding(vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            referralCodes.forEach {
                RewardsEventItemInvitesCard(
                    code = it,
                    isVerifiedPointsBalance = isVerifiedPointsBalance,
                )
            }
        }

    }
}