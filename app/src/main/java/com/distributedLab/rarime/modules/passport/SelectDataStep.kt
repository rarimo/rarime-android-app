package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSwitch
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.RewardChip
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun SelectDataStep(onNext: () -> Unit, onClose: () -> Unit) {
    ScanPassportLayout(
        step = 3,
        title = "Select your data",
        text = "Selected data can be used as anonymised proofs",
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
                                    text = "Joshua Smith",
                                    style = RarimeTheme.typography.subtitle3,
                                    color = RarimeTheme.colors.textPrimary
                                )
                                Text(
                                    text = "Male, Age: 24",
                                    style = RarimeTheme.typography.body3,
                                    color = RarimeTheme.colors.textSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
                                    .padding(12.dp)
                            ) {
                                AppIcon(
                                    id = R.drawable.ic_user,
                                    size = 32.dp,
                                    tint = RarimeTheme.colors.textPrimary
                                )
                            }
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
                                    text = "Must Data",
                                    style = RarimeTheme.typography.subtitle3,
                                    color = RarimeTheme.colors.textPrimary
                                )
                                RewardChip(reward = 50, isActive = true)
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                MustDataRow("Document class mode", "P")
                                MustDataRow("Issuing state code", "USA")
                                MustDataRow("Document number", "00AA00000")
                            }
                        }
                    }
                }
                item {
                    CardContainer(modifier = Modifier.padding(bottom = 20.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(
                                text = "Additional Data",
                                style = RarimeTheme.typography.subtitle3,
                                color = RarimeTheme.colors.textPrimary
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        AppSwitch()
                                        Text(
                                            text = "Select All",
                                            style = RarimeTheme.typography.subtitle4,
                                            color = RarimeTheme.colors.textSecondary
                                        )
                                    }
                                    RewardChip(reward = 35)
                                }
                                HorizontalDivider()
                                DataItemSelector(
                                    label = "Expiry date",
                                    value = "03/14/2060",
                                    reward = 5
                                )
                                DataItemSelector(
                                    label = "Date of issue",
                                    value = "03/14/2024",
                                    reward = 5
                                )
                                DataItemSelector(
                                    label = "Nationality",
                                    value = "USA",
                                    reward = 20
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
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                val text = buildAnnotatedString {
                    append("You will claim ")
                    withStyle(RarimeTheme.typography.subtitle5.toSpanStyle()) {
                        append("80 / ")
                    }
                    append("85 RMO")
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\uD83C\uDF81",
                        style = RarimeTheme.typography.body4,
                    )
                    Text(
                        text = text,
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                PrimaryButton(
                    text = "Continue",
                    size = ButtonSize.Large,
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DataItemSelector(
    label: String = "Date of issue",
    value: String = "03/14/2024",
    reward: Int = 10
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppSwitch()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = label,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
                Text(
                    text = value,
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary
                )
            }
        }
        RewardChip(reward = reward)
    }
}

@Composable
private fun MustDataRow(title: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
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

@Preview
@Composable
private fun SelectDataStepPreview() {
    SelectDataStep(onNext = {}, onClose = {})
}
