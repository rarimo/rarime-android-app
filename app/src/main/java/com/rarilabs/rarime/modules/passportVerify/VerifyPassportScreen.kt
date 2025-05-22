package com.rarilabs.rarime.modules.passportVerify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.ConflictException
import com.rarilabs.rarime.modules.home.components.ErrorReservedPointsContent
import com.rarilabs.rarime.modules.home.components.ReservedCongratsModalContent
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.passportVerify.viewModels.ReserveTokenViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.UiPrivacyCheckbox
import com.rarilabs.rarime.ui.components.rememberAppCheckboxState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.ZkpException
import kotlinx.coroutines.launch

@Composable
fun VerifyPassportScreen(
    reserveTokenViewModel: ReserveTokenViewModel = hiltViewModel(),
    onFinish: () -> Unit,
    onSendError: () -> Unit
) {
    val mainViewModal = LocalMainViewModel.current
    var isReserving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val termsAcceptedState = rememberAppCheckboxState()
    suspend fun reserveTokens() {
        isReserving = true
        try {
            reserveTokenViewModel.reserve()

            mainViewModal.setModalVisibility(true)
            mainViewModal.setModalContent {
                ReservedCongratsModalContent(
                    onClose = { mainViewModal.setModalVisibility(false) },
                )
            }
            onFinish()
        } catch (e: ZkpException) {
            if (e.message?.contains("timestampUpperbound") == true) {
                mainViewModal.setModalVisibility(true)
                reserveTokenViewModel.setAlreadyReserved()
                mainViewModal.setModalContent {
                    ErrorReservedPointsContent(
                        onClose = { mainViewModal.setModalVisibility(false) },
                        title = stringResource(R.string.passport_is_expired_error_header),
                        description = stringResource(R.string.passport_is_expired_error_description)
                    )
                }
                ErrorHandler.logError("VerifyPoitntsScreen", "ConflictException", e)
                onFinish()
            }
        } catch (e: ConflictException) {
            mainViewModal.setModalVisibility(true)
            reserveTokenViewModel.setAlreadyReserved()
            mainViewModal.setModalContent {
                ErrorReservedPointsContent(
                    onClose = { mainViewModal.setModalVisibility(false) },
                    title = stringResource(R.string.already_reserved_points_header),
                    description = stringResource(R.string.already_reserved_points_body)
                )
            }
            ErrorHandler.logError("VerifyPoitntsScreen", "ConflictException", e)
            onFinish()
        } catch (e: Exception) {
            mainViewModal.setModalVisibility(true)
            mainViewModal.setModalContent {
                ErrorReservedPointsContent(
                    onClose = { onSendError.invoke(); mainViewModal.setModalVisibility(false) },
                    title = stringResource(R.string.reserve_error_header),
                    description = stringResource(R.string.reserve_error_description)
                )
            }
            ErrorHandler.logError("VerifyPoitntsScreen", "ConflictException", e)
            onFinish()
        }
        isReserving = false
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 80.dp, bottom = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy((-32).dp)) {
                AppIcon(
                    id = R.drawable.ic_rarimo,
                    size = 32.dp,
                    tint = RarimeTheme.colors.textPrimary,
                    modifier = Modifier
                        .background(RarimeTheme.colors.backgroundPure, CircleShape)
                        .border(2.dp, RarimeTheme.colors.backgroundPrimary, CircleShape)
                        .padding(20.dp),
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .size(72.dp)
                        .background(RarimeTheme.colors.backgroundPure, CircleShape)
                        .border(2.dp, RarimeTheme.colors.backgroundPrimary, CircleShape)
                ) {
                    Text(
                        text = reserveTokenViewModel.getFlag(),
                        style = RarimeTheme.typography.h5,
                        color = RarimeTheme.colors.textPrimary,
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.reserve_tokens_title, Constants.SCAN_PASSPORT_REWARD.toInt()
                    ),
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary,
                )
                Text(
                    text = stringResource(R.string.reserve_tokens_description),
                    style = RarimeTheme.typography.body3,
                    textAlign = TextAlign.Center,
                    color = RarimeTheme.colors.textSecondary,
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
                UiPrivacyCheckbox(termsAcceptedState = termsAcceptedState, enabled = !isReserving)
                PrimaryButton(
                    text = if (isReserving) stringResource(R.string.reserving_btn) else stringResource(
                        R.string.reserv_btn
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = termsAcceptedState.checked && !isReserving,
                    size = ButtonSize.Large,
                    onClick = { coroutineScope.launch { reserveTokens() } })
            }
        }
    }
}


@Preview
@Composable
private fun VerifyPoitntsScreenPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy((-32).dp)) {
        AppIcon(
            id = R.drawable.ic_rarimo,
            size = 32.dp,
            tint = RarimeTheme.colors.textPrimary,
            modifier = Modifier
                .background(RarimeTheme.colors.backgroundPure, CircleShape)
                .border(2.dp, RarimeTheme.colors.backgroundPrimary, CircleShape)
                .padding(20.dp),
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(72.dp)
                .background(RarimeTheme.colors.backgroundPure, CircleShape)
                .border(2.dp, RarimeTheme.colors.backgroundPrimary, CircleShape)
        ) {
            Text(
                text = "🇺🇦",
                style = RarimeTheme.typography.h5,
                color = RarimeTheme.colors.textPrimary,
            )
        }
    }
}