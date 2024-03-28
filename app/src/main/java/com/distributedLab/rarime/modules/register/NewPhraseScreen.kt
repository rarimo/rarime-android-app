package com.distributedLab.rarime.modules.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.InfoAlert
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

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
                    text = "Step 1/2",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            }
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "New recovery phrase",
                style = RarimeTheme.typography.subtitle2,
                color = RarimeTheme.colors.textPrimary
            )
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp),) {
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
                            text = "Copy to clipboard",
                            onClick = { /*TODO*/ }
                        )
                    }
                    HorizontalDivider()
                    InfoAlert(text = "Don’t share your recovery phrase with anyone")
                }
            }
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
                text = "Continue",
                rightIcon = R.drawable.ic_arrow_right,
                onClick = onNext
            )
        }
    }
}

@Preview
@Composable
private fun NewPhraseScreenPreview () {
    NewPhraseScreen(onNext = {}, onBack = {})
}