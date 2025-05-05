package com.rarilabs.rarime.modules.digitalLikeness

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.GifViewer
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay


enum class ProcessingStatus(val title: String) {
    DOWNLOADING("Downloading circuit data"),
    EXTRACTING_FEATURES("Extracting image features"),
    RUNNING_ZKML("Running ZKML"),
    FINSH("")
}

enum class ProcessingItemStatus {
    FINISHED, LOADING, NOT_ACTIVE
}

@Composable
fun DigitalLikenessProcessing(
    modifier: Modifier = Modifier,
    processing: () -> Unit,
    onNext: () -> Unit
) {

    var currentStep: ProcessingStatus by remember { mutableStateOf(ProcessingStatus.DOWNLOADING) }

    LaunchedEffect(Unit) {
        processing()
    }

    var currentProgress: Float by remember {
        mutableFloatStateOf(0.0f)
    }

    LaunchedEffect(Unit) {
        while (true) {
            for (i in 0..100) {
                currentProgress = i.toFloat() / 100f
                delay(20)

                if (i == 100) {
                    when (currentStep) {
                        ProcessingStatus.DOWNLOADING -> {
                            currentStep = ProcessingStatus.EXTRACTING_FEATURES
                        }

                        ProcessingStatus.EXTRACTING_FEATURES -> {
                            currentStep = ProcessingStatus.RUNNING_ZKML
                        }

                        ProcessingStatus.RUNNING_ZKML -> {
                            currentStep = ProcessingStatus.FINSH
                        }

                        ProcessingStatus.FINSH -> {

                        }
                    }
                }
            }
            if (currentStep == ProcessingStatus.FINSH) {
                //onNext()
                break
            }
        }
    }

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .background(RarimeTheme.colors.backgroundPrimary)
            .then(modifier)
    ) {


        GifViewer(
            gifId = R.raw.likeness_processing,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Please wait",
            color = RarimeTheme.colors.textPrimary,
            style = RarimeTheme.typography.h1
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center,
            text = currentStep.title,
            color = RarimeTheme.colors.textSecondary,
            style = RarimeTheme.typography.body3
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in ProcessingStatus.entries) {

                if (i == ProcessingStatus.FINSH)
                    continue

                val isFinished =
                    i.ordinal < currentStep.ordinal      // step comes before current = done
                val isProcessing = i.ordinal == currentStep.ordinal   // current step
                val isNotStarted = i.ordinal > currentStep.ordinal

                val currentStatus = when {
                    isFinished -> ProcessingItemStatus.FINISHED
                    isProcessing -> ProcessingItemStatus.LOADING
                    isNotStarted -> ProcessingItemStatus.NOT_ACTIVE
                    else -> throw IllegalStateException()
                }

                when (currentStatus) {
                    ProcessingItemStatus.FINISHED -> ProcessItemFinished(title = i.title)
                    ProcessingItemStatus.LOADING -> ProcessItemLoading(
                        title = i.title,
                        progress = currentProgress
                    )

                    ProcessingItemStatus.NOT_ACTIVE -> ProcessItemNotActive(
                        title = i.title
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

    }
}

@Composable
fun ProcessItemLoading(
    modifier: Modifier = Modifier,
    title: String,
    progress: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    progress.coerceIn(
                        0f, 1f
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
                .border(
                    1.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = RarimeTheme.colors.componentPrimary
                )
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

            Text(
                text = "${(progress * 100).toInt()}%",
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textPrimary
            )

        }
    }
}

@Composable
fun ProcessItemFinished(
    modifier: Modifier = Modifier,
    title: String,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    1f
                )
                .height(62.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(RarimeTheme.colors.successLighter)
                .align(Alignment.CenterStart)
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .border(
                    1.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = RarimeTheme.colors.componentPrimary
                )
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.successDark
            )


            AppIcon(id = R.drawable.ic_check, tint = RarimeTheme.colors.successDarker)

        }
    }
}


@Composable
fun ProcessItemNotActive(
    modifier: Modifier = Modifier,
    title: String,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    0f
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
                .border(
                    1.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = RarimeTheme.colors.componentPrimary
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textSecondary
            )
        }
    }
}

@Preview
@Composable
private fun DigitalLikenessProcessingPreview() {
    Surface {
        DigitalLikenessProcessing(
            modifier = Modifier,
            onNext = {

            },
            processing = {}
        )
    }
}
