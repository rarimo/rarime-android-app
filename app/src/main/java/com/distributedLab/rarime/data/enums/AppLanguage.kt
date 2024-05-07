package com.distributedLab.rarime.data.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.distributedLab.rarime.R

enum class AppLanguage(val localeTag: String, val flag: String) {
    ENGLISH("en", "🇺🇸"),
    UKRAINIAN("uk", "🇺🇦"),
    GEORGIAN("ka", "🇬🇪");

    companion object {
        fun fromLocaleTag(tag: String) = entries.first { tag.startsWith(it.localeTag) }
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