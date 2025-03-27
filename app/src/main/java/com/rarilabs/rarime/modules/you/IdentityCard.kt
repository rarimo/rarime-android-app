package com.rarilabs.rarime.modules.you

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.enums.getBackgroundImage
import com.rarilabs.rarime.data.enums.getForegroundColor
import com.rarilabs.rarime.data.enums.toLocalizedTitle
import com.rarilabs.rarime.data.enums.toLocalizedValue
import com.rarilabs.rarime.modules.passportScan.calculateAgeFromBirthDate
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.modules.passportScan.nfc.RevocationStep
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.BackgroundRemover
import com.rarilabs.rarime.util.DateUtil
import net.sf.scuba.data.Gender
import org.jmrtd.lds.icao.MRZInfo

@Composable
fun IdentityCard(
    modifier: Modifier = Modifier,
    passport: EDocument,
    look: PassportCardLook,
    identifier: PassportIdentifier,
    isIncognito: Boolean,
    passportStatus: PassportStatus,
    onLookChange: (PassportCardLook) -> Unit,
    onIncognitoChange: (Boolean) -> Unit,
    onIdentifierChange: (PassportIdentifier) -> Unit,
    registrationStatus: IdentityCardBottomBarUiState,
    retryRegistration: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val settingsSheetState = rememberAppSheetState()
    val revokeSheetState = rememberAppSheetState()


    var isPressing by remember { mutableStateOf(false) }

    val isInfoHidden = remember(isIncognito, isPressing) {
        isIncognito && !isPressing
    }

    // Animated saturation based on registration status.
    val targetSaturation =
        if (
            registrationStatus.passportStatus == PassportStatus.UNREGISTERED ||
            registrationStatus.passportStatus == PassportStatus.ALREADY_REGISTERED_BY_OTHER_PK
        )
            0f
        else 1f
    val animatedSaturation by animateFloatAsState(
        targetValue = targetSaturation,
        animationSpec = tween(durationMillis = 500)
    )
    // Recompute the color matrix only when animatedSaturation changes.
    val colorMatrix = remember(animatedSaturation) {
        ColorMatrix().apply { setToSaturation(animatedSaturation) }
    }.also {
        it.setToSaturation(animatedSaturation)
    }

    var faceImage: ImageBitmap? by remember { mutableStateOf(null) }

    LaunchedEffect(passport.personDetails) {
        passport.personDetails?.getPortraitImage()?.let { bitmap ->
            BackgroundRemover().removeBackground(bitmap) { image ->
                faceImage = image?.asImageBitmap()
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy((-43).dp)) {

        if (
            listOf(
                PassportStatus.WAITLIST,
                PassportStatus.NOT_ALLOWED,
                PassportStatus.WAITLIST_NOT_ALLOWED
            ).contains(passportStatus)
        ) {
            StatusCard(
                modifier = Modifier.padding(
                    top = 20.dp
                ), passportStatus
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = RarimeTheme.colors.backgroundPrimary)
                .then(modifier)
        ) {

            Column(

                modifier
                    .fillMaxWidth()
                    .paint(
                        painterResource(id = look.getBackgroundImage()),
                        contentScale = ContentScale.FillHeight
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isPressing = true
                            tryAwaitRelease()
                            isPressing = false
                        })
                    }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp, end = 14.dp)
                        .clip(RoundedCornerShape(106.dp))
                        .background(RarimeTheme.colors.componentPrimary)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
                        text = stringResource(R.string.passport).uppercase(),
                        color = RarimeTheme.colors.textPrimary,
                        style = RarimeTheme.typography.overline2
                    )
                }


                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = if (isInfoHidden) "•••••" else passport.personDetails!!.name!!,
                    style = RarimeTheme.typography.h2,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = if (isInfoHidden) "•••••" else passport.personDetails!!.surname!!,
                    style = RarimeTheme.typography.additional2,
                    color = RarimeTheme.colors.textPlaceholder
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(start = 24.dp, top = 6.dp),
                        text = if (isInfoHidden) "••••••••••••" else stringResource(
                            R.string.years_old, calculateAgeFromBirthDate(
                                passport.personDetails!!.birthDate!!
                            )
                        ),

                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.textSecondary,
                    )

                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .height(200.dp)
                            .padding(end = 16.dp)
                            .blur(
                                radius = if (isInfoHidden) 54.dp else 0.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                    ) {
                        Image(
                            alignment = Alignment.BottomEnd,
                            modifier = Modifier
                                .height(200.dp),
                            contentScale = ContentScale.FillHeight,
                            bitmap = faceImage ?: ImageBitmap(1, 1),
                            contentDescription = null,
                            colorFilter = ColorFilter.colorMatrix(colorMatrix)
                        )
                    }


                }


                Box(Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                    IdentityCardBottomBar(
                        registrationStatus = registrationStatus,
                        retryRegistration = retryRegistration,
                        identifier = identifier,
                        eDocument = passport,
                        onIncognitoChange = onIncognitoChange,
                        isIncognito = isInfoHidden,
                        settingsSheetState = settingsSheetState,
                        revokeSheetState = revokeSheetState
                    )
                }

                AppBottomSheet(state = revokeSheetState) {
                    RevocationStep(
                        mrzData = MRZInfo(
                            "P",
                            "NNN",
                            "",
                            "",
                            passport.personDetails!!.serialNumber!!,
                            passport.personDetails!!.nationality!!,
                            DateUtil.convertToMrzDate(passport.personDetails?.birthDate),
                            Gender.UNSPECIFIED,
                            DateUtil.convertToMrzDate(passport.personDetails?.expiryDate),
                            ""
                        ),
                        onClose = { revokeSheetState.hide() },
                        onNext = { revokeSheetState.hide() },
                        onError = { revokeSheetState.hide() }
                    )
                }

                AppBottomSheet(state = settingsSheetState) {
                    PassportCardSettings(
                        look = look,
                        identifiers = identifier,
                        onLookChange = onLookChange,
                        onIdentifiersChange = onIdentifierChange,
                        settingAppBottomSheetState = settingsSheetState,
                        eDocument = passport
                    )
                }

            }
        }
    }


}


@Composable
private fun PassportCardSettings(
    look: PassportCardLook,
    eDocument: EDocument,
    identifiers: PassportIdentifier,
    onLookChange: (PassportCardLook) -> Unit,
    onIdentifiersChange: (PassportIdentifier) -> Unit,
    settingAppBottomSheetState: AppSheetState
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)
            )

            AppIcon(
                modifier = Modifier.clickable { settingAppBottomSheetState.hide() },
                id = R.drawable.ic_close
            )
        }

        HorizontalDivider()
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.card_visual),
                style = RarimeTheme.typography.overline2,
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
                        onClick = { onLookChange(item) })
                }
            }
            HorizontalDivider()
            PassportIdentifiersPicker(identifiers, onIdentifiersChange, eDocument = eDocument)
        }
    }
}

@Composable
private fun PassportIdentifiersPicker(
    selectedIdentifier: PassportIdentifier,
    onIdentifierChange: (PassportIdentifier) -> Unit,
    eDocument: EDocument
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.passport_card_identifiers_title),
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary
            )

        }
        PassportIdentifier.entries.forEach { identifier ->
            val isSelected = selectedIdentifier == identifier
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = identifier.toLocalizedTitle(),
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = identifier.toLocalizedValue(eDocument),
                        style = RarimeTheme.typography.subtitle5,
                        color = RarimeTheme.colors.textPrimary
                    )
                }

                RadioButton(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            return@RadioButton
                        }
                        onIdentifierChange(identifier)
                    }
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
                color = if (isActive) RarimeTheme.colors.textPrimary else RarimeTheme.colors.componentPrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                Color.Transparent,
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

                .clip(RoundedCornerShape(8.dp))
                .paint(
                    painter = painterResource(id = look.getBackgroundImage()),
                    contentScale = ContentScale.Crop
                )
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
        PassportStatus.WAITLIST_NOT_ALLOWED -> {
            statusIcon = R.drawable.ic_globe_simple_x
            statusTitle = R.string.unsupported_card_title
        }

        PassportStatus.WAITLIST -> {
            statusIcon = R.drawable.ic_globe_simple_time
            statusTitle = R.string.waitlist_title
            statusDescription = R.string.waitlist_card_subtitle
        }

        PassportStatus.NOT_ALLOWED -> {
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
                tint = if (passportStatus == PassportStatus.NOT_ALLOWED) RarimeTheme.colors.errorMain else RarimeTheme.colors.warningMain,
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
                if (
                    passportStatus == PassportStatus.WAITLIST ||
                    passportStatus == PassportStatus.WAITLIST_NOT_ALLOWED
                ) {
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


@Preview
@Composable
private fun IdentityCardPreview() {

    var isIncognito by remember { mutableStateOf(false) }
    var look by remember { mutableStateOf(PassportCardLook.BLACK) }
    var identifier by remember {
        mutableStateOf(
            PassportIdentifier.NATIONALITY
        )
    }

    IdentityCard(
        passport = EDocument(
            personDetails = PersonDetails(
                name = "John",
                surname = "Doe",
                birthDate = "01.01.1996",
                expiryDate = "01.01.2025",
                nationality = "USA",
                serialNumber = "123456789",
                faceImageInfo = null
            )
        ),
        look = look,
        identifier = identifier,
        isIncognito = isIncognito,
        onLookChange = { look = it },
        onIncognitoChange = { isIncognito = it },
        passportStatus = PassportStatus.NOT_ALLOWED,
        registrationStatus = IdentityCardBottomBarUiState(),
        retryRegistration = {},
        onIdentifierChange = { identifier = it })
}

@Preview
@Composable
private fun SettingsPreview() {
    var isIncognito by remember { mutableStateOf(false) }
    var look by remember { mutableStateOf(PassportCardLook.BLACK) }
    var identifier by remember {
        mutableStateOf(
            PassportIdentifier.DOCUMENT_ID
        )
    }
    Surface {
        PassportCardSettings(
            look = look,
            identifiers = identifier,
            onLookChange = { look = it },
            onIdentifiersChange = { identifier = it },
            settingAppBottomSheetState = rememberAppSheetState(),
            eDocument = EDocument(
                personDetails = PersonDetails(
                    name = "John",
                    surname = "Doe",
                    birthDate = "01.01.1996",
                    expiryDate = "01.01.2025",
                    nationality = "USA",
                    serialNumber = "123456789",
                    faceImageInfo = null
                )
            ),
        )
    }

}