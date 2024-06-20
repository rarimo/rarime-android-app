package com.rarilabs.rarime.ui.base

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.RichTooltipColors
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTooltip (
    modifier: Modifier = Modifier,
    iconColor: Color = RarimeTheme.colors.textPrimary,
    tooltipContent: @Composable () -> Unit = {},
    tooltipText: String? = null,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state = rememberTooltipState()

    Row (
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        content()

        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = {
                if (tooltipText != null) {
                    RichTooltip(
                        text = {
                            Text(
                                text = tooltipText,
                                style = RarimeTheme.typography.body3,
                                color = RarimeTheme.colors.textSecondary,
                            )
                        },
                        colors = RichTooltipColors(
                            containerColor = RarimeTheme.colors.baseWhite,
                            contentColor = RarimeTheme.colors.textPrimary,
                            titleContentColor = RarimeTheme.colors.textPrimary,
                            actionContentColor = RarimeTheme.colors.textPrimary,
                        ),
                    )
                }
                tooltipContent()
            },
            state = state
        ) {
            AppIcon(
                id = R.drawable.ic_info,
                tint = iconColor,
                size = 14.dp,
                // FIXME: autohide too fast
                modifier = Modifier.clickable { scope.launch { state.show(MutatePriority.Default) } }
            )
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTooltipPreview() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BaseTooltip (
            tooltipContent = {
                RichTooltip(
                    text = {
                        Text(
                            text = "Lorem ipsum dolor sit amet concestetur! Lorem ipsum dolor sit amet concestetur! Lorem ipsum dolor sit amet concestetur!",
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.warningDarker,
                        )
                    },
                    colors = RichTooltipColors(
                        containerColor = RarimeTheme.colors.warningLighter,
                        contentColor = RarimeTheme.colors.warningDarker,
                        titleContentColor = RarimeTheme.colors.successDarker,
                        actionContentColor = RarimeTheme.colors.textPrimary,
                    ),
                )
            }
        ) {
            Text(text = "custom tooltip")
        }
        Spacer(modifier = Modifier.height(100.dp))
        BaseTooltip (
            tooltipText = "Lorem ipsum dolor sit amet concestetur! Lorem ipsum dolor sit amet concestetur! Lorem ipsum dolor sit amet concestetur! "
        ) {
            Text(text = "default minimal tooltip")
        }
    }
}