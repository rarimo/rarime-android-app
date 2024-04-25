package com.distributedLab.rarime.modules.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppRadioButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

enum class AppLanguage {
    ENGLISH,
    UKRAINIAN,
    GEORGIAN
}

@Composable
fun LanguageScreen(onBack: () -> Unit) {
    var selectedLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }

    ProfileRouteLayout(
        title = stringResource(R.string.language),
        onBack = onBack
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppLanguage.entries.forEach { language ->
                LanguageItem(
                    language = language,
                    isSelected = language == selectedLanguage,
                    onClick = { selectedLanguage = language }
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
    val label = when (language) {
        AppLanguage.ENGLISH -> stringResource(R.string.english)
        AppLanguage.UKRAINIAN -> stringResource(R.string.ukrainian)
        AppLanguage.GEORGIAN -> stringResource(R.string.georgian)
    }

    AppRadioButton(isSelected = isSelected, onClick = onClick) {
        Text(
            text = label,
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary
        )
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    LanguageScreen {}
}
