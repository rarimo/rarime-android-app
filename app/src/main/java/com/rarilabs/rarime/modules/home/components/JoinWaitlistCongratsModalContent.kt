package com.rarilabs.rarime.modules.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun JoinWaitlistCongratsModalContent(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPure, RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.confetti),
            contentDescription = null,
            modifier = Modifier
                .width(240.dp)
                .height(170.dp)
                .padding(top = 10.dp)
                .align(Alignment.TopCenter)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            AppIcon(
                id = R.drawable.ic_check,
                size = 24.dp,
                tint = RarimeTheme.colors.backgroundPure,
                modifier = Modifier
                    .background(RarimeTheme.colors.successMain, CircleShape)
                    .padding(28.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.congrats_joined_waitlist_title),
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.congrats_joined_waitlist_description),
                    style = RarimeTheme.typography.body2,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(240.dp)
                )
            }

            HorizontalDivider()

            PrimaryButton(
                text = stringResource(R.string.okay_btn),
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = onClose
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NonSpecificCongratsModalContentPreview() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(24.dp)
    ) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            JoinWaitlistCongratsModalContent(onClose = {})
        }
    }
}