package com.rarilabs.rarime.modules.passportScan

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.passportScan.models.AdditionalPersonDetails
import com.rarilabs.rarime.modules.passportScan.models.DocType
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.PassportImage
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.RewardChip
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.Constants.NOT_ALLOWED_COUNTRIES
import com.rarilabs.rarime.util.ImageUtil
import java.time.LocalDate
import java.time.Period

@Composable
fun PassportDataStep(onNext: () -> Unit, onClose: () -> Unit, eDocument: EDocument) {
    val faceImageInfo = eDocument.personDetails?.faceImageInfo
    val image = faceImageInfo?.let { ImageUtil.getImage(it).bitmapImage }

    val isUnsupported = NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)

    PassportDataStepContent(
        onNext = onNext,
        onClose = onClose,
        eDocument = eDocument,
        image = image,
        isUnsupported = isUnsupported,
    )
}

@Composable
private fun PassportDataStepContent(
    onNext: () -> Unit,
    onClose: () -> Unit,
    eDocument: EDocument,
    image: Bitmap? = null,
    isUnsupported: Boolean,
) {
    ScanPassportLayout(
        step = 3,
        title = stringResource(R.string.passport_data_title),
        text = stringResource(R.string.passport_data_text),
        onClose = {
            onClose.invoke()
        }
    ) {
        Column {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                item {
                    CardContainer {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = eDocument.personDetails!!.name.toString() + " " + eDocument.personDetails!!.surname,
                                    style = RarimeTheme.typography.subtitle3,
                                    color = RarimeTheme.colors.textPrimary
                                )
                                Text(
                                    text = "${eDocument.personDetails!!.gender}, Age: ${
                                        calculateAgeFromBirthDate(
                                            eDocument.personDetails!!.birthDate!!
                                        )
                                    }",
                                    style = RarimeTheme.typography.body3,
                                    color = RarimeTheme.colors.textSecondary
                                )
                            }

                            PassportImage(image = image)
                        }
                    }
                }
                item {
                    CardContainer {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.must_data),
                                    style = RarimeTheme.typography.subtitle3,
                                    color = RarimeTheme.colors.textPrimary
                                )
                                if (!isUnsupported) {
                                    RewardChip(
                                        // TODO: remove hardcoded const
                                        reward = Constants.SCAN_PASSPORT_REWARD.toInt(),
                                        isActive = true
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                MustDataRow(
                                    stringResource(R.string.document_class_mode),
                                    eDocument.docType.toString()
                                )
                                MustDataRow(
                                    stringResource(R.string.issuing_state_code),
                                    eDocument.personDetails!!.nationality.toString()
                                )
                                MustDataRow(
                                    stringResource(R.string.document_number),
                                    eDocument.personDetails!!.serialNumber.toString()
                                )
                            }
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RarimeTheme.colors.backgroundPure)
                    .padding(top = 12.dp, bottom = 20.dp)
                    .padding(horizontal = 20.dp)
            ) {
                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    size = ButtonSize.Large,
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MustDataRow(title: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary
        )
        Text(
            text = value,
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary
        )
    }
}

fun calculateAgeFromBirthDate(birthDate: String): Int {
    // Parse birth date
    val dateOfBirth =
        LocalDate.parse(birthDate, java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))

    // Calculate age
    val currentDate = LocalDate.now()
    return Period.between(dateOfBirth, currentDate).years
}

@Preview
@Composable
private fun SelectDataStepPreview() {
    val eDocument = EDocument(
        docType = DocType.PASSPORT,
        personDetails = PersonDetails(
            name = "John",
            surname = "Doe",
            birthDate = "01.01.1990",
            gender = "MALE",
            issuerAuthority = "USA",
            nationality = "USA",
            serialNumber = "1234567890",
        ),
        additionalPersonDetails = AdditionalPersonDetails()
    )
    PassportDataStepContent(
        onNext = {},
        onClose = {},
        eDocument = eDocument,
        image = null,
        isUnsupported = false,
    )
}
