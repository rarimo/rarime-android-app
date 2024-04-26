package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun BetaLaunchScreen(onClose: () -> Unit) {
    HomeIntroLayout(
        icon = {
            AppIcon(
                id = R.drawable.ic_rarime,
                size = 32.dp,
                tint = RarimeTheme.colors.textPrimary
            )
        },
        title = stringResource(R.string.beta_launch_title),
        description = stringResource(R.string.beta_launch_description)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.beta_launch_text),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.full_functional_available_on))
                        withStyle(RarimeTheme.typography.subtitle5.toSpanStyle()) {
                            append(stringResource(R.string.july))
                        }
                    },
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.warningMain
                )
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

@Preview(showBackground = true)
@Composable
private fun BetaLaunchScreenPreview() {
    BetaLaunchScreen(onClose = {})
}