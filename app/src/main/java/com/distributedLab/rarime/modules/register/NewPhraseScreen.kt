package com.distributedLab.rarime.modules.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.InfoAlert
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

// TODO: Replace with actual wordlist
val wordlist = listOf(
    "domain",
    "explore",
    "famous",
    "lion",
    "apple",
    "banana",
    "cherry",
    "date",
    "egg",
    "fig",
    "grape",
    "honey",
)

@Composable
fun NewPhraseScreen(onNext: () -> Unit, onBack: () -> Unit) {
    PhraseStepScaffold(
        step = 1,
        title = stringResource(R.string.new_phrase_title),
        nextButtonText = stringResource(R.string.continue_btn),
        onNext = onNext,
        onBack = onBack,
    ) {
        CardContainer {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    for ((index, value) in wordlist.withIndex()) {
                        item {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        RarimeTheme.colors.componentPrimary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 6.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    style = RarimeTheme.typography.subtitle5,
                                    color = RarimeTheme.colors.textPrimary
                                )
                                Text(
                                    text = value,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = RarimeTheme.typography.body3,
                                    color = RarimeTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PrimaryTextButton(
                        leftIcon = R.drawable.ic_copy_simple,
                        text = stringResource(R.string.copy_to_clipboard_btn),
                        onClick = { /*TODO*/ }
                    )
                }
                HorizontalDivider()
                InfoAlert(text = stringResource(R.string.new_phrase_warning))
            }
        }
    }
}

@Preview
@Composable
private fun NewPhraseScreenPreview() {
    NewPhraseScreen(onNext = {}, onBack = {})
}