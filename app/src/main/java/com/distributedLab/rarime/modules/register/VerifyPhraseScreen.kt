package com.distributedLab.rarime.modules.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun VerifyPhraseScreen(onNext: () -> Unit, onBack: () -> Unit) {
    val sheetState = rememberAppSheetState()
    val isCorrect = remember {
        mutableStateOf(false)
    }

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
                onClick = {
                    if (isCorrect.value) {
                        onNext()
                    } else {
                        sheetState.show()
                        isCorrect.value = true
                    }
                }
            )
        },
    ) {
        CardContainer {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                WordSelector(
                    wordNumber = 2,
                    options = listOf("domain", "explore", "club")
                )
                WordSelector(
                    wordNumber = 5,
                    options = listOf("apple", "music", "features")
                )
                WordSelector(
                    wordNumber = 10,
                    options = listOf("party", "engage", "features")
                )
            }
        }

        AppBottomSheet(
            state = sheetState,
            bottomBar = { hide ->
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.Large,
                    text = "Try Again",
                    onClick = hide
                )
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(RarimeTheme.colors.errorLighter, CircleShape)
                        .padding(20.dp)
                ) {
                    AppIcon(
                        id = R.drawable.ic_info,
                        size = 32.dp,
                        tint = RarimeTheme.colors.errorMain
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = "Incorrect",
                    style = RarimeTheme.typography.h5,
                    color = RarimeTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = "Selections not matched. please try again",
                    style = RarimeTheme.typography.body2,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun WordSelector(wordNumber: Int, options: List<String>) {
    val selectedWord = remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Word #$wordNumber",
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach {
                WordButton(
                    modifier = Modifier.weight(1f),
                    word = it,
                    selected = it == selectedWord.value,
                ) {
                    selectedWord.value = it
                }
            }
        }
    }
}

@Composable
private fun WordButton(
    modifier: Modifier,
    word: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            if (selected) 0.dp else 1.dp,
            if (selected) RarimeTheme.colors.primaryMain else RarimeTheme.colors.componentPrimary
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) RarimeTheme.colors.primaryMain else RarimeTheme.colors.backgroundPure,
        ),
        onClick = onClick
    ) {
        Text(
            text = word,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary
        )
    }
}

@Preview
@Composable
private fun VerifyPhraseScreenPreview() {
    VerifyPhraseScreen(onNext = {}, onBack = {})
}