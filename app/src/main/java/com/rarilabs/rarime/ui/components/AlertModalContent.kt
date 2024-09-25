package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun AlertModalContent(
    onClose: () -> Unit = {},
    withConfetti: Boolean = true,
    mediaContent: @Composable () -> Unit = {
        AppIcon(
            id = R.drawable.ic_check,
            size = 24.dp,
            tint = RarimeTheme.colors.textPrimary,
            modifier = Modifier
                .background(RarimeTheme.colors.primaryMain, CircleShape)
                .padding(28.dp)
        )
    },
    title: String,
    subtitle: String,
    buttonText: String,
    buttonColor: Color = RarimeTheme.colors.baseBlack,
    buttonBg: Color = RarimeTheme.colors.primaryMain
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPure, RoundedCornerShape(24.dp))
    ) {
        if (withConfetti) {
            Image(
                painter = painterResource(id = R.drawable.confetti),
                contentDescription = null,
                modifier = Modifier
                    .width(240.dp)
                    .height(170.dp)
                    .padding(top = 10.dp)
                    .align(Alignment.TopCenter)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            mediaContent()
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = RarimeTheme.typography.body2,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(240.dp)
                )
            }

            HorizontalDivider()

            BaseButton(
                text = buttonText,
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBg,
                    contentColor = buttonColor,
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContratsInvitationModalContentPreview() {
    AlertModalContent(
        title = "Lorem",
        subtitle = "Ipsum Dolor Sit Amet",
        buttonText = "Concestetur!"
    )
}