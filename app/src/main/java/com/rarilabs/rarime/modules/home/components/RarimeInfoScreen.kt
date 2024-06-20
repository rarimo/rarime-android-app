package com.rarilabs.rarime.modules.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun RarimeInfoScreen(onClose: () -> Unit) {
    HomeIntroLayout(
        icon = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
            ) {
                AppIcon(
                    id = R.drawable.ic_rarime,
                    size = 32.dp,
                    tint = RarimeTheme.colors.textPrimary
                )
            }
        },
        title = stringResource(R.string.what_is_rarime_title)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DescriptionStepRow(text = stringResource(R.string.what_is_rarime_text_item_1))
                DescriptionStepRow(text = stringResource(R.string.what_is_rarime_text_item_2))
                DescriptionStepRow(annotatedText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = RarimeTheme.colors.textPrimary
                        )
                    ) {
                        append(stringResource(R.string.what_is_rarime_text_item_3_accent))
                    }
                    append(stringResource(R.string.what_is_rarime_text_item_3))
                })
            }
            PrimaryButton(
                text = stringResource(R.string.okay_btn),
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                onClick = onClose
            )
        }
    }
}

@Composable
private fun DescriptionStepRow(text: String? = null, annotatedText: AnnotatedString? = null) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "•",
            style = RarimeTheme.typography.subtitle3,
            color = RarimeTheme.colors.textSecondary,
        )
        text?.let {
            Text(
                text = it,
                style = RarimeTheme.typography.body2,
                color = RarimeTheme.colors.textSecondary,
            )
        }
        annotatedText?.let {
            Text(
                text = it,
                style = RarimeTheme.typography.body2,
                color = RarimeTheme.colors.textSecondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RarimeInfoScreenPreview() {
    RarimeInfoScreen(onClose = {})
}