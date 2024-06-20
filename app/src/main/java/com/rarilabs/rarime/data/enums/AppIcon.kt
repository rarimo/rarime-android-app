package com.rarilabs.rarime.data.enums

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rarilabs.rarime.R

enum class AppIcon(val activity: String, @DrawableRes val iconId: Int) {
    BLACK_AND_WHITE(
        activity = "com.distributedLab.rarime.MainActivityBW",
        iconId = R.drawable.logo_bw
    ),
    BLACK_AND_GREEN(
        activity = "com.distributedLab.rarime.MainActivityBG",
        iconId = R.drawable.logo_bg
    ),
    GREEN_AND_BLACK(
        activity = "com.distributedLab.rarime.MainActivityGB",
        iconId = R.drawable.logo_gb
    ),
}

@Composable
fun AppIcon.toLocalizedString(): String {
    return when (this) {
        AppIcon.BLACK_AND_WHITE -> stringResource(R.string.icon_black_and_white)
        AppIcon.BLACK_AND_GREEN -> stringResource(R.string.icon_black_and_green)
        AppIcon.GREEN_AND_BLACK -> stringResource(R.string.icon_green_and_black)
    }
}