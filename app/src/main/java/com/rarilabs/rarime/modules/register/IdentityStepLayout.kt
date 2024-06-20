package com.rarilabs.rarime.modules.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun IdentityStepLayout(
    title: String,
    onBack: () -> Unit,
    nextButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp)) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                PrimaryTextButton(leftIcon = R.drawable.ic_caret_left, onClick = onBack)
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(bottom = 32.dp),
                    text = title,
                    style = RarimeTheme.typography.subtitle2,
                    color = RarimeTheme.colors.textPrimary
                )
            }
            content()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RarimeTheme.colors.backgroundPure)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            nextButton()
        }
    }
}

@Preview
@Composable
private fun IdentityStepLayoutPreview() {
    IdentityStepLayout(
        title = "Title",
        onBack = {},
        nextButton = {
            PrimaryButton(text = "Button", onClick = { /*TODO*/ })
        },
    ) {
        Text("Content")
    }
}