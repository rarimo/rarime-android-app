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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.ui.theme.dropShadow
import kotlinx.coroutines.launch

@Composable
fun AppSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    val xOffset = if (checked) 16f else 0f
    val animatedOffset = remember { Animatable(xOffset) }
    val alpha = if (enabled) 1f else 0.8f

    val animatedBgColor by animateColorAsState(
        targetValue =
        if (checked) RarimeTheme.colors.primaryDark
        else RarimeTheme.colors.componentPrimary,
        label = ""
    )

    LaunchedEffect(checked) {
        if (animatedOffset.targetValue != xOffset) {
            scope.launch {
                animatedOffset.animateTo(xOffset, animationSpec = tween(300))
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(24.dp)
            .width(40.dp)
            .alpha(alpha)
            .background(animatedBgColor, CircleShape)
            .padding(2.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
                if (enabled) {
                    onCheckedChange(!checked)
                }
            }
    ) {
        Box(
            Modifier
                .size(20.dp)
                .offset(animatedOffset.value.dp, 0.dp)
                .dropShadow(borderRadius = 250.dp)
                .background(RarimeTheme.colors.baseWhite.copy(alpha = alpha), CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppSwitchPreview() {
    var isChecked by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(12.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row {
            AppSwitch(checked = isChecked, onCheckedChange = { isChecked = it })
            Text(
                text = "Regular",
                modifier = Modifier.padding(8.dp, 0.dp),
                style = RarimeTheme.typography.subtitle4,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppSwitch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
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
