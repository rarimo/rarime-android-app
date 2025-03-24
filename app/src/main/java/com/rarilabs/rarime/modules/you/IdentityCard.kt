package com.rarilabs.rarime.modules.you

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
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
import com.rarilabs.rarime.modules.passportScan.calculateAgeFromBirthDate
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.BackgroundRemover

@Composable
fun IdentityCard(
    modifier: Modifier = Modifier,
    passport: EDocument,
    look: PassportCardLook,
    identifiers: List<PassportIdentifier>,
    isIncognito: Boolean,
    passportStatus: PassportStatus,
    onLookChange: (PassportCardLook) -> Unit,
    onIncognitoChange: (Boolean) -> Unit,
    onIdentifiersChange: (List<PassportIdentifier>) -> Unit
) {

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var isPressing by remember { mutableStateOf(false) }


    val isInfoHidden = isIncognito && !isPressing

    var faceImage: ImageBitmap? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(Unit) {

        val bitmap = passport.personDetails?.getPortraitImage()

        bitmap?.let {
            BackgroundRemover().removeBackground(it) { image ->
                val removedBackendBitmap = image?.asImageBitmap()
                faceImage = removedBackendBitmap
            }
        }

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
                    painterResource(id = R.drawable.card_bg2),
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
                    text = "ID CARD",
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

                Image(
                    modifier = Modifier
                        .height(157.dp)
                        .offset(y = 8.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.FillHeight,
                    bitmap = faceImage ?: ImageBitmap(1, 1),
                    contentDescription = null
                )
            }


            Box(Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                IdentityCardBottomBar()
            }

        }
    }
}


@Preview
@Composable
private fun IdentityCardPreview() {

    var isIncognito by remember { mutableStateOf(false) }
    var look by remember { mutableStateOf(PassportCardLook.BLACK) }
    var identifiers by remember {
        mutableStateOf(
            listOf(
                PassportIdentifier.NATIONALITY, PassportIdentifier.DOCUMENT_ID
            )
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
        identifiers = identifiers,
        isIncognito = isIncognito,
        onLookChange = { look = it },
        onIncognitoChange = { isIncognito = it },
        passportStatus = PassportStatus.NOT_ALLOWED,
        onIdentifiersChange = { identifiers = it })
}