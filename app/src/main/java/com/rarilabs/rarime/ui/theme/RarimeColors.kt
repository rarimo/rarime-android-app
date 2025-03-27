    package com.rarilabs.rarime.ui.theme

    import androidx.compose.runtime.Stable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.setValue
    import androidx.compose.runtime.staticCompositionLocalOf
    import androidx.compose.runtime.structuralEqualityPolicy
    import androidx.compose.ui.graphics.Brush
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

        // info
        infoDarker: Color,
        infoDark: Color,
        infoMain: Color,
        infoLight: Color,
        infoLighter: Color,

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
        backgroundContainer: Color,
        backgroundBlur: Color,
        backgroundSurface1: Color,
        backgroundSurface2: Color,
        backgroundPure: Color,

        // base
        baseBlack: Color,
        baseWhite: Color,

        // inverted
        invertedDark: Color,
        invertedLight: Color,

        // additional
        gradient1: Brush,
        gradient2: Brush,
        gradient3: Brush,
        gradient4: Brush,
        gradient5: Brush,
        gradient6: Brush,
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
        var infoDarker by mutableStateOf(infoDarker, structuralEqualityPolicy())
            internal set
        var infoDark by mutableStateOf(infoDark, structuralEqualityPolicy())
            internal set
        var infoMain by mutableStateOf(infoMain, structuralEqualityPolicy())
            internal set
        var infoLight by mutableStateOf(infoLight, structuralEqualityPolicy())
            internal set
        var infoLighter by mutableStateOf(infoLighter, structuralEqualityPolicy())
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
        var backgroundBlur by mutableStateOf(backgroundBlur, structuralEqualityPolicy())
            internal set
        var backgroundSurface1 by mutableStateOf(backgroundSurface1, structuralEqualityPolicy())
            internal set
        var backgroundSurface2 by mutableStateOf(backgroundSurface2, structuralEqualityPolicy())
            internal set
        var backgroundContainer by mutableStateOf(backgroundPrimary, structuralEqualityPolicy())
            internal set
        var backgroundPure by mutableStateOf(backgroundPure, structuralEqualityPolicy())
            internal set
        var baseBlack by mutableStateOf(baseBlack, structuralEqualityPolicy())
            internal set
        var baseWhite by mutableStateOf(baseWhite, structuralEqualityPolicy())
            internal set
        var invertedDark by mutableStateOf(invertedDark, structuralEqualityPolicy())
            internal set
        var invertedLight by mutableStateOf(invertedLight, structuralEqualityPolicy())
            internal set
        var gradient1 by mutableStateOf(gradient1, structuralEqualityPolicy())
            internal set

        var gradient2 by mutableStateOf(gradient2, structuralEqualityPolicy())
            internal set

        var gradient3 by mutableStateOf(gradient3, structuralEqualityPolicy())
            internal set

        var gradient4 by mutableStateOf(gradient4, structuralEqualityPolicy())
            internal set

        var gradient5 by mutableStateOf(gradient5, structuralEqualityPolicy())
            internal set

        var gradient6 by mutableStateOf(gradient6, structuralEqualityPolicy())
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
            infoDarker: Color = this.infoDarker,
            infoDark: Color = this.infoDark,
            infoMain: Color = this.infoMain,
            infoLight: Color = this.infoLight,
            infoLighter: Color = this.infoLighter,
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
            backgroundPure: Color = this.backgroundPure,
            baseBlack: Color = this.baseBlack,
            baseWhite: Color = this.baseWhite,
            backgroundContainer: Color = this.backgroundContainer,
            gradient1: Brush = this.gradient1,
            gradient2: Brush = this.gradient2,
            gradient3: Brush = this.gradient3,
            gradient4: Brush = this.gradient4,
            gradient5: Brush = this.gradient5,
            gradient6: Brush = this.gradient6,

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
            infoDarker = infoDarker,
            infoDark = infoDark,
            infoMain = infoMain,
            infoLight = infoLight,
            infoLighter = infoLighter,
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
            backgroundBlur = backgroundBlur,
            backgroundSurface1 = backgroundSurface1,
            backgroundSurface2 = backgroundSurface2,
            backgroundPure = backgroundPure,
            baseBlack = baseBlack,
            baseWhite = baseWhite,
            invertedDark = invertedDark,
            invertedLight = invertedLight,
            backgroundContainer = backgroundContainer,
            gradient1 = gradient1,
            gradient2 = gradient2,
            gradient3 = gradient3,
            gradient4 = gradient4,
            gradient5 = gradient5,
            gradient6 = gradient6
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
            this.backgroundContainer = other.backgroundContainer
            this.backgroundPure = other.backgroundPure
            this.baseBlack = other.baseBlack
            this.baseWhite = other.baseWhite
        }
    }

    fun darkColors() = RarimeColors(
        // primary
        primaryDarker = Color(0xFFFFFFFF),
        primaryDark = Color(0xFFFFFFFF),
        primaryMain = Color(0xFFFFFFFF),
        primaryLight = Color(0x1FFFFFFF),
        primaryLighter = Color(0x0FFFFFFF),

        // secondary
        secondaryDarker = Color(0xFFA8E152),
        secondaryDark = Color(0xFF99D838),
        secondaryMain = Color(0xFF8CCD28),
        secondaryLight = Color(0x1F8CCD28),
        secondaryLighter = Color(0x0F8CCD28),

        // success
        successDarker = Color(0xFF4AD07B),
        successDark = Color(0xFF3DD073),
        successMain = Color(0xFF37CF6F),
        successLight = Color(0x1F37CF6F),
        successLighter = Color(0x0F37CF6F),

        // info
        infoDarker = Color(0xFF5D97F5),
        infoDark = Color(0xFF4788F1),
        infoMain = Color(0xFF367BEC),
        infoLight = Color(0x1F367BEC),
        infoLighter = Color(0x0F367BEC),

        // error
        errorDarker = Color(0xFFEE6565),
        errorDark = Color(0xFFE65454),
        errorMain = Color(0xFFDA4343),
        errorLight = Color(0x1FDA4343),
        errorLighter = Color(0x0FDA4343),

        // warning
        warningDarker = Color(0xFFFBB239),
        warningDark = Color(0xFFF3A728),
        warningMain = Color(0xFFED9E19),
        warningLight = Color(0x1FED9E19),
        warningLighter = Color(0x0FED9E19),

        // text
        textPrimary = Color(0xE5FFFFFF),
        textSecondary = Color(0x8FFFFFFF),
        textPlaceholder = Color(0x70FFFFFF),
        textDisabled = Color(0x47FFFFFF),

        // component
        componentPrimary = Color(0x0DFFFFFF),
        componentHovered = Color(0x1AFFFFFF),
        componentPressed = Color(0x26FFFFFF),
        componentSelected = Color(0x0DFFFFFF),
        componentDisabled = Color(0x0DFFFFFF),

        // background
        backgroundPrimary = Color(0xFF0E0E0E),
        backgroundContainer = Color(0xFF171717),
        backgroundBlur = Color(0xE50E0E0E),
        backgroundPure = Color(0xFF0E0E0E),
        backgroundSurface1 = Color(0xFF272827),
        backgroundSurface2 = Color(0xFF3F403F),

        // base
        baseBlack = Color(0xFF202020),
        baseWhite = Color(0xFFFFFFFF),

        // inverted
        invertedDark = Color(0xFFF3F6F2),
        invertedLight = Color(0xFF141614),

        // additional
        gradient1 = Brush.linearGradient(colors = listOf(Color(0xFF9AFE8A), Color(0xFF8AFECC))),
        gradient2 = Brush.linearGradient(colors = listOf(Color(0xFFF2F8EE), Color(0xFFCBE7EC))),
        gradient3 = Brush.linearGradient(colors = listOf(Color(0xFFDFFCC4), Color(0xFFF4F3F0))),
        gradient4 = Brush.linearGradient(colors = listOf(Color(0xFFD3D1EF), Color(0xFFFCE3FC))),
        gradient5 = Brush.linearGradient(colors = listOf(Color(0xFF80ED99), Color(0xFFD5FEC8))),
        gradient6 = Brush.linearGradient(colors = listOf(Color(0xFF39CDA0), Color(0xFF45C45C)))
    )

    fun lightColors() = RarimeColors(
        // primary
        primaryDarker = Color(0xFF050505),
        primaryDark = Color(0xFF111211),
        primaryMain = Color(0xFF141614),
        primaryLight = Color(0x1416141F),
        primaryLighter = Color(0x1416140F),

        // secondary
        secondaryDarker = Color(0xFF4D7C0F),
        secondaryDark = Color(0xFF65A30D),
        secondaryMain = Color(0xFF84CC16),
        secondaryLight = Color(0x1F84CC16),
        secondaryLighter = Color(0x0F84CC16),

        // success
        successDarker = Color(0xFF15803D),
        successDark = Color(0xFF16A34A),
        successMain = Color(0xFF22C55E),
        successLight = Color(0x1F22C55E),
        successLighter = Color(0x0F22C55E),

        // info
        infoDarker = Color(0xFF1D4ED8),
        infoDark = Color(0xFF2563EB),
        infoMain = Color(0xFF3B82F6),
        infoLight = Color(0x1F3B82F6),
        infoLighter = Color(0x0F3B82F6),

        // error
        errorDarker = Color(0xFFB91C1C),
        errorDark = Color(0xFFDC2626),
        errorMain = Color(0xFFEF4444),
        errorLight = Color(0x1FEF4444),
        errorLighter = Color(0x0FEF4444),

        // warning
        warningDarker = Color(0xFFC09027),
        warningDark = Color(0xFFE1AC3B),
        warningMain = Color(0xFFF59E0B),
        warningLight = Color(0x1FF59E0B),
        warningLighter = Color(0x0FF59E0B),

        // text
        textPrimary = Color(0xFF141614),
        textSecondary = Color(0x8F141614),
        textPlaceholder = Color(0x70141614),
        textDisabled = Color(0x47141614),

        // component
        componentPrimary = Color(0x0D141614),
        componentHovered = Color(0x1A141614),
        componentPressed = Color(0x26141614),
        componentSelected = Color(0x0D141614),
        componentDisabled = Color(0x0D141614),

        // background
        backgroundPrimary = Color(0xFFFFFFFF),
        backgroundContainer = Color(0xFFFFFFFF),
        backgroundBlur = Color(0xE5FFFFFF),
        backgroundPure = Color(0xFFFFFFFF),
        backgroundSurface1 = Color(0xFFFFFFFF),
        backgroundSurface2 = Color(0xFFFFFFFF),

        // base
        baseBlack = Color(0xFF141614),
        baseWhite = Color(0xFFFFFFFF),

        // inverted
        invertedDark = Color(0xFF141614),
        invertedLight = Color(0xFFFFFFFF),

        // additional
        gradient1 = Brush.linearGradient(colors = listOf(Color(0xFF9AFE8A), Color(0xFF8AFECC))),
        gradient2 = Brush.linearGradient(colors = listOf(Color(0xFFF2F8EE), Color(0xFFCBE7EC))),
        gradient3 = Brush.linearGradient(colors = listOf(Color(0xFFDFFCC4), Color(0xFFF4F3F0))),
        gradient4 = Brush.linearGradient(colors = listOf(Color(0xFFD3D1EF), Color(0xFFFCE3FC))),
        gradient5 = Brush.linearGradient(colors = listOf(Color(0xFF80ED99), Color(0xFFD5FEC8))),
        gradient6 = Brush.linearGradient(colors = listOf(Color(0xFF39CDA0), Color(0xFF45C45C))),
    )

    val LocalColors = staticCompositionLocalOf { lightColors() }
