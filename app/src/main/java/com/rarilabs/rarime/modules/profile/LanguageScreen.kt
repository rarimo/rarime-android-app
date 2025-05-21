package com.rarilabs.rarime.modules.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppLanguage
import com.rarilabs.rarime.data.enums.toLocalizedString
import com.rarilabs.rarime.ui.components.AppRadioButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel = hiltViewModel(),
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit
) {

    val language by viewModel.language.collectAsState()

    ProfileRouteLayout(
        title = stringResource(R.string.language), onBack = onBack
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppLanguage.entries.forEach { lang ->
                LanguageItem(
                    language = lang,
                    isSelected = lang == language,
                    onClick = { onLanguageChange.invoke(lang); viewModel.updateLanguage(lang) })
            }
        }
    }
}

@Composable
private fun LanguageItem(
    language: AppLanguage, isSelected: Boolean, onClick: () -> Unit
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
    LanguageScreen(onBack = {}, onLanguageChange = {})
}
