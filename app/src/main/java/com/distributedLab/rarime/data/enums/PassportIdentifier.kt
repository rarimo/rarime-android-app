package com.distributedLab.rarime.data.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passportScan.models.EDocument
import com.distributedLab.rarime.util.DateUtil

enum class PassportIdentifier(val value: String, val order: Int) {
    NATIONALITY("nationality", 0),
    DOCUMENT_ID("document_id", 1),
    EXPIRY_DATE("expiry_date", 2),
    BIRTH_DATE("birth_date", 3);

    companion object {
        fun fromString(value: String) = entries.first { it.value == value }
    }
}

@Composable
fun PassportIdentifier.toLocalizedTitle(): String {
    return when (this) {
        PassportIdentifier.NATIONALITY -> stringResource(R.string.nationality)
        PassportIdentifier.DOCUMENT_ID -> stringResource(R.string.document_number)
        PassportIdentifier.EXPIRY_DATE -> stringResource(R.string.expiry_date)
        PassportIdentifier.BIRTH_DATE -> stringResource(R.string.birth_date)
    }
}

fun PassportIdentifier.toLocalizedValue(passport: EDocument): String {
    return when (this) {
        PassportIdentifier.NATIONALITY -> passport.personDetails?.nationality ?: ""
        PassportIdentifier.DOCUMENT_ID -> passport.personDetails?.serialNumber ?: ""
        PassportIdentifier.EXPIRY_DATE -> DateUtil.formatDateString(passport.personDetails?.expiryDate)
        PassportIdentifier.BIRTH_DATE -> DateUtil.formatDateString(passport.personDetails?.birthDate)
    }
}

fun PassportIdentifier.toTitleStub(): String {
    return when (this) {
        PassportIdentifier.NATIONALITY -> "•••••••••"
        PassportIdentifier.DOCUMENT_ID -> "•••••••••••"
        PassportIdentifier.EXPIRY_DATE -> "•••••••••"
        PassportIdentifier.BIRTH_DATE -> "•••••••••"
    }
}

fun PassportIdentifier.toValueStub(): String {
    return when (this) {
        PassportIdentifier.NATIONALITY -> "•••"
        PassportIdentifier.DOCUMENT_ID -> "••••••••"
        PassportIdentifier.EXPIRY_DATE -> "••••••"
        PassportIdentifier.BIRTH_DATE -> "••••••"
    }
}