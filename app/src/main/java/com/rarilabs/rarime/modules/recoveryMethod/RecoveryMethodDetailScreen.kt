package com.rarilabs.rarime.modules.recoveryMethod

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSwitch
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Composable
fun RecoveryMethodDetailScreen(
    onClose: () -> Unit, onCopy: () -> Unit
) {
    val privateKey = "324523h423grewadisabudbawiudawwafa" //todo in future get this from manager
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            IconButton(
                onClick = onClose, modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                AppIcon(id = R.drawable.ic_arrow_left_s_line, tint = RarimeTheme.colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(100.dp))
            Text(
                text = stringResource(R.string.recover_method_details_screen_title),
                style = RarimeTheme.typography.buttonMedium.copy(color = RarimeTheme.colors.textPrimary),
                modifier = Modifier.align(alignment = Alignment.CenterVertically)

            )

        }
        Spacer(modifier = Modifier.width(33.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = RarimeTheme.colors.componentPrimary)

        ) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_key_2_line),
                    contentDescription = "",
                    tint = RarimeTheme.colors.textPrimary,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(20.dp)
                )
                Text(
                    text = stringResource(R.string.recovery_method_detail_screen_card_holder_title),
                    style = RarimeTheme.typography.subtitle5,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier.padding(20.dp)
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                    ),
                shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(containerColor = RarimeTheme.colors.backgroundSurface1)
            ) {
                Text(
                    text = privateKey,
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier.padding(20.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
                    modifier = Modifier.fillMaxWidth(0.95f)

                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_file_copy_line),
                        contentDescription = "",
                        tint = RarimeTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = stringResource(R.string.recovery_method_detail_screen_card_holder_btn_label),
                        style = RarimeTheme.typography.buttonMedium,
                        color = RarimeTheme.colors.textPrimary,
                        modifier = Modifier
                    )
                }
            }
            Text(
                text = stringResource(R.string.recovery_method_detal_screen_card_holder_description),
                style = RarimeTheme.typography.body5,
                minLines = 3,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.padding(20.dp)
            )

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            RecoveryMethodCard(
                isEnabled = true,
                iconId = R.drawable.ic_cloud_line,
                isRecomended = true,
                onCheckedChange = {},
                title = stringResource(R.string.recovery_method_cloud_backup_title),
                description = stringResource(R.string.recovery_method_cloud_backup_description)
            )
            RecoveryMethodCard(
                isEnabled = false,
                iconId = R.drawable.ic_emotion_happy_line,
                isRecomended = false,
                onCheckedChange = {},
                title = stringResource(R.string.recovery_method_face_scan_title),
                description = stringResource(R.string.recovery_method_face_scan_description)
            )

            RecoveryMethodCard(
                isEnabled = false,
                iconId = R.drawable.ic_box_3_line,
                isRecomended = false,
                onCheckedChange = {},
                title = stringResource(R.string.recovery_method_object_scan_title),
                description = stringResource(R.string.recovery_method_object_scan_description)
            )
        }

    }

}

@Composable
private fun RecoveryMethodCard(
    isEnabled: Boolean,
    iconId: Int,
    isRecomended: Boolean,
    onCheckedChange: (Boolean) -> Unit,//todo for logic
    // isChecked: Boolean,
    title: String,
    description: String
) = if (isEnabled) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 1.dp, color = RarimeTheme.colors.warningBase),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {
        if (isRecomended) {
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "RECOMENDED",
                    style = RarimeTheme.typography.overline3,
                    color = RarimeTheme.colors.baseWhite,
                    modifier = Modifier
                        .background(
                            color = RarimeTheme.colors.warningBase,
                            shape = RoundedCornerShape(bottomStart = 8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)

                )
            }
        } else {
            Spacer(modifier = Modifier.width(12.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 20.dp, top = 10.dp)
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = "",
                tint = RarimeTheme.colors.textPrimary,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp, end = 20.dp)
                    .size(20.dp)
            )

            Column(modifier = Modifier) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.subtitle5,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    style = RarimeTheme.typography.body5,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            var isChecked by remember { mutableStateOf(false) }
            AppSwitch(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    onCheckedChange
                },
                enabled = isEnabled,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(width = 40.dp, height = 24.dp)
            )

        }
    }
} else {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 1.dp, color = RarimeTheme.colors.componentPrimary),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 30.dp)
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = "",
                tint = RarimeTheme.colors.textSecondary,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp, end = 20.dp)
                    .size(20.dp)
            )

            Column(modifier = Modifier) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.subtitle5,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    style = RarimeTheme.typography.body5,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "SOON",
                style = RarimeTheme.typography.overline3,
                color = RarimeTheme.colors.baseBlack,
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .background(
                        brush = RarimeTheme.colors.gradient12, shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)


            )


        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun PreviewRecoveryMethodDetailScreen(

) {

    RecoveryMethodDetailScreen(onClose = {}, onCopy = {})

}
