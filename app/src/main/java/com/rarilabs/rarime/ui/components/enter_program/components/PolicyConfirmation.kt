package com.rarilabs.rarime.ui.components.enter_program.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.components.HomeIntroLayout
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.UiPrivacyCheckbox
import com.rarilabs.rarime.ui.components.rememberAppCheckboxState
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun PolicyConfirmation(
    onNext: () -> Unit = { }
) {
    val termsAcceptedState = rememberAppCheckboxState()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        HomeIntroLayout(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.reward_coin),
                    contentDescription = "Invitation Icon",
                    modifier = Modifier.size(110.dp)
                )
            },
            title = stringResource(id = R.string.other_passport_card_title),
            description = stringResource(id = R.string.other_passport_card_description)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.checking_eligibility),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HorizontalDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                UiPrivacyCheckbox(termsAcceptedState = termsAcceptedState)

                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = termsAcceptedState.checked,
                    size = ButtonSize.Large,
                    onClick = onNext
                )
            }
        }
    }
}

@Preview
@Composable
private fun PolicyConfirmationPreview() {
    Column(
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        PolicyConfirmation(onNext = {})
    }

}