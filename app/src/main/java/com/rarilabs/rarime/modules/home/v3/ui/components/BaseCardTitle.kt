package com.rarilabs.rarime.modules.home.v3.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

/**
 * Card header component with optional title, gradient subtitle, and caption.
 * Styles for each text element can be customized.
 *
 * @param modifier Modifier applied to the root Column.
 *
 * @param title Optional primary title text.
 * @param titleStyle TextStyle for the primary title.
 *
 * @param accentTitle Optional text rendered with a gradient brush.
 * @param gradient Brush used to tint the gradientTitle text.
 * @param accentTitleStyle TextStyle for the gradient title.
 *
 * @param caption Optional caption text below the title or subtitle.
 * @param captionStyle TextStyle for the caption.
 */
@Composable
fun BaseCardTitle(
    modifier: Modifier = Modifier,

    title: String? = null,
    titleModifier: Modifier = Modifier,
    titleStyle: TextStyle = TextStyle(
        fontSize = RarimeTheme.typography.h2.fontSize,
        fontWeight = RarimeTheme.typography.h2.fontWeight,
        color = RarimeTheme.colors.baseBlack
    ),

    accentTitle: String? = null,
    accentTitleModifier: Modifier = Modifier,
    gradient: Brush = RarimeTheme.colors.gradient6,
    accentTitleStyle: TextStyle = TextStyle(
        brush = gradient,
        fontSize = RarimeTheme.typography.additional2.fontSize,
        fontWeight = RarimeTheme.typography.additional2.fontWeight
    ),

    caption: String? = null,
    captionModifier: Modifier = Modifier,
    captionStyle: TextStyle = TextStyle(
        fontSize = RarimeTheme.typography.body4.fontSize,
        fontWeight = RarimeTheme.typography.body4.fontWeight,
        color = RarimeTheme.colors.baseBlackOp40
    ),
) {
    Column(modifier = modifier) {
        title?.let {
            Text(
                text = it,
                style = titleStyle,
                modifier = titleModifier
            )
        }

        accentTitle?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = accentTitleStyle,
                modifier = accentTitleModifier
            )
        }

        caption?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                style = captionStyle,
                modifier = captionModifier
            )
        }
    }
}

@Preview(showBackground = true, name = "Default Styles")
@Composable
fun BaseCardTitlePreview_Default() {
    BaseCardTitle(
        title = "RariMe",
        accentTitle = "Learn More",
        caption = "* Nothing leaves this device"
    )
}

@Preview(showBackground = true, name = "Custom H5 Title & Caption")
@Composable
fun BaseCardTitlePreview_CustomStyles() {
    BaseCardTitle(
        title = "RariMe",
        accentTitle = "Explore",
        caption = "Built locally",
        titleStyle = TextStyle(
            fontSize = RarimeTheme.typography.h5.fontSize,
            fontWeight = RarimeTheme.typography.h5.fontWeight,
            color = RarimeTheme.colors.textPrimary
        ),
        captionStyle = TextStyle(
            fontSize = RarimeTheme.typography.body2.fontSize,
            fontWeight = RarimeTheme.typography.body2.fontWeight,
            color = RarimeTheme.colors.textSecondary
        )
    )
}
