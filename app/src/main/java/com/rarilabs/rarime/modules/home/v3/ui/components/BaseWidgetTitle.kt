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
 * Widget header component with optional overline, title, gradient subtitle, and caption.
 * Styles for each text element can be customized.
 *
 * @param modifier Modifier applied to the root Column.
 *
 * @param overline Optional small text rendered above the main title.
 * @param overlineModifier Modifier for the overline text.
 * @param overlineStyle TextStyle for the overline.
 *
 * @param title Optional primary title text.
 * @param titleModifier Modifier for the title text.
 * @param titleStyle TextStyle for the primary title.
 *
 * @param accentTitle Optional text rendered with a gradient brush below the title.
 * @param accentTitleModifier Modifier for the gradient subtitle.
 * @param gradient Brush used to tint the gradient subtitle.
 * @param accentTitleStyle TextStyle for the gradient subtitle.
 *
 * @param caption Optional caption text below the subtitle.
 * @param captionModifier Modifier for the caption.
 * @param captionStyle TextStyle for the caption.
 */
@Composable
fun BaseWidgetTitle(
    modifier: Modifier = Modifier,

    overline: String? = null,
    overlineModifier: Modifier = Modifier,
    overlineStyle: TextStyle = TextStyle(
        fontSize = RarimeTheme.typography.overline1.fontSize,
        fontWeight = RarimeTheme.typography.overline1.fontWeight,
        color = RarimeTheme.colors.baseBlackOp50
    ),

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
        overline?.let {
            Text(
                text = it,
                style = overlineStyle,
                modifier = overlineModifier
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

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

@Preview(showBackground = true, name = "With Overline")
@Composable
fun BaseWidgetTitlePreview_Overline() {
    BaseWidgetTitle(
        overline = "Category",
        title = "RariMe",
        accentTitle = "Learn More",
        caption = "* Nothing leaves this device"
    )
}
