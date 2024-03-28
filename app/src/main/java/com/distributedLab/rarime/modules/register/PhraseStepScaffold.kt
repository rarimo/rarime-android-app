package com.distributedLab.rarime.modules.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.theme.RarimeTheme


val totalSteps = 2

@Composable
fun PhraseStepScaffold(
    step: Int,
    title: String,
    nextButtonText: String,
    onNext: () -> Unit,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PrimaryTextButton(leftIcon = R.drawable.ic_caret_left, onClick = onBack)
                Text(
                    text = stringResource(R.string.step_indicator, step, totalSteps),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            }
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = title,
                style = RarimeTheme.typography.subtitle2,
                color = RarimeTheme.colors.textPrimary
            )
            content()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RarimeTheme.colors.backgroundPure)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = nextButtonText,
                rightIcon = R.drawable.ic_arrow_right,
                onClick = onNext
            )
        }
    }
}