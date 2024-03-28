package com.distributedLab.rarime.modules.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun PasscodeScreen(onNext: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.enable_passcode_title),
            style = RarimeTheme.typography.subtitle1,
            color = RarimeTheme.colors.textPrimary
        )
        PrimaryButton(
            text = stringResource(R.string.enable_btn),
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            onClick = onNext
        )
    }
}
