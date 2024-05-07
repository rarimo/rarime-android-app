package com.distributedLab.rarime.modules.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.toLocalizedString
import com.distributedLab.rarime.ui.components.AppRadioButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun LanguageScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit
) {
    ProfileRouteLayout(
        title = stringResource(R.string.language),
        onBack = onBack
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppLanguage.entries.forEach { lang ->
                LanguageItem(
                    language = lang,
                    isSelected = lang == language,
                    onClick = { onLanguageChange(lang) }
                )
            }

        }
    }
}

@Composable
private fun LanguageItem(
    language: AppLanguage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    AppRadioButton(isSelected = isSelected, onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
            ) {
            Text(
                text = language.flag,
                style = RarimeTheme.typography.subtitle3,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = language.toLocalizedString(),
                style = RarimeTheme.typography.buttonMedium,
                color = RarimeTheme.colors.textPrimary
            )
        }
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    LanguageScreen(
        language = AppLanguage.ENGLISH,
        onLanguageChange = {},
        onBack = {}
    )
}
