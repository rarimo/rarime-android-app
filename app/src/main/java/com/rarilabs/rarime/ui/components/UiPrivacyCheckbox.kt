package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
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
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants

@Composable
fun UiPrivacyCheckbox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    termsAcceptedState: AppCheckboxState = rememberAppCheckboxState(),
) {
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

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppCheckbox(state = termsAcceptedState, enabled = enabled)
        ClickableText(
            text = termsAnnotation,
            style = RarimeTheme.typography.body5.copy(color = RarimeTheme.colors.textSecondary),
            onClick = {
                termsAnnotation
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            }
        )
    }
}

@Preview
@Composable
fun UiPrivacyCheckboxPreview() {
    Column(
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(24.dp)
    ) {
        UiPrivacyCheckbox()
    }
}