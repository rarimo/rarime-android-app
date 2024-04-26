package com.distributedLab.rarime.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.ui.theme.dropShadow
import kotlinx.coroutines.launch

@Composable
fun AppSwitch(
    modifier: Modifier = Modifier,
    state: AppCheckboxState = rememberAppCheckboxState(),
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    val xOffset = if (state.checked) 16f else 0f
    val animatedOffset = remember { Animatable(xOffset) }

    val animatedBgColor by animateColorAsState(
        targetValue =
        if (!enabled) RarimeTheme.colors.componentDisabled
        else if (state.checked) RarimeTheme.colors.primaryDark
        else RarimeTheme.colors.componentPrimary,
        label = ""
    )

    DisposableEffect(state.checked) {
        if (animatedOffset.targetValue != xOffset) {
            scope.launch {
                animatedOffset.animateTo(xOffset, animationSpec = tween(300))
            }
        }
        onDispose { }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(24.dp)
            .width(40.dp)
            .background(animatedBgColor, CircleShape)
            .padding(2.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
                if (enabled) {
                    state.updateChecked(!state.checked)
                }
            }
    ) {
        Box(
            Modifier
                .size(20.dp)
                .offset(animatedOffset.value.dp, 0.dp)
                .dropShadow(borderRadius = 250.dp)
                .background(
                    if (enabled) RarimeTheme.colors.baseWhite else RarimeTheme.colors.componentDisabled,
                    CircleShape
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppSwitchPreview() {
    val checkedState = rememberAppCheckboxState()

    Column(
        modifier = Modifier.padding(12.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row {
            AppSwitch(state = checkedState)
            Text(
                text = "Regular",
                modifier = Modifier.padding(8.dp, 0.dp),
                style = RarimeTheme.typography.subtitle4,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppSwitch(
                state = checkedState,
                enabled = false,
            )
            Text(
                text = "Disabled",
                modifier = Modifier.padding(8.dp, 0.dp),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textDisabled
            )
        }
    }
}
