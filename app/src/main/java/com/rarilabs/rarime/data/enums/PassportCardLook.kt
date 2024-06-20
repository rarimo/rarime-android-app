package com.rarilabs.rarime.data.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class PassportCardLook(val value: Int) {
    WHITE(0),
    BLACK(1),
    GREEN(2);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}

@Composable
fun PassportCardLook.getBackgroundColor(): Color {
    return when (this) {
        PassportCardLook.GREEN -> RarimeTheme.colors.primaryMain
        PassportCardLook.BLACK -> RarimeTheme.colors.baseBlack
        PassportCardLook.WHITE -> RarimeTheme.colors.baseWhite
    }
}

@Composable
fun PassportCardLook.getForegroundColor(): Color {
    return when (this) {
        PassportCardLook.GREEN -> RarimeTheme.colors.baseBlack
        PassportCardLook.BLACK -> RarimeTheme.colors.baseWhite
        PassportCardLook.WHITE -> RarimeTheme.colors.baseBlack
    }
}

@Composable
fun PassportCardLook.getTitle(): String {
    return when (this) {
        PassportCardLook.GREEN -> stringResource(R.string.passport_look_green)
        PassportCardLook.BLACK -> stringResource(R.string.passport_look_black)
        PassportCardLook.WHITE -> stringResource(R.string.passport_look_white)
    }
}
