package com.distributedLab.rarime.modules.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.modules.passport.PassportIntroScreen
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RewardsScreen(onPassportScan: () -> Unit) {
    val sheetState = rememberAppSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        Text(
            text = "Rewards",
            style = RarimeTheme.typography.subtitle1,
            color = RarimeTheme.colors.textPrimary
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            size = ButtonSize.Large,
            text = "Scan Passport",
            onClick = { sheetState.show() }
        )

        AppBottomSheet(state = sheetState) { hide ->
            PassportIntroScreen(onStart = { hide(onPassportScan) })
        }
    }
}
