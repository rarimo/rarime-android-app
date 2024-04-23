package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.calculateAgeFromBirthDate
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PassportImage
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.ImageUtil

enum class PassportCardLook {
    GREEN, BLACK, WHITE;

    val backgroundColor: Color
        @Composable
        get() = when (this) {
            GREEN -> RarimeTheme.colors.primaryMain
            BLACK -> RarimeTheme.colors.baseBlack
            WHITE -> RarimeTheme.colors.baseWhite
        }

    val foregroundColor: Color
        @Composable
        get() = when (this) {
            GREEN -> RarimeTheme.colors.baseBlack
            BLACK -> RarimeTheme.colors.baseWhite
            WHITE -> RarimeTheme.colors.baseBlack
        }

    val titleResourceId: Int
        get() = when (this) {
            GREEN -> R.string.passport_look_green
            BLACK -> R.string.passport_look_black
            WHITE -> R.string.passport_look_white
        }
}

@Composable
fun PassportCard(
    passport: EDocument,
    look: PassportCardLook,
    isIncognito: Boolean,
    onLookChange: (PassportCardLook) -> Unit,
    onIncognitoChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val settingsSheetState = rememberAppSheetState()

    val fullName = passport.personDetails!!.name + " " + passport.personDetails!!.surname
    val faceImageInfo = passport.personDetails!!.faceImageInfo
    val image = if (faceImageInfo == null) null else ImageUtil.getImage(faceImageInfo).bitmapImage!!

    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(look.backgroundColor, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PassportImage(
                    image = image,
                    color = look.foregroundColor,
                    backgroundColor = look.foregroundColor.copy(alpha = 0.05f),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppIcon(
                        id = if (isIncognito) R.drawable.ic_eye_slash else R.drawable.ic_eye,
                        tint = look.foregroundColor,
                        modifier = Modifier
                            .background(look.foregroundColor.copy(alpha = 0.05f), CircleShape)
                            .padding(8.dp)
                            .clickable { onIncognitoChange(!isIncognito) }
                    )
                    AppIcon(
                        id = R.drawable.ic_dots_three_outline,
                        tint = look.foregroundColor,
                        modifier = Modifier
                            .background(look.foregroundColor.copy(alpha = 0.05f), CircleShape)
                            .padding(8.dp)
                            .clickable { settingsSheetState.show() }
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isIncognito) "••••• •••••••" else fullName,
                    style = RarimeTheme.typography.h6,
                    color = look.foregroundColor
                )
                Text(
                    text = if (isIncognito) "••• ••••• •••" else stringResource(
                        R.string.years_old, calculateAgeFromBirthDate(
                            passport.personDetails!!.birthDate!!
                        )
                    ),
                    style = RarimeTheme.typography.body2,
                    color = look.foregroundColor.copy(alpha = 0.56f)
                )
            }
        }
        HorizontalDivider(color = look.foregroundColor.copy(alpha = 0.05f))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PassportInfoRow(
                label = stringResource(R.string.nationality),
                value = if (isIncognito) "•••" else passport.personDetails!!.nationality!!,
                look = look
            )
            PassportInfoRow(
                label = stringResource(R.string.document_number),
                value = if (isIncognito) "••••••••" else passport.personDetails!!.serialNumber!!,
                look = look
            )
        }
    }

    AppBottomSheet(state = settingsSheetState) {
        PassportCardSettings(
            look = look,
            onLookChange = onLookChange,
            onDelete = onDelete
        )
    }
}

@Composable
private fun PassportInfoRow(label: String, value: String, look: PassportCardLook) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = RarimeTheme.typography.body3,
            color = look.foregroundColor.copy(alpha = 0.56f)
        )
        Text(
            text = value,
            style = RarimeTheme.typography.subtitle4,
            color = look.foregroundColor
        )
    }
}

@Composable
private fun PassportCardSettings(
    look: PassportCardLook,
    onLookChange: (PassportCardLook) -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.settings_title),
            style = RarimeTheme.typography.h6,
            color = RarimeTheme.colors.textPrimary,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)
        )
        HorizontalDivider()
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.card_visual),
                style = RarimeTheme.typography.overline3,
                color = RarimeTheme.colors.textSecondary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PassportCardLook.entries.forEach { item ->
                    PassportLookOption(
                        look = item,
                        isActive = item == look,
                        modifier = Modifier.weight(1f),
                        onClick = { onLookChange(item) }
                    )
                }
            }
            HorizontalDivider()
            PrimaryTextButton(onClick = onDelete) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        id = R.drawable.ic_trash_simple,
                        tint = RarimeTheme.colors.errorMain,
                        modifier = Modifier
                            .background(RarimeTheme.colors.errorLighter, CircleShape)
                            .padding(10.dp)
                    )
                    Text(
                        text = stringResource(R.string.delete_card_btn),
                        style = RarimeTheme.typography.buttonMedium,
                        color = RarimeTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PassportLookOption(
    look: PassportCardLook,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isActive) Color.Transparent else RarimeTheme.colors.componentPrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                if (isActive) RarimeTheme.colors.componentPrimary else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(64.dp)
                .height(48.dp)
                .background(look.backgroundColor, RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = look.foregroundColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp)
                    .background(look.foregroundColor.copy(alpha = 0.1f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(29.dp)
                    .height(5.dp)
                    .background(look.foregroundColor.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
            )
            Box(
                modifier = Modifier
                    .width(19.dp)
                    .height(5.dp)
                    .background(look.foregroundColor.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
            )
        }
        Text(
            text = stringResource(look.titleResourceId),
            style = RarimeTheme.typography.buttonMedium,
            color = RarimeTheme.colors.textPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PassportCardPreview() {
    PassportCard(
        passport = EDocument(
            personDetails = PersonDetails(
                name = "John",
                surname = "Doe",
                birthDate = "01.01.1990",
                nationality = "USA",
                serialNumber = "123456789",
                faceImageInfo = null
            )
        ),
        look = PassportCardLook.BLACK,
        isIncognito = false,
        onLookChange = {},
        onIncognitoChange = {},
        onDelete = {}
    )
}
