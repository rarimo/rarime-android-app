package com.distributedLab.rarime.data.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.distributedLab.rarime.R

enum class AppLanguage(val value: String) {
    ENGLISH("en"),
    UKRAINIAN("uk"),
    GEORGIAN("ka");

    companion object {
        fun fromString(value: String) = entries.first { it.value == value }
    }
}

@Composable
fun AppLanguage.toLocalizedString(): String {
    return when (this) {
        AppLanguage.ENGLISH -> stringResource(R.string.english)
        AppLanguage.UKRAINIAN -> stringResource(R.string.ukrainian)
        AppLanguage.GEORGIAN -> stringResource(R.string.georgian)
    }
}