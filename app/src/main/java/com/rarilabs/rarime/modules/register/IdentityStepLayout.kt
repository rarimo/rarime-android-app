package com.rarilabs.rarime.modules.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
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
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            PrimaryTextButton(leftIcon = R.drawable.ic_caret_left, onClick = onBack)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier,
                text = title,
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )
        }
        content()

        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            nextButton()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IdentityStepLayoutPreview() {
    IdentityStepLayout(
        title = "Title",
        onBack = {},
        nextButton = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = "Button",
                onClick = { /*TODO*/ }
            )
        },
    ) {
        Text("Content")
    }
}