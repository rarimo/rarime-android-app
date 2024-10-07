package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class SnackbarSeverity {
    Success,
    Warning,
    Error;

    @Composable
    fun containerColor(): Color = when (this) {
        Success -> RarimeTheme.colors.successDark
        Warning -> RarimeTheme.colors.warningMain
        Error -> RarimeTheme.colors.errorMain
    }

    fun icon(): Int = when (this) {
        Success -> R.drawable.ic_check
        Warning -> R.drawable.ic_warning
        Error -> R.drawable.ic_info
    }

    @Composable
    fun iconTint(): Color = when (this) {
        Success -> RarimeTheme.colors.baseWhite
        Warning -> RarimeTheme.colors.baseWhite
        Error -> RarimeTheme.colors.baseWhite
    }

    @Composable
    fun defaultTitle(): String = when (this) {
        Success -> stringResource(id = R.string.snackbar_success_title)
        Warning -> stringResource(id = R.string.snackbar_warning_title)
        Error -> stringResource(id = R.string.snackbar_error_title)
    }

    @Composable
    fun defaultMessage(): String = when (this) {
        Success -> stringResource(id = R.string.snackbar_success_message)
        Warning -> stringResource(id = R.string.snackbar_warning_message)
        Error -> stringResource(id = R.string.snackbar_error_message)
    }

    @Composable
    fun textColor(): Color = when (this) {
        Success -> RarimeTheme.colors.baseWhite
        Warning -> RarimeTheme.colors.baseWhite
        Error -> RarimeTheme.colors.baseWhite
    }
}

data class SnackbarShowOptions(
    val severity: SnackbarSeverity?,
    val containerColor: Color?,
    val title: String?,
    val message: String?,
    val textColor: Color?,
    val icon: Int?,
    val iconTint: Color?,
    val duration: SnackbarDuration,
)

fun getSnackbarDefaultShowOptions(
    severity: SnackbarSeverity? = null,
    containerColor: Color? = null,
    title: String? = null,
    message: String? = null,
    textColor: Color? = null,
    icon: Int? = null,
    iconTint: Color? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarShowOptions {
    return SnackbarShowOptions(
        severity = severity,
        containerColor = containerColor,
        title = title,
        message = message,
        textColor = textColor,
        icon = icon,
        iconTint = iconTint,
        duration = duration,
    )
}

@Composable
fun UiSnackbarDefault(
    options: SnackbarShowOptions = getSnackbarDefaultShowOptions()
) {
    val severity = options.severity ?: SnackbarSeverity.Success
    val containerColor = options.containerColor ?: severity.containerColor()
    val title = options.title ?: severity.defaultTitle()
    val message = options.message ?: severity.defaultMessage()
    val textColor = options.textColor ?: severity.textColor()
    val icon = options.icon ?: severity.icon()
    val iconTint = options.iconTint ?: severity.iconTint()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppIcon(id = icon, tint = iconTint, size = 20.dp)

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle4,
                color = textColor
            )

            Text(
                text = message,
                style = RarimeTheme.typography.body4,
                color = textColor.copy(alpha = 0.64f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UiSnackbarDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UiSnackbarDefault()
        UiSnackbarDefault(
            options = getSnackbarDefaultShowOptions(
                severity = SnackbarSeverity.Warning
            )
        )
        UiSnackbarDefault(
            options = getSnackbarDefaultShowOptions(
                severity = SnackbarSeverity.Error
            )
        )
    }
}