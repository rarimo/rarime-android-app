package com.rarilabs.rarime.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme

class AppCheckboxState(initialChecked: Boolean = false) {
    var checked by mutableStateOf(initialChecked)
        private set

    fun updateChecked(newChecked: Boolean) {
        checked = newChecked
    }

    companion object {
        val Saver: Saver<AppCheckboxState, *> = listSaver(save = { listOf(it.checked) }, restore = {
            AppCheckboxState(initialChecked = it[0])
        })
    }
}

@Composable
fun rememberAppCheckboxState(checked: Boolean = false) =
    rememberSaveable(checked, saver = AppCheckboxState.Saver) {
        AppCheckboxState(checked)
    }

@Composable
fun AppCheckbox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: AppCheckboxState = rememberAppCheckboxState(),
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (state.checked) RarimeTheme.colors.primaryDark
        else RarimeTheme.colors.componentPrimary, label = ""
    )

    Box(modifier = modifier
        .background(animatedBgColor, RoundedCornerShape(4.dp))
        .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(4.dp))
        .padding(2.dp)
        .clickable(interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { if (enabled) state.updateChecked(!state.checked) })) {
        AppIcon(
            id = R.drawable.ic_check,
            size = 16.dp,
            tint = if (state.checked) RarimeTheme.colors.baseBlack else Color.Transparent,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppCheckboxPreview() {
    val checkedState = rememberAppCheckboxState()
    AppTheme {
        Box(modifier = Modifier.padding(8.dp)) {
            AppCheckbox(state = checkedState)
        }
    }
}