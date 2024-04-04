package com.distributedLab.rarime.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme


enum class ProcessingStatus {
    PROCESSING,
    SUCCESS,
    FAILURE
}

@Composable
fun ProcessingChip(status: ProcessingStatus) {
    val bgColor by animateColorAsState(
        targetValue = when (status) {
            ProcessingStatus.PROCESSING -> RarimeTheme.colors.warningLighter
            ProcessingStatus.SUCCESS -> RarimeTheme.colors.successLighter
            ProcessingStatus.FAILURE -> RarimeTheme.colors.errorLighter
        },
        label = ""
    )

    val contentColor by animateColorAsState(
        targetValue = when (status) {
            ProcessingStatus.PROCESSING -> RarimeTheme.colors.warningDark
            ProcessingStatus.SUCCESS -> RarimeTheme.colors.successDark
            ProcessingStatus.FAILURE -> RarimeTheme.colors.errorMain
        },
        label = ""
    )

    val text = when (status) {
        ProcessingStatus.PROCESSING -> "PROCESSING"
        ProcessingStatus.SUCCESS -> "DONE"
        ProcessingStatus.FAILURE -> "FAILED"
    }

    val icon = when (status) {
        ProcessingStatus.PROCESSING -> null
        ProcessingStatus.SUCCESS -> R.drawable.ic_check
        ProcessingStatus.FAILURE -> R.drawable.ic_close
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(bgColor, CircleShape)
            .height(24.dp)
            .padding(horizontal = 8.dp)
    ) {
        icon?.let {
            AppIcon(
                id = it,
                size = 16.dp,
                tint = contentColor
            )
        }
        Text(
            text = text,
            style = RarimeTheme.typography.overline3,
            color = contentColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProcessingChipPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ProcessingChip(ProcessingStatus.PROCESSING)
        ProcessingChip(ProcessingStatus.SUCCESS)
        ProcessingChip(ProcessingStatus.FAILURE)
    }
}