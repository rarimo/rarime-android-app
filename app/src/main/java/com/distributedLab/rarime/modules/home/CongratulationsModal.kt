package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun CongratulationsModal(isClaimed: Boolean, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = RarimeTheme.colors.baseBlack.copy(alpha = 0.1f))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
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
                if (isClaimed) {
                    Image(
                        painter = painterResource(id = R.drawable.reward_coin),
                        contentDescription = null,
                        modifier = Modifier.size(110.dp)
                    )
                } else {
                    AppIcon(
                        id = R.drawable.ic_check,
                        size = 24.dp,
                        tint = RarimeTheme.colors.backgroundPure,
                        modifier = Modifier
                            .background(RarimeTheme.colors.successMain, CircleShape)
                            .padding(28.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isClaimed) stringResource(R.string.congrats_title) else stringResource(
                            R.string.joined_waitlist_title
                        ),
                        style = RarimeTheme.typography.h6,
                        color = RarimeTheme.colors.textPrimary
                    )
                    Text(
                        text = if (isClaimed) stringResource(R.string.congrats_description) else stringResource(
                            R.string.joined_waitlist_description
                        ),
                        style = RarimeTheme.typography.body2,
                        color = RarimeTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(240.dp)
                    )
                }
                HorizontalDivider()
                PrimaryButton(
                    text = if (isClaimed) stringResource(R.string.thanks_btn) else stringResource(R.string.okay_btn),
                    size = ButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClose
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CongratulationsModalPreview() {
    CongratulationsModal(isClaimed = true, onClose = {})
}
