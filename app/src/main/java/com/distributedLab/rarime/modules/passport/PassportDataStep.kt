package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.models.AdditionalPersonDetails
import com.distributedLab.rarime.modules.passport.models.DocType
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.RewardChip
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.ImageUtil
import java.time.LocalDate
import java.time.Period

@Composable
fun PassportDataStep(onNext: () -> Unit, onClose: () -> Unit, eDocument: EDocument) {
    val faceImageInfo = eDocument.personDetails!!.faceImageInfo
    val image = ImageUtil.getImage(faceImageInfo!!).bitmapImage!!

    ScanPassportLayout(
        step = 3,
        title = stringResource(R.string.passport_data_title),
        text = stringResource(R.string.passport_data_text),
        onClose = onClose
    ) {
        Column {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    CardContainer {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
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

                            Image(
                                bitmap = image.asImageBitmap(),
                                contentScale = ContentScale.FillWidth,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
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
                                RewardChip(reward = 50, isActive = true)
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                MustDataRow(
                                    stringResource(R.string.document_class_mode),
                                    eDocument.docType.toString()
                                )
                                MustDataRow(
                                    stringResource(R.string.issuing_state_code),
                                    eDocument.personDetails!!.issuerAuthority.toString()
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
            serialNumber = "1234567890",
        ),
        additionalPersonDetails = AdditionalPersonDetails()
    )
    PassportDataStep(onNext = {}, onClose = {}, eDocument = eDocument)
}
