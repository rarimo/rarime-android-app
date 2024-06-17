package com.distributedLab.rarime.ui.components.enter_program.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.home.components.HomeIntroLayout
import com.distributedLab.rarime.ui.base.BaseIconButton
import com.distributedLab.rarime.ui.components.AppTextField
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.enter_program.UNSPECIFIED_PASSPORT_STEPS
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SocialItem(
    val icon: Int,
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun Invitation(
    modifier: Modifier = Modifier,
    onNext: () -> Unit = { },
    updateStep: (step: UNSPECIFIED_PASSPORT_STEPS) -> Unit = { },
    invitationViewModel: InvitationViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current

    val invitationCodeState = rememberAppTextFieldState(initialText = "")

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isSubmitting by remember { mutableStateOf(false) }

    fun verifyCode() {
        scope.launch {
            isSubmitting = true

            try {
                invitationViewModel.createBalance(invitationCodeState.text)
                onNext()
            } catch (e: Exception) {
                Log.e("verifyCode", e.toString())
                invitationCodeState.updateErrorMessage(
                    context.getString(R.string.invalid_referal_code)
                )
            }

            isSubmitting = false
        }
    }

    HomeIntroLayout(
        title = stringResource(id = R.string.other_passport_card_title),
        description = stringResource(id = R.string.other_passport_card_description),
        icon = {
            Image(
                painter = painterResource(id = R.drawable.reward_coin),
                contentDescription = "Invitation Icon",
                modifier = Modifier.size(110.dp)
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            AppTextField(
                state = invitationCodeState,
                placeholder = "Enter invitation code",
                enabled = !isSubmitting,
                trailingItem = {
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        BaseIconButton(
                            modifier = Modifier
                                .width(52.dp)
                                .height(32.dp),
                            icon = R.drawable.ic_arrow_right,
                            onClick = { verifyCode() },
                            enabled = invitationCodeState.text.isNotEmpty() && !isSubmitting,
                            colors = ButtonColors(
                                containerColor = RarimeTheme.colors.primaryMain,
                                contentColor = RarimeTheme.colors.textPrimary,
                                disabledContainerColor = RarimeTheme.colors.componentDisabled,
                                disabledContentColor = RarimeTheme.colors.textPrimary.copy(alpha = 0.5f),
                            ),
                        )
                    }
                }
            )

            HorizontalDivider()

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "How can I get a code?",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary
                )

                Text(
                    text = "You must be invited or receive a code from social channels",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf(
                    SocialItem(
                        icon = R.drawable.ic_twitter_x,
                        title = "x",
                        onClick = { uriHandler.openUri(BaseConfig.TWITTER_URL) }
                    ),
                    SocialItem(
                        icon = R.drawable.ic_discord,
                        title = "Discord",
                        onClick = { uriHandler.openUri(BaseConfig.DISCORD_URL) }
                    )
                ).forEachIndexed { idx, it ->
                    if (idx > 0) {
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RarimeTheme.colors.componentPrimary)
                            .requiredHeight(78.dp)
                            .clickable { it.onClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = it.icon),
                                contentDescription = it.title
                            )

                            Text(
                                text = it.title,
                                style = RarimeTheme.typography.buttonSmall,
                                color = RarimeTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Supported countries",
                    style = RarimeTheme.typography.buttonSmall,
                    color = RarimeTheme.colors.textSecondary
                )

                Text(
                    modifier = Modifier
                        .clickable {
                            updateStep(UNSPECIFIED_PASSPORT_STEPS.ABOUT_PROGRAM)
                        },
                    text = "Learn more about the program",
                    style = RarimeTheme.typography.buttonSmall,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
    }
}

@Preview
@Composable
fun InvitationPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {

        Invitation()
    }
}