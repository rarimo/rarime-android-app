package com.rarilabs.rarime.modules.hiddenPrize

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CircledBadge
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme


data class ADDSCANPROPS(
    val idTitle: Int,
    val idDescription: Int,
    val color:Color,
    val idIcon: Int,
    val iconTitleColor:Color,
    val iconBackgroundTitleColor:Color
)


@Composable
fun AddScanBottomSheet(
    modifier: Modifier = Modifier,
    isInviteEnable: Boolean = true,
    isShareEnable: Boolean = true
) {
    val enabledColor = RarimeTheme.colors.textPrimary
    val disabletColor = RarimeTheme.colors.textDisabled
    val enabletIconTitleColor = Color(0xFF9D4EDD)  //TODO: sync with theme
    val enabledIconBackgroundTitleColor = Color(0xFFF5EDFC) //TODO: sync with theme
    val disabledIconTitleColor = RarimeTheme.colors.textSecondary
    val disabledIconBackgroundTitleColor = RarimeTheme.colors.componentPrimary

    val props = remember(isInviteEnable, isShareEnable) {
        if(isInviteEnable && isShareEnable){
                ADDSCANPROPS(
                    idIcon = R.drawable.ic_flashlight_fill,
                    idTitle = R.string.add_scans_bottom_sheet_title_enebled,
                    idDescription = R.string.hidden_prize_add_scans_description_enabled,
                    color = enabledColor,
                    iconTitleColor = enabletIconTitleColor,
                    iconBackgroundTitleColor = enabledIconBackgroundTitleColor
                )
        }
        else {
                ADDSCANPROPS(
                    idIcon = R.drawable.ic_question,
                    idTitle = R.string.add_scans_bottom_sheet_title_disablet,
                    idDescription = R.string.hidden_prize_add_scans_description_disablet,
                    color = disabletColor,
                    iconTitleColor = disabledIconTitleColor,
                    iconBackgroundTitleColor = disabledIconBackgroundTitleColor
                )




        }
    }


    Column {
            CircledBadge(
                modifier = Modifier,
                iconId = props.idIcon,
                containerSize = 56,
                contentColor = props.iconTitleColor,
                contentSize = 24,
                containerColor = props.iconBackgroundTitleColor
            )



        Text(
            text = stringResource(props.idTitle),
            style = RarimeTheme.typography.h3,
            color = RarimeTheme.colors.textPrimary
        )
        Text(
            text = stringResource(props.idDescription),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary
        )
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircledBadge(
                modifier = Modifier,
                iconId = R.drawable.ic_share,
                containerSize = 40,
                contentColor = props.color,
                contentSize = 20,
                containerColor = RarimeTheme.colors.componentPrimary
            )




            Column {
                Text("Share on socials")
                Text("+1 scan")
            }



            PrimaryButton(
                onClick = {},
                enabled = isInviteEnable,
                text = "Share",
                size = ButtonSize.Small
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircledBadge(
                modifier = Modifier,
                iconId = R.drawable.ic_user_plus,
                containerSize = 40,
                contentColor = props.color,
                contentSize = 20,
                containerColor = RarimeTheme.colors.componentPrimary
            )



            Column {
                Text("Invite a friend")
                Text("+1 scan")
            }



            PrimaryButton(
                onClick = {},
                enabled = isShareEnable,
                modifier = Modifier,
                text = "Invite",
                size = ButtonSize.Small
            )
        }

    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun AddScansBottomSheetPreview() {
    Box(modifier = Modifier) {
        AddScanBottomSheet()
    }

}