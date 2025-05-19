package com.rarilabs.rarime.modules.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun HomeIntroLayout(
    icon: @Composable () -> Unit,
    title: String,
    description: String? = null,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .padding(top = 40.dp, bottom = 20.dp)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon()

            Text(
                text = title,
                style = RarimeTheme.typography.h6,
                color = RarimeTheme.colors.textPrimary
            )
            description?.let {
                Text(
                    text = description,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(300.dp)
                )
            }
        }
        HorizontalDivider()
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeIntroLayoutPreview() {
    HomeIntroLayout(
        icon = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
            ) {

                AppIcon(
                    id = R.drawable.ic_house_simple_fill,
                    size = 32.dp,
                    tint = RarimeTheme.colors.textPrimary
                )
            }
        },
        title = "Home Intro Title",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
    ) {
        Text("Content")
    }
}