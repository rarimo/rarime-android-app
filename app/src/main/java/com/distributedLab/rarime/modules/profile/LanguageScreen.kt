package com.distributedLab.rarime.modules.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onLanguageChanged: (AppLanguage) -> Unit,
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
                    onClick = { onLanguageChanged(lang) }
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
        Text(
            text = language.toLocalizedString(),
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary
        )
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    LanguageScreen(
        language = AppLanguage.ENGLISH,
        onLanguageChanged = {},
        onBack = {}
    )
}
