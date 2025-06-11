package com.rarilabs.rarime.data.enums

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rarilabs.rarime.R

enum class AppIcon(val activity: String, @DrawableRes val iconId: Int) {
    BLACK(
        activity = "com.rarilabs.rarime.MainActivityBlack",
        iconId = R.drawable.ic_logo_black
    ),
    WHITE(
        activity = "com.rarilabs.rarime.MainActivityGB",
        iconId = R.drawable.logo_bw
    ),
    GREEN(
        activity = "com.rarilabs.rarime.MainActivityBW",
        iconId = R.drawable.ic_logo_green
    ),
    GRADIENT(
        activity = "com.rarilabs.rarime.MainActivityBG",
        iconId = R.drawable.ic_logo_gradient
    ),
    CAT(
        activity = "com.rarilabs.rarime.MainActivityCat",
        iconId = R.drawable.ic_logo_cat
    )


}

@Composable
fun AppIcon.toLocalizedString(): String {
    return when (this) {
        AppIcon.WHITE -> stringResource(R.string.icon_white)
        AppIcon.BLACK -> stringResource(R.string.icon_black)
        AppIcon.GREEN -> stringResource(R.string.icon_green)
        AppIcon.GRADIENT -> stringResource(R.string.icon_gradient)
        AppIcon.CAT -> stringResource(R.string.icon_cat)
    }
}

@Composable
fun AppIcon.getInAppIcon(): Int {
    return when (this) {
        AppIcon.WHITE -> R.drawable.app_icon_white
        AppIcon.BLACK -> R.drawable.app_icon_black
        AppIcon.GREEN -> R.drawable.app_icon_green
        AppIcon.GRADIENT -> R.drawable.app_icon_gradeint
        AppIcon.CAT -> R.drawable.app_icon_cat
    }
}