package com.rarilabs.rarime.modules.home.v3.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.model.BG_DOT_MAP_HEIGHT
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun BaseExpandedWidget(
    modifier: Modifier = Modifier,

    // slots
    header: @Composable () -> Unit = {},
    body: @Composable ColumnScope.() -> Unit = {},
    footer: @Composable () -> Unit = {},
    background: @Composable BoxScope.() -> Unit = {},

    // default `Card` props
    shape: Shape = RoundedCornerShape(0.dp),
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,

    columnModifier: Modifier = Modifier
        .verticalScroll(rememberScrollState())
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
                    modifier = columnModifier
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
fun BaseExpandedWidgetPreview() {
    BaseExpandedWidget(
        modifier = Modifier.fillMaxSize(),
        header = {
            Row(modifier = Modifier.padding(20.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    AppIcon(id = R.drawable.ic_close)
                }
            }
        },
        body = {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Spacer(Modifier.height(BG_DOT_MAP_HEIGHT.dp))
                BaseWidgetTitle(
                    title = "RariMe",
                    accentTitle = "Learn More",
                    gradient = RarimeTheme.colors.gradient6,
                    caption = "* Nothing leaves this device",
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "An identification and privacy solution that revolutionizes polling, surveying and election processes",
                    color = RarimeTheme.colors.textSecondary
                )
            }
        },
        footer = {},
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RarimeTheme.colors.gradient3)
            ) {
                Image(
                    painter = painterResource(R.drawable.freedomtool_bg),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BG_DOT_MAP_HEIGHT.dp)
                )
            }
        }
    )
}
