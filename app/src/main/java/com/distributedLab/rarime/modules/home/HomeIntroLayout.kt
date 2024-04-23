package com.distributedLab.rarime.modules.home

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
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun HomeIntroLayout(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .padding(top = 40.dp, bottom = 20.dp)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
            ) {
                icon()
            }
            Text(
                text = title,
                style = RarimeTheme.typography.h6,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = description,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(300.dp)
            )
        }
        HorizontalDivider()
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeIntroLayoutPreview() {
    HomeIntroLayout(
        icon = { AppIcon(id = R.drawable.ic_house_simple_fill, size = 32.dp) },
        title = "Home Intro Title",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
    ) {
        Text("Content")
    }
}