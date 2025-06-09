package com.rarilabs.rarime.modules.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ProfileRouteLayout(
    title: String,
    onBack: () -> Unit,
    paddingHorizontal: Dp = 20.dp,
    paddingVertical: Dp = 20.dp,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Box(
            modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            PrimaryTextButton(
                leftIcon = R.drawable.ic_caret_left, onClick = onBack
            )
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle6,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = paddingHorizontal)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun ProfileRouteLayoutPreview() {
    ProfileRouteLayout(
        title = "Profile Route Title",
        onBack = {},
    ) {
        CardContainer {
            Text("Profile Route Content")
        }
    }
}