package com.distributedLab.rarime.modules.security

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.TertiaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun EnableScreenLayout(
    title: String,
    text: String,
    @DrawableRes icon: Int,
    onEnable: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(vertical = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 56.dp)
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(RarimeTheme.colors.primaryLighter, CircleShape)
                    .padding(42.dp),
            ) {
                AppIcon(id = icon, size = 72.dp, tint = RarimeTheme.colors.primaryDarker)
            }
            Text(
                text = title,
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary,
                modifier = Modifier.padding(top = 64.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = text,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.padding(top = 12.dp),
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider()
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 20.dp)
        ) {
            PrimaryButton(
                text = stringResource(R.string.enable_btn),
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = onEnable
            )
            TertiaryButton(
                text = "Maybe Later",
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = onSkip
            )
        }
    }
}

@Preview
@Composable
private fun EnableScreenLayoutPreview() {
    EnableScreenLayout(
        title = "Enable\nScreen",
        text = "Some text here to explain",
        icon = R.drawable.ic_fingerprint,
        onEnable = {},
        onSkip = {}
    )
}