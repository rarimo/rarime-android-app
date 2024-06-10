package com.distributedLab.rarime.modules.home.components.passport

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.PassportIdentifier
import com.distributedLab.rarime.data.enums.PassportStatus
import com.distributedLab.rarime.data.enums.getBackgroundColor
import com.distributedLab.rarime.data.enums.getForegroundColor
import com.distributedLab.rarime.data.enums.getTitle
import com.distributedLab.rarime.data.enums.toLocalizedTitle
import com.distributedLab.rarime.data.enums.toLocalizedValue
import com.distributedLab.rarime.data.enums.toTitleStub
import com.distributedLab.rarime.data.enums.toValueStub
import com.distributedLab.rarime.modules.passport.calculateAgeFromBirthDate
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSwitch
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PassportImage
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.ImageUtil


@Composable
fun PassportCard(
    passport: EDocument,
    look: PassportCardLook,
    identifiers: List<PassportIdentifier>,
    isIncognito: Boolean,
    passportStatus: PassportStatus,
    onLookChange: (PassportCardLook) -> Unit,
    onIncognitoChange: (Boolean) -> Unit,
    onIdentifiersChange: (List<PassportIdentifier>) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val settingsSheetState = rememberAppSheetState()
    var isPressing by remember { mutableStateOf(false) }

    // TODO: fix recomposition
    val fullName = passport.personDetails!!.name + " " + passport.personDetails!!.surname
    val faceImageInfo = passport.personDetails!!.faceImageInfo
    val image = if (faceImageInfo == null) null else ImageUtil.getImage(faceImageInfo).bitmapImage!!

    val isInfoHidden = isIncognito && !isPressing


    Column(verticalArrangement = Arrangement.spacedBy((-43).dp)) {
        if (passportStatus == PassportStatus.WAITLIST || passportStatus == PassportStatus.NOT_ALLOWED) {
            StatusCard(modifier = Modifier.padding(top = 20.dp), passportStatus)
        }
        Column(verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(look.getBackgroundColor(), RoundedCornerShape(24.dp))
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isPressing = true
                        tryAwaitRelease()
                        isPressing = false
                    })
                }) {

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PassportImage(
                        image = image,
                        color = look.getForegroundColor(),
                        backgroundColor = look.getForegroundColor().copy(alpha = 0.05f),
                        modifier = Modifier.blur(
                            if (isInfoHidden) 12.dp else 0.dp,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppIcon(
                            id = if (isInfoHidden) R.drawable.ic_eye_slash else R.drawable.ic_eye,
                            tint = look.getForegroundColor(),
                            modifier = Modifier
                                .background(
                                    look
                                        .getForegroundColor()
                                        .copy(alpha = 0.05f), CircleShape
                                )
                                .padding(8.dp)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onIncognitoChange(!isIncognito) })
                        )
                        AppIcon(
                            id = R.drawable.ic_dots_three_outline,
                            tint = look.getForegroundColor(),
                            modifier = Modifier
                                .background(
                                    look
                                        .getForegroundColor()
                                        .copy(alpha = 0.05f), CircleShape
                                )
                                .padding(8.dp)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { settingsSheetState.show() })
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isInfoHidden) "••••• •••••••" else fullName,
                        style = RarimeTheme.typography.h6,
                        color = look.getForegroundColor()
                    )
                    Text(
                        text = if (isInfoHidden) "••• ••••• •••" else stringResource(
                            R.string.years_old, calculateAgeFromBirthDate(
                                passport.personDetails!!.birthDate!!
                            )
                        ),
                        style = RarimeTheme.typography.body2,
                        color = look.getForegroundColor().copy(alpha = 0.56f)
                    )
                }
            }
            HorizontalDivider(color = look.getForegroundColor().copy(alpha = 0.05f))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(50.dp)
            ) {
                identifiers.forEach { identifier ->
                    PassportInfoRow(
                        look = look,
                        label = if (isInfoHidden) identifier.toTitleStub() else identifier.toLocalizedTitle(),
                        value = if (isInfoHidden) {
                            identifier.toValueStub()
                        } else {
                            identifier.toLocalizedValue(passport)
                        }
                    )
                }
            }
        }
    }



    AppBottomSheet(state = settingsSheetState) {
        PassportCardSettings(
            look = look,
            identifiers = identifiers,
            onLookChange = onLookChange,
            onIdentifiersChange = onIdentifiersChange
        )
    }
}

@Composable
private fun PassportInfoRow(label: String, value: String, look: PassportCardLook) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = RarimeTheme.typography.body3,
            color = look.getForegroundColor().copy(alpha = 0.56f)
        )
        Text(
            text = value,
            style = RarimeTheme.typography.subtitle4,
            color = look.getForegroundColor()
        )
    }
}

@Composable
private fun PassportCardSettings(
    look: PassportCardLook,
    identifiers: List<PassportIdentifier>,
    onLookChange: (PassportCardLook) -> Unit,
    onIdentifiersChange: (List<PassportIdentifier>) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)
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
                    PassportLookOption(look = item,
                        isActive = item == look,
                        modifier = Modifier.weight(1f),
                        onClick = { onLookChange(item) })
                }
            }
            HorizontalDivider()
            PassportIdentifiersPicker(identifiers, onIdentifiersChange)
        }
    }
}

@Composable
private fun PassportIdentifiersPicker(
    identifiers: List<PassportIdentifier>, onIdentifiersChange: (List<PassportIdentifier>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.passport_card_identifiers_title),
                style = RarimeTheme.typography.overline3,
                color = RarimeTheme.colors.textSecondary
            )
            Text(
                text = stringResource(R.string.passport_card_identifiers_text),
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary
            )
        }
        PassportIdentifier.entries.forEach { identifier ->
            val isSelected = identifiers.contains(identifier)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = identifier.toLocalizedTitle(),
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary
                )
                AppSwitch(
                    checked = isSelected,
                    enabled = identifiers.size < Constants.MAX_PASSPORT_IDENTIFIERS || isSelected,
                    onCheckedChange = {
                        val newIdentifiers = if (isSelected) {
                            identifiers.filter { it != identifier }
                        } else {
                            identifiers + listOf(identifier)
                        }
                        onIdentifiersChange(newIdentifiers.sortedBy { it.order })
                    },
                )
            }
        }
    }
}

@Composable
private fun PassportLookOption(
    look: PassportCardLook, isActive: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit
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
                .background(look.getBackgroundColor(), RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = look
                        .getForegroundColor()
                        .copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp)
                    .background(
                        look
                            .getForegroundColor()
                            .copy(alpha = 0.1f), CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .width(29.dp)
                    .height(5.dp)
                    .background(
                        look
                            .getForegroundColor()
                            .copy(alpha = 0.1f), RoundedCornerShape(100.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .width(19.dp)
                    .height(5.dp)
                    .background(
                        look
                            .getForegroundColor()
                            .copy(alpha = 0.1f), RoundedCornerShape(100.dp)
                    )
            )
        }
        Text(
            text = look.getTitle(),
            style = RarimeTheme.typography.buttonMedium,
            color = RarimeTheme.colors.textPrimary
        )
    }
}

@Composable
fun StatusCard(modifier: Modifier = Modifier, passportStatus: PassportStatus) {

    var statusIcon = remember {
        R.drawable.ic_globe_simple_time
    }

    var statusTitle = remember {
        R.string.waitlist_title
    }

    var statusDescription = remember {
        R.string.waitlist_card_subtitle
    }


    when (passportStatus) {
        PassportStatus.WAITLIST -> remember {
            statusIcon = R.drawable.ic_globe_simple_time
            statusTitle = R.string.waitlist_title
            statusDescription = R.string.waitlist_card_subtitle
        }

        PassportStatus.NOT_ALLOWED -> remember {
            statusIcon = R.drawable.ic_globe_simple_x
            statusTitle = R.string.unsupported_card_title
        }

        else -> {
            return
        }
    }



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(RarimeTheme.colors.componentPrimary, RoundedCornerShape(24.dp))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AppIcon(
                id = statusIcon,
                tint = if(passportStatus == PassportStatus.WAITLIST) RarimeTheme.colors.warningMain else RarimeTheme.colors.errorMain,
                modifier = Modifier.padding(vertical = 4.dp),
                size = 24.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(id = statusTitle),
                    style = RarimeTheme.typography.subtitle5,
                    color = RarimeTheme.colors.textPrimary
                )
                if (passportStatus == PassportStatus.WAITLIST) {
                    Text(
                        text = stringResource(id = statusDescription),
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary
                    )
                }

            }
        }

    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatusCardPreview() {
    StatusCard(passportStatus = PassportStatus.NOT_ALLOWED)
}

@Preview(showBackground = true)
@Composable
private fun PassportCardPreview() {
    var isIncognito by remember { mutableStateOf(false) }
    var look by remember { mutableStateOf(PassportCardLook.BLACK) }
    var identifiers by remember {
        mutableStateOf(
            listOf(
                PassportIdentifier.NATIONALITY, PassportIdentifier.DOCUMENT_ID
            )
        )
    }

    PassportCard(passport = EDocument(
        personDetails = PersonDetails(
            name = "John",
            surname = "Doe",
            birthDate = "01.01.1990",
            expiryDate = "01.01.2025",
            nationality = "USA",
            serialNumber = "123456789",
            faceImageInfo = null
        )
    ),
        look = look,
        identifiers = identifiers,
        isIncognito = isIncognito,
        onLookChange = { look = it },
        onIncognitoChange = { isIncognito = it },
        passportStatus = PassportStatus.NOT_ALLOWED,
        onIdentifiersChange = { identifiers = it })
}
