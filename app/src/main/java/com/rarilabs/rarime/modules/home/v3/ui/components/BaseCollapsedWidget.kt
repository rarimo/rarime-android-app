package com.rarilabs.rarime.modules.home.v3.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppBackgroundGradient
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun BaseCollapsedWidget(
    modifier: Modifier = Modifier,

    // slots
    header: @Composable () -> Unit = {},
    body: @Composable ColumnScope.() -> Unit = {},
    footer: @Composable () -> Unit = {},
    background: @Composable BoxScope.() -> Unit = {},

    // default `Card` props
    shape: Shape = RoundedCornerShape(40.dp),
    colors: CardColors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    border: BorderStroke? = null,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            background()

            Column(modifier = Modifier.fillMaxSize()) {
                header()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    body()
                }
                footer()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BaseCollapsedWidgetPreview() {

    BaseCollapsedWidget(
        modifier = Modifier
            .height(552.dp)
            .padding(16.dp)
            .fillMaxWidth(),
        header = {
            Row(modifier = Modifier.padding(24.dp)) {
                BaseWidgetLogo()
            }
        },
        body = {},
        footer = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(start = 22.dp, bottom = 28.dp, end = 30.dp)
            ) {
                BaseWidgetTitle(
                    title = "RariMe",
                    accentTitle = "Learn More",
                    caption = "* Nothing leaves this device",
                )
                Spacer(modifier = Modifier.weight(1f))
                AppIcon(id = R.drawable.ic_arrow_right_up_line)
            }
        },
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RarimeTheme.colors.backgroundBlur)
            ) {
                AppBackgroundGradient()
            }
        }
    )
}
