package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.distributedLab.rarime.util.Constants

@Composable
fun AirdropIntroScreen(onStart: () -> Unit) {
    val termsAcceptedState = rememberAppCheckboxState()
    val uriHandler = LocalUriHandler.current

    val termsAnnotation = buildAnnotatedString {
        append(stringResource(R.string.terms_check_agreement))
        pushStringAnnotation("URL", Constants.TERMS_URL)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(stringResource(R.string.rarime_general_terms_conditions))
        }
        pop()
        append(", ")
        pushStringAnnotation("URL", Constants.PRIVACY_URL)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(stringResource(R.string.rarime_privacy_notice))
        }
        pop()
        append(stringResource(R.string.and))
        pushStringAnnotation("URL", Constants.AIRDROP_TERMS_URL)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(stringResource(R.string.rarimo_airdrop_program_terms_conditions))
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
            title = stringResource(R.string.airdrop_intro_title),
            description = stringResource(R.string.airdrop_intro_description)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.airdrop_intro_text_title),
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.airdrop_intro_text),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
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

@Preview(showBackground = true)
@Composable
private fun AirdropIntroScreenPreview() {
    AirdropIntroScreen(onStart = {})
}