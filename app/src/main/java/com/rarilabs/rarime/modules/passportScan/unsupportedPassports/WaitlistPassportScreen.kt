package com.rarilabs.rarime.modules.passportScan.unsupportedPassports

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.components.JoinWaitlistCongratsModalContent
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.modules.passportScan.models.WaitlistPassportScreenViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.InfoAlert
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TertiaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Country
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SendEmailUtil
import com.rarilabs.rarime.util.SendEmailUtil.sendEmail

@Composable
fun WaitlistPassportScreen(
    eDocument: EDocument,
    onClose: () -> Unit,
    viewModel: WaitlistPassportScreenViewModel = hiltViewModel(),
) {
    val mainViewModel = LocalMainViewModel.current

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        ErrorHandler.logDebug("WaitlistPassportScreen", eDocument.aaResponse.toString())
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            viewModel.joinWaitlist()
            mainViewModel.setModalContent {
                JoinWaitlistCongratsModalContent(onClose = {
                    mainViewModel.setModalVisibility(false)
                })
            }
            mainViewModel.setModalVisibility(true)

            SendEmailUtil.deleteEdocumentFile(context)

            onClose.invoke()
        }
    )

    WaitlistPassportScreenContent(
        eDocument = eDocument,
        onClose = onClose,
        onJoin = {
            try {
                val file = SendEmailUtil.generateEdocumentFile(eDocument, context)
                launcher.launch(
                    sendEmail(
                        file!!, context, header = "Edocument", ""
                    )
                )
            } catch (e: Exception) {
                ErrorHandler.logError("Waitlist", "Cant send eDocument", e)
            }
        }
    )
}

@Composable
private fun WaitlistPassportScreenContent(
    eDocument: EDocument,
    onClose: () -> Unit,
    onJoin: () -> Unit,
) {
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

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(72.dp)
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
                    .border(2.dp, RarimeTheme.colors.backgroundPrimary, CircleShape)
            ) {
                Text(
                    text = Country.fromISOCode(eDocument.personDetails!!.nationality)!!.flag,
                    style = RarimeTheme.typography.h3,
                    color = RarimeTheme.colors.textPrimary,
                )
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
                        R.string.waitlist_title
                    ),
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary,
                )
                Text(
                    text = Country.fromISOCode(eDocument.personDetails!!.nationality)!!.localizedName,
                    style = RarimeTheme.typography.body4,
                    textAlign = TextAlign.Center,
                    color = RarimeTheme.colors.textSecondary,
                )

                HorizontalDivider()
            }
            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(id = R.string.waitlist_subtitle),
                    color = RarimeTheme.colors.textPrimary,
                    style = RarimeTheme.typography.subtitle4
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(id = R.string.waitlist_description),
                    color = RarimeTheme.colors.textPrimary,
                    style = RarimeTheme.typography.body4
                )
                Spacer(modifier = Modifier.height(24.dp))
                InfoAlert(text = stringResource(id = R.string.waitlist_info))
            }

        }


        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimaryButton(
                text = stringResource(R.string.join_program),
                size = ButtonSize.Large,
                rightIcon = R.drawable.ic_arrow_right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                onClick = { onJoin.invoke() },
            )
            TertiaryButton(
                onClick = {
                    onClose.invoke()
                },
                text = stringResource(id = R.string.cancel_btn),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun WaitlistPassportScreenPreview() {
    val eDocument: EDocument = EDocument(personDetails = PersonDetails(issuerAuthority = "GEO"))

    WaitlistPassportScreenContent(eDocument = eDocument, onClose = {}, onJoin = {})
}