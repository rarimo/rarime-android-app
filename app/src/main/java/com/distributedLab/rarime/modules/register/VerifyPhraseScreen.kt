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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSheetState
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun VerifyPhraseScreen(onNext: () -> Unit, onBack: () -> Unit) {
    // TODO: Replace with actual word numbers and options
    val wordNumbers = listOf(2, 5, 10)
    val wordOptions = listOf(
        listOf("domain", "explore", "club"),
        listOf("apple", "music", "features"),
        listOf("party", "engage", "features")
    )

    val selectedWords = remember { mutableStateListOf("", "", "") }
    val sheetState = rememberAppSheetState()

    fun validateWords(): Boolean {
        // TODO: Replace with actual phrase validation
        return selectedWords.toList() == listOf("explore", "apple", "features")
    }

    PhraseStepLayout(
        step = 2,
        title = stringResource(R.string.verify_phrase_title),
        onBack = onBack,
        nextButton = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = stringResource(R.string.next_btn),
                rightIcon = R.drawable.ic_arrow_right,
                enabled = selectedWords.all { it.isNotEmpty() },
                onClick = {
                    if (validateWords()) {
                        onNext()
                    } else {
                        sheetState.show()
                    }
                }
            )
        }
    ) {
        CardContainer {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                for ((index, word) in selectedWords.withIndex()) {
                    WordSelector(
                        wordNumber = wordNumbers[index],
                        selectedWord = word,
                        wordOptions = wordOptions[index],
                        onWordSelected = { selectedWords[index] = it }
                    )
                }
            }
        }
        IncorrectSelectionSheet(sheetState)
    }
}

@Composable
private fun WordSelector(
    wordNumber: Int,
    selectedWord: String,
    wordOptions: List<String>,
    onWordSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.word_number, wordNumber),
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            wordOptions.forEach {
                WordButton(
                    modifier = Modifier.weight(1f),
                    word = it,
                    selected = it == selectedWord,
                    onClick = { onWordSelected(it) }
                )
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
            color = if (selected) RarimeTheme.colors.baseBlack else RarimeTheme.colors.textPrimary
        )
    }
}

@Composable
private fun IncorrectSelectionSheet(sheetState: AppSheetState) {
    AppBottomSheet(
        state = sheetState,
        bottomBar = { hide ->
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = stringResource(R.string.try_again_btn),
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
                text = stringResource(R.string.incorrect_selection_title),
                style = RarimeTheme.typography.h5,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.incorrect_selection_text),
                style = RarimeTheme.typography.body2,
                color = RarimeTheme.colors.textSecondary
            )
        }
    }
}


@Preview
@Composable
private fun VerifyPhraseScreenPreview() {
    VerifyPhraseScreen(onNext = {}, onBack = {})
}