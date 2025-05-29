package com.rarilabs.rarime.modules.hiddenPrize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.CircledBadge
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme


data class AddScanProps(
    val idTitle: Int,
    val idDescription: Int,
    val color: Color,
    val idIcon: Int,
    val iconTitleColor: Color,
    val iconBackgroundTitleColor: Color
)

@Composable
fun AddScanBottomSheet(
    isInviteEnable: Boolean = true,
    isShareEnable: Boolean = true,
    onShare: () -> Unit,
    onInvite: () -> Unit
) {
    val enabledColor = RarimeTheme.colors.textPrimary
    val disabledColor = RarimeTheme.colors.textDisabled
    val enabledIconTitleColor = RarimeTheme.colors.hiddenPrizeAccent
    val enabledIconBackgroundTitleColor = RarimeTheme.colors.hiddenPrizeAccent.copy(alpha = 0.1f)//Todo change color
    val disabledIconTitleColor = RarimeTheme.colors.textSecondary
    val disabledIconBackgroundTitleColor = RarimeTheme.colors.componentPrimary
    val props = remember(isInviteEnable, isShareEnable) {
        if (isInviteEnable && isShareEnable) {
            AddScanProps(
                idIcon = R.drawable.ic_flashlight_fill,
                idTitle = R.string.add_scans_bottom_sheet_title_enebled,
                idDescription = R.string.hidden_prize_add_scans_description_enabled,
                color = enabledColor,
                iconTitleColor = enabledIconTitleColor,
                iconBackgroundTitleColor = enabledIconBackgroundTitleColor
            )
        } else {
            AddScanProps(//TODO delete this
                idIcon = R.drawable.ic_question,
                idTitle = R.string.add_scans_bottom_sheet_title_disablet,
                idDescription = R.string.hidden_prize_add_scans_description_disablet,
                color = disabledColor,
                iconTitleColor = disabledIconTitleColor,
                iconBackgroundTitleColor = disabledIconBackgroundTitleColor
            )
        }
    }
    Box(
        modifier = Modifier
    ) {
        Column {
            Box(
                modifier = Modifier.padding(
                    start = 24.dp, bottom = 20.dp
                )
            ) {
                CircledBadge(
                    iconId = props.idIcon,
                    containerSize = 56,
                    contentColor = props.iconTitleColor,
                    contentSize = 24,
                    containerColor = props.iconBackgroundTitleColor,
                )
            }
            Text(
                text = stringResource(props.idTitle),
                style = RarimeTheme.typography.h3,
                color = RarimeTheme.colors.textPrimary,
                modifier = Modifier.padding(
                    bottom = 8.dp, start = 24.dp
                )
            )
            Text(
                text = stringResource(props.idDescription),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.padding(
                    horizontal = 24.dp
                )
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = 24.dp, vertical = 32.dp
                )
            )
            RowAddScans(
                isEnabled = isShareEnable,
                props = props,
                rowTitle = stringResource(R.string.hidden_prize_title_share_row),
                rowDescription = stringResource(R.string.hidden_prize_share_row_description),
                modifier = Modifier,
                buttonLabel = stringResource(R.string.hidden_prize_share_row_button_label),
                idIcon = R.drawable.ic_share_line,
                onClick = onShare
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            RowAddScans(
                isEnabled = isInviteEnable,
                props = props,
                rowTitle = stringResource(R.string.hidden_prize_invite_row_title),
                rowDescription = stringResource(R.string.hidden_prize_invite_row_description),
                modifier = Modifier,
                buttonLabel = stringResource(R.string.hidden_prize_invite_row_button_label),
                idIcon = R.drawable.ic_user_add_line,
                onClick = onInvite
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun RowAddScans(
    isEnabled: Boolean = true,
    props: AddScanProps,
    idIcon: Int,
    rowTitle: String,
    rowDescription: String,
    buttonLabel: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.padding(end = 16.dp)
        ) {
            CircledBadge(
                modifier = Modifier,
                iconId = idIcon,
                containerSize = 40,
                contentColor = props.color,
                contentSize = 20,
                containerColor = RarimeTheme.colors.componentPrimary
            )
        }

        Column {
            Text(
                text = rowTitle, color = props.color
            )
            Text(
                text = rowDescription, color = props.color
            )
        }
        Spacer(Modifier.weight(weight = 1.0f))

        PrimaryButton(
            onClick = onClick,
            enabled = isEnabled,
            modifier = Modifier,
            text = buttonLabel,
            size = ButtonSize.Small
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddScansBottomSheetEnabledPreview() {
    Box(modifier = Modifier) {
        AddScanBottomSheet(onShare = {}, onInvite = {})
    }

}

@Preview(showBackground = true)
@Composable
fun AddScansBottomSheetDisabledPreview() {
    Box(modifier = Modifier) {
        AddScanBottomSheet(
            isInviteEnable = false,
            isShareEnable = false,
            onShare = {},
            onInvite = {})
    }
}

