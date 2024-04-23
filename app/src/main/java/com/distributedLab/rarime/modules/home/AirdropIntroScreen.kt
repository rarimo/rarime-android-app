package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppCheckbox
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppCheckboxState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun AirdropIntroScreen(onStart: () -> Unit) {
    val termsAcceptedState = rememberAppCheckboxState()
    val uriHandler = LocalUriHandler.current

    // TODO: Replace with real URLs
    val termsUrl = "https://rarime.com"
    val privacyUrl = "https://rarime.com"
    val airdropTermsUrl = "https://rarime.com"

    val termsAnnotation = buildAnnotatedString {
        append("By checking this box, you are agreeing to ")
        pushStringAnnotation("URL", termsUrl)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append("RariMe General Terms & Conditions")
        }
        pop()
        append(", ")
        pushStringAnnotation("URL", privacyUrl)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append("RariMe Privacy Notice")
        }
        pop()
        append(" and ")
        pushStringAnnotation("URL", airdropTermsUrl)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append("Rarimo Airdrop Program Terms & Conditions")
        }
        pop()
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 20.dp)
    ) {
        HomeIntroLayout(
            icon = {
                Text(
                    text = "🇺🇦",
                    style = RarimeTheme.typography.h5,
                    color = RarimeTheme.colors.textPrimary,
                )
            },
            title = "Programable Airdrop",
            description = "Beta launch is focused on distributing tokens to Ukrainian identity holders"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DescriptionStepRow("1.", stringResource(R.string.airdrop_intro_step_1))
                DescriptionStepRow("2.", stringResource(R.string.airdrop_intro_step_2))
                DescriptionStepRow("3.", stringResource(R.string.airdrop_intro_step_3))
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "WHAT IS THIS?",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary
                )
                Text(
                    text = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it h",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = buildAnnotatedString {
                        append("Full functional available on: ")
                        withStyle(RarimeTheme.typography.subtitle5.toSpanStyle()) {
                            append("July")
                        }
                    },
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.warningMain
                )

            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HorizontalDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppCheckbox(state = termsAcceptedState)
                    ClickableText(
                        text = termsAnnotation,
                        style = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary),
                        onClick = {
                            termsAnnotation
                                .getStringAnnotations("URL", it, it)
                                .firstOrNull()?.let { stringAnnotation ->
                                    uriHandler.openUri(stringAnnotation.item)
                                }
                        }
                    )
                }
                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = termsAcceptedState.checked,
                    size = ButtonSize.Large,
                    onClick = onStart
                )
            }
        }
    }
}

@Composable
fun DescriptionStepRow(prefix: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(18.dp),
            text = prefix,
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary,
        )
        Text(
            text = text,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PassportIntroScreenPreview() {
    AirdropIntroScreen(onStart = {})
}