package com.distributedLab.rarime.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color

@Stable
class RarimeColors(
    // primary
    primaryDarker: Color,
    primaryDark: Color,
    primaryMain: Color,
    primaryLight: Color,
    primaryLighter: Color,

    // secondary
    secondaryDarker: Color,
    secondaryDark: Color,
    secondaryMain: Color,
    secondaryLight: Color,
    secondaryLighter: Color,

    // success
    successDarker: Color,
    successDark: Color,
    successMain: Color,
    successLight: Color,
    successLighter: Color,

    // error
    errorDarker: Color,
    errorDark: Color,
    errorMain: Color,
    errorLight: Color,
    errorLighter: Color,

    // warning
    warningDarker: Color,
    warningDark: Color,
    warningMain: Color,
    warningLight: Color,
    warningLighter: Color,

    // text
    textPrimary: Color,
    textSecondary: Color,
    textPlaceholder: Color,
    textDisabled: Color,

    // component
    componentPrimary: Color,
    componentHovered: Color,
    componentPressed: Color,
    componentSelected: Color,
    componentDisabled: Color,

    // background
    backgroundPrimary: Color,
    backgroundOpacity: Color,
    backgroundPure: Color,

    // base
    baseBlack: Color,
    baseWhite: Color,

    // additional
    additionalLayerBorder: Color,
    additionalPureDark: Color,
) {
    var primaryDarker by mutableStateOf(primaryDarker, structuralEqualityPolicy())
        internal set
    var primaryDark by mutableStateOf(primaryDark, structuralEqualityPolicy())
        internal set
    var primaryMain by mutableStateOf(primaryMain, structuralEqualityPolicy())
        internal set
    var primaryLight by mutableStateOf(primaryLight, structuralEqualityPolicy())
        internal set
    var primaryLighter by mutableStateOf(primaryLighter, structuralEqualityPolicy())
        internal set
    var secondaryDarker by mutableStateOf(secondaryDarker, structuralEqualityPolicy())
        internal set
    var secondaryDark by mutableStateOf(secondaryDark, structuralEqualityPolicy())
        internal set
    var secondaryMain by mutableStateOf(secondaryMain, structuralEqualityPolicy())
        internal set
    var secondaryLight by mutableStateOf(secondaryLight, structuralEqualityPolicy())
        internal set
    var secondaryLighter by mutableStateOf(secondaryLighter, structuralEqualityPolicy())
        internal set
    var successDarker by mutableStateOf(successDarker, structuralEqualityPolicy())
        internal set
    var successDark by mutableStateOf(successDark, structuralEqualityPolicy())
        internal set
    var successMain by mutableStateOf(successMain, structuralEqualityPolicy())
        internal set
    var successLight by mutableStateOf(successLight, structuralEqualityPolicy())
        internal set
    var successLighter by mutableStateOf(successLighter, structuralEqualityPolicy())
        internal set
    var errorDarker by mutableStateOf(errorDarker, structuralEqualityPolicy())
        internal set
    var errorDark by mutableStateOf(errorDark, structuralEqualityPolicy())
        internal set
    var errorMain by mutableStateOf(errorMain, structuralEqualityPolicy())
        internal set
    var errorLight by mutableStateOf(errorLight, structuralEqualityPolicy())
        internal set
    var errorLighter by mutableStateOf(errorLighter, structuralEqualityPolicy())
        internal set
    var warningDarker by mutableStateOf(warningDarker, structuralEqualityPolicy())
        internal set
    var warningDark by mutableStateOf(warningDark, structuralEqualityPolicy())
        internal set
    var warningMain by mutableStateOf(warningMain, structuralEqualityPolicy())
        internal set
    var warningLight by mutableStateOf(warningLight, structuralEqualityPolicy())
        internal set
    var warningLighter by mutableStateOf(warningLighter, structuralEqualityPolicy())
        internal set
    var textPrimary by mutableStateOf(textPrimary, structuralEqualityPolicy())
        internal set
    var textSecondary by mutableStateOf(textSecondary, structuralEqualityPolicy())
        internal set
    var textPlaceholder by mutableStateOf(textPlaceholder, structuralEqualityPolicy())
        internal set
    var textDisabled by mutableStateOf(textDisabled, structuralEqualityPolicy())
        internal set
    var componentPrimary by mutableStateOf(componentPrimary, structuralEqualityPolicy())
        internal set
    var componentHovered by mutableStateOf(componentHovered, structuralEqualityPolicy())
        internal set
    var componentPressed by mutableStateOf(componentPressed, structuralEqualityPolicy())
        internal set
    var componentSelected by mutableStateOf(componentSelected, structuralEqualityPolicy())
        internal set
    var componentDisabled by mutableStateOf(componentDisabled, structuralEqualityPolicy())
        internal set
    var backgroundPrimary by mutableStateOf(backgroundPrimary, structuralEqualityPolicy())
        internal set
    var backgroundOpacity by mutableStateOf(backgroundOpacity, structuralEqualityPolicy())
        internal set
    var backgroundPure by mutableStateOf(backgroundPure, structuralEqualityPolicy())
        internal set
    var baseBlack by mutableStateOf(baseBlack, structuralEqualityPolicy())
        internal set
    var baseWhite by mutableStateOf(baseWhite, structuralEqualityPolicy())
        internal set
    var additionalLayerBorder by mutableStateOf(additionalLayerBorder, structuralEqualityPolicy())
        internal set
    var additionalPureDark by mutableStateOf(additionalPureDark, structuralEqualityPolicy())
        internal set

    fun copy(
        primaryDarker: Color = this.primaryDarker,
        primaryDark: Color = this.primaryDark,
        primaryMain: Color = this.primaryMain,
        primaryLight: Color = this.primaryLight,
        primaryLighter: Color = this.primaryLighter,
        secondaryDarker: Color = this.secondaryDarker,
        secondaryDark: Color = this.secondaryDark,
        secondaryMain: Color = this.secondaryMain,
        secondaryLight: Color = this.secondaryLight,
        secondaryLighter: Color = this.secondaryLighter,
        successDarker: Color = this.successDarker,
        successDark: Color = this.successDark,
        successMain: Color = this.successMain,
        successLight: Color = this.successLight,
        successLighter: Color = this.successLighter,
        errorDarker: Color = this.errorDarker,
        errorDark: Color = this.errorDark,
        errorMain: Color = this.errorMain,
        errorLight: Color = this.errorLight,
        errorLighter: Color = this.errorLighter,
        warningDarker: Color = this.warningDarker,
        warningDark: Color = this.warningDark,
        warningMain: Color = this.warningMain,
        warningLight: Color = this.warningLight,
        warningLighter: Color = this.warningLighter,
        textPrimary: Color = this.textPrimary,
        textSecondary: Color = this.textSecondary,
        textPlaceholder: Color = this.textPlaceholder,
        textDisabled: Color = this.textDisabled,
        componentPrimary: Color = this.componentPrimary,
        componentHovered: Color = this.componentHovered,
        componentPressed: Color = this.componentPressed,
        componentSelected: Color = this.componentSelected,
        componentDisabled: Color = this.componentDisabled,
        backgroundPrimary: Color = this.backgroundPrimary,
        backgroundOpacity: Color = this.backgroundOpacity,
        backgroundPure: Color = this.backgroundPure,
        baseBlack: Color = this.baseBlack,
        baseWhite: Color = this.baseWhite,
        additionalLayerBorder: Color = this.additionalLayerBorder,
        additionalPureDark: Color = this.additionalPureDark,
    ) = RarimeColors(
        primaryDarker = primaryDarker,
        primaryDark = primaryDark,
        primaryMain = primaryMain,
        primaryLight = primaryLight,
        primaryLighter = primaryLighter,
        secondaryDarker = secondaryDarker,
        secondaryDark = secondaryDark,
        secondaryMain = secondaryMain,
        secondaryLight = secondaryLight,
        secondaryLighter = secondaryLighter,
        successDarker = successDarker,
        successDark = successDark,
        successMain = successMain,
        successLight = successLight,
        successLighter = successLighter,
        errorDarker = errorDarker,
        errorDark = errorDark,
        errorMain = errorMain,
        errorLight = errorLight,
        errorLighter = errorLighter,
        warningDarker = warningDarker,
        warningDark = warningDark,
        warningMain = warningMain,
        warningLight = warningLight,
        warningLighter = warningLighter,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textPlaceholder = textPlaceholder,
        textDisabled = textDisabled,
        componentPrimary = componentPrimary,
        componentHovered = componentHovered,
        componentPressed = componentPressed,
        componentSelected = componentSelected,
        componentDisabled = componentDisabled,
        backgroundPrimary = backgroundPrimary,
        backgroundOpacity = backgroundOpacity,
        backgroundPure = backgroundPure,
        baseBlack = baseBlack,
        baseWhite = baseWhite,
        additionalLayerBorder = additionalLayerBorder,
        additionalPureDark = additionalPureDark,
    )

    fun updateColorsFrom(other: RarimeColors) {
        this.primaryDarker = other.primaryDarker
        this.primaryDark = other.primaryDark
        this.primaryMain = other.primaryMain
        this.primaryLight = other.primaryLight
        this.primaryLighter = other.primaryLighter
        this.secondaryDarker = other.secondaryDarker
        this.secondaryDark = other.secondaryDark
        this.secondaryMain = other.secondaryMain
        this.secondaryLight = other.secondaryLight
        this.secondaryLighter = other.secondaryLighter
        this.successDarker = other.successDarker
        this.successDark = other.successDark
        this.successMain = other.successMain
        this.successLight = other.successLight
        this.successLighter = other.successLighter
        this.errorDarker = other.errorDarker
        this.errorDark = other.errorDark
        this.errorMain = other.errorMain
        this.errorLight = other.errorLight
        this.errorLighter = other.errorLighter
        this.warningDarker = other.warningDarker
        this.warningDark = other.warningDark
        this.warningMain = other.warningMain
        this.warningLight = other.warningLight
        this.warningLighter = other.warningLighter
        this.textPrimary = other.textPrimary
        this.textSecondary = other.textSecondary
        this.textPlaceholder = other.textPlaceholder
        this.textDisabled = other.textDisabled
        this.componentPrimary = other.componentPrimary
        this.componentHovered = other.componentHovered
        this.componentPressed = other.componentPressed
        this.componentSelected = other.componentSelected
        this.componentDisabled = other.componentDisabled
        this.backgroundPrimary = other.backgroundPrimary
        this.backgroundOpacity = other.backgroundOpacity
        this.backgroundPure = other.backgroundPure
        this.baseBlack = other.baseBlack
        this.baseWhite = other.baseWhite
        this.additionalLayerBorder = other.additionalLayerBorder
        this.additionalPureDark = other.additionalPureDark
    }
}

fun darkColors() = RarimeColors(
    baseBlack = Color(0xFF202020),
    baseWhite = Color(0xFFFFFFFF),
    primaryDarker = Color(0xFFDDFE84),
    primaryDark = Color(0xFFD5FD67),
    primaryMain = Color(0xFFCDFD4A),
    primaryLight = Color(0x1ACDFD4A),
    primaryLighter = Color(0x0DCDFD4A),
    secondaryDarker = Color(0xFF676767),
    secondaryDark = Color(0xFF444444),
    secondaryMain = Color(0xFF202020),
    secondaryLight = Color(0xFF1B1B1B),
    secondaryLighter = Color(0xFF111111),
    successDarker = Color(0xFF78D9B6),
    successDark = Color(0xFF58D0A4),
    successMain = Color(0xFF6CF1C1),
    successLight = Color(0x1A38C793),
    successLighter = Color(0x0D38C793),
    errorDarker = Color(0xFFE9657E),
    errorDark = Color(0xFFE4405F),
    errorMain = Color(0xFFF54667),
    errorLight = Color(0x1ADF1C41),
    errorLighter = Color(0x0DDF1C41),
    warningDarker = Color(0xFFF5A570),
    warningDark = Color(0xFFF3904E),
    warningMain = Color(0xFFFDA366),
    warningLight = Color(0x1AF17B2C),
    warningLighter = Color(0x0DF17B2C),
    textPrimary = Color(0xE5FFFFFF),
    textSecondary = Color(0x8FFFFFFF),
    textPlaceholder = Color(0x70FFFFFF),
    textDisabled = Color(0x47FFFFFF),
    componentPrimary = Color(0x0DFFFFFF),
    componentHovered = Color(0x1AFFFFFF),
    componentPressed = Color(0x26FFFFFF),
    componentSelected = Color(0x0DFFFFFF),
    componentDisabled = Color(0x0DFFFFFF),
    backgroundPrimary = Color(0xFF111111),
    backgroundOpacity = Color(0x0DFFFFFF),
    backgroundPure = Color(0xFF272727),
    additionalLayerBorder = Color(0x0DFFFFFF),
    additionalPureDark = Color(0x1AFFFFFF),
)

fun lightColors() = RarimeColors(
    baseBlack = Color(0xFF202020),
    baseWhite = Color(0xFFFFFFFF),
    primaryDarker = Color(0xFF8BAC32),
    primaryDark = Color(0xFFACD53E),
    primaryMain = Color(0xFFCDFD4A),
    primaryLight = Color(0xFFE5FEA1),
    primaryLighter = Color(0xFFF5FFDB),
    secondaryDarker = Color(0xFF0C0C0C),
    secondaryDark = Color(0xFF161616),
    secondaryMain = Color(0xFF202020),
    secondaryLight = Color(0xFFD2D2D2),
    secondaryLighter = Color(0xFFF4F4F4),
    successDarker = Color(0xFF268764),
    successDark = Color(0xFF2FA77B),
    successMain = Color(0xFF38C793),
    successLight = Color(0xFFC7EFE1),
    successLighter = Color(0xFFEBF9F4),
    errorDarker = Color(0xFF98132C),
    errorDark = Color(0xFFBB1837),
    errorMain = Color(0xFFDF1C41),
    errorLight = Color(0xFFF9D2D9),
    errorLighter = Color(0xFFFDEFF2),
    warningDarker = Color(0xFFA4541E),
    warningDark = Color(0xFFCA6725),
    warningMain = Color(0xFFF17B2C),
    warningLight = Color(0x1AF17B2C),
    warningLighter = Color(0x0DF17B2C),
    textPrimary = Color(0xFF202020),
    textSecondary = Color(0x8F202020),
    textPlaceholder = Color(0x70202020),
    textDisabled = Color(0x47202020),
    componentPrimary = Color(0x0D202020),
    componentHovered = Color(0x1A202020),
    componentPressed = Color(0x26202020),
    componentSelected = Color(0x0D202020),
    componentDisabled = Color(0x0D202020),
    backgroundPrimary = Color(0xFFEFEFEF),
    backgroundOpacity = Color(0xB2FFFFFF),
    backgroundPure = Color(0xFFFFFFFF),
    additionalLayerBorder = Color(0xFFFFFFFF),
    additionalPureDark = Color(0xFF262626),
)

val LocalColors = staticCompositionLocalOf { lightColors() }
