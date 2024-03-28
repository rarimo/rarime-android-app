package com.distributedLab.rarime.modules.register

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton

@Composable
fun VerifyPhraseScreen(onNext: () -> Unit, onBack: () -> Unit) {
    PhraseStepScaffold(
        step = 2,
        title = "Verify recovery phrase",
        onBack = onBack,
        nextButton = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = "Next",
                rightIcon = R.drawable.ic_arrow_right,
                onClick = onNext
            )
        },
    ) {
        CardContainer(modifier = Modifier.height(300.dp))
    }
}

@Preview
@Composable
private fun VerifyPhraseScreenPreview() {
    VerifyPhraseScreen(onNext = {}, onBack = {})
}