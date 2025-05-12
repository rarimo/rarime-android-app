package com.rarilabs.rarime.modules.digitalLikeness

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.LivenessProcessingStatus
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.GifViewer
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


enum class ProcessingItemStatus {
    FINISHED, LOADING, NOT_ACTIVE, FAILED
}

@Composable
fun DigitalLikenessProcessing(
    modifier: Modifier = Modifier,
    selectedBitmap: Bitmap,
    processing: suspend (Bitmap) -> Unit,
    currentProcessingState: LivenessProcessingStatus,
    currentProcessingError: LivenessProcessingStatus? = null,
    onNext: () -> Unit,
    downloadProgress: Int
) {

    var currentProgress: Float by remember {
        mutableFloatStateOf(0f)
    }

    val retryScope = rememberCoroutineScope()

    LaunchedEffect(downloadProgress) {
        currentProgress = downloadProgress / 100f
    }

    LaunchedEffect(currentProcessingState) {
        when (currentProcessingState) {
            LivenessProcessingStatus.RUNNING_ZKML -> {
                currentProgress = 0f
                val totalTime = 40_000L
                val steps = 100
                val stepDelay = totalTime / steps

                repeat(steps + 1) { i ->
                    currentProgress = i / steps.toFloat()
                    delay(stepDelay)
                }
            }

            LivenessProcessingStatus.DOWNLOADING,
            LivenessProcessingStatus.FINISH -> {
                return@LaunchedEffect
            }

            else -> {
                currentProgress = 0f
                repeat(101) { i ->
                    currentProgress = i / 100f
                    delay(20)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            processing(
                selectedBitmap
            )

            if (currentProcessingError != LivenessProcessingStatus.DOWNLOADING) {
                onNext()
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
            text = currentProcessingState.title,
            color = RarimeTheme.colors.textSecondary,
            style = RarimeTheme.typography.body3
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in LivenessProcessingStatus.entries) {

                if (i == LivenessProcessingStatus.FINISH)
                    continue

                val isError =
                    currentProcessingError?.ordinal != null && i == currentProcessingError
                val isFinished =
                    i.ordinal < currentProcessingState.ordinal      // step comes before current = done
                val isProcessing = i.ordinal == currentProcessingState.ordinal   // current step
                val isNotStarted = i.ordinal > currentProcessingState.ordinal

                val currentStatus = when {
                    isError -> ProcessingItemStatus.FAILED
                    isFinished -> ProcessingItemStatus.FINISHED
                    isProcessing && !isError -> ProcessingItemStatus.LOADING
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

                    ProcessingItemStatus.FAILED -> ProcessItemError(
                        title = i.title,
                        onRetry = if (currentProcessingError == LivenessProcessingStatus.DOWNLOADING) {
                            {
                                retryScope.launch {
                                    processing(selectedBitmap)
                                }
                            }
                        } else null
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
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    progress.coerceIn(
                        0f, 1f
                    )
                )
                .height(60.dp)
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
fun ProcessItemError(
    modifier: Modifier = Modifier,
    title: String, onRetry: (() -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    1f
                )
                .height(60.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(RarimeTheme.colors.errorLighter)
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
                color = RarimeTheme.colors.errorDark
            )

            if (onRetry != null) {
                AppIcon(
                    id = R.drawable.ic_restart_line,
                    tint = RarimeTheme.colors.errorDark,
                    modifier = Modifier
                        .clickable(onClick = onRetry)
                        .size(20.dp)
                        .clip(CircleShape)
                )
            } else {
                AppIcon(id = R.drawable.ic_close, tint = RarimeTheme.colors.errorDark)
            }
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
                .height(60.dp)
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
            selectedBitmap = createBitmap(0, 0, Bitmap.Config.ALPHA_8),
            onNext = {

            },
            currentProcessingState = LivenessProcessingStatus.DOWNLOADING,
            processing = {

            },
            downloadProgress = 0
        )
    }
}
