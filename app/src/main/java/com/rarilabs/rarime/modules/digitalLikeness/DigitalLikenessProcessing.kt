package com.rarilabs.rarime.modules.digitalLikeness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay


private enum class ProcessingStatus {
    DOWNLOADING, EXTRACTING_FEATURES, RUNNING_ZKML, OVERRIDING_ACCESS
}

private enum class ProcessingItemStatus {
    FINISHED,
    LOADING,
    NOT_ACTIVE
}

@Composable
fun DigitalLikenessProcessing(modifier: Modifier = Modifier) {

    var currentStep: ProcessingStatus by remember { mutableStateOf(ProcessingStatus.DOWNLOADING) }

    var currentProgress: Float by remember {
        mutableFloatStateOf(0.0f)
    }

    LaunchedEffect(Unit) {
        for (i in 0..100) {
            currentProgress = i.toFloat() / 100f
            delay(100)
            if (i == 100) {

                if (currentStep == ProcessingStatus.OVERRIDING_ACCESS) {
                    break
                }

                when (currentStep) {
                    ProcessingStatus.DOWNLOADING -> {
                        currentStep = ProcessingStatus.EXTRACTING_FEATURES
                    }

                    ProcessingStatus.EXTRACTING_FEATURES -> {
                        currentStep = ProcessingStatus.RUNNING_ZKML
                    }

                    ProcessingStatus.RUNNING_ZKML -> {
                        currentStep = ProcessingStatus.OVERRIDING_ACCESS
                    }

                    ProcessingStatus.OVERRIDING_ACCESS -> {}
                }

            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        Text(
            text = "Please wait",
            color = RarimeTheme.colors.textPrimary,
            style = RarimeTheme.typography.h1
        )


        for (i in ProcessingStatus.entries) {
            val isFinished = currentStep.ordinal > i.ordinal

            val isProcessng = currentStep.ordinal == i.ordinal

            val isNotStarted = currentStep.ordinal < i.ordinal

            val currentStatus = when {
                isFinished -> ProcessingItemStatus.FINISHED
                isProcessng -> ProcessingItemStatus.LOADING
                isNotStarted -> ProcessingItemStatus.NOT_ACTIVE
                else -> throw IllegalStateException()
            }


            ProcessItem()
        }

    }
}

@Composable
fun ProcessItem(
    modifier: Modifier = Modifier,
    title: String,
    progress: Float,
    status: ProcessingItemStatus
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    if (isFinished) 1f else progress.coerceIn(
                        0f,
                        1f
                    )
                )
                .height(62.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(RarimeTheme.colors.componentPrimary)
                .align(Alignment.CenterStart)
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textPrimary
            )
            if (isFinished) {
                AppIcon(id = R.drawable.ic_check)
            } else {
                Text(
                    text = "${(progress * 100).toInt()}%", style = RarimeTheme.typography.subtitle5,
                    color = RarimeTheme.colors.textPrimary
                )
            }
        }
    }
}

@Preview
@Composable
private fun DigitalLikenessProcessingPreview() {
    DigitalLikenessProcessing()
}

@Preview
@Composable
private fun ProcessItemPreview() {
    Surface {
        ProcessItem(
            modifier = Modifier,
            title = "Downloading circuit data",
            progress = 0.1f,
            isFinished = true
        )
    }
}