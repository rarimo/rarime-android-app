package com.rarilabs.rarime.modules.votes.voteProcessScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import com.rarilabs.rarime.ui.components.InfoAlert
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.UiLinearProgressBar
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch

@Composable
fun SendingVoteScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current,
    isFinishAllowed: Boolean = true,
    updateIsAnimated: (Boolean) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    var initialProgress by remember { mutableStateOf(0f) }


    LaunchedEffect(lifecycleOwner) {
        coroutineScope.launch {
            progress.snapTo(initialProgress)
            progress.animateTo(
                targetValue = 0.97f,
                animationSpec = tween(durationMillis = 30000, easing = FastOutSlowInEasing)
            )
        }
    }

    LaunchedEffect(isFinishAllowed) {
        if (isFinishAllowed) {
            coroutineScope.launch {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                )
                updateIsAnimated(true)
            }
        }
    }

    LaunchedEffect(progress.value) {
        initialProgress = progress.value
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.75f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VoteLoadingScreen()
            //AppAnimation(id = R.raw.anim_dots)
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Sending",
                style = RarimeTheme.typography.h5,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sending your vote",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UiLinearProgressBar(
                    percentage = progress.value,
                )

                Text(
                    text = "${(progress.value * 100).toInt()}%",
                    style = RarimeTheme.typography.caption2,
                    color = RarimeTheme.colors.primaryMain,
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(40.dp))

            InfoAlert(text = "Please don\'t close the app, or your answers won’t be included.")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SendingVoteScreenPreview() {
    var isAnimated by remember {
        mutableStateOf(false)
    }

    var isFinishAllowed by remember {
        mutableStateOf(false)
    }

    if (isAnimated) {
        Text("Done")
    } else {
        SendingVoteScreen(
            isFinishAllowed = isFinishAllowed,
            updateIsAnimated = {
                isAnimated = it
            },
        )
    }

    PrimaryButton(onClick = { isFinishAllowed = true })
}