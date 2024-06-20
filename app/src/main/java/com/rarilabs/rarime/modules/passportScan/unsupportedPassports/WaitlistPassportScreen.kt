package com.rarilabs.rarime.modules.passportScan.unsupportedPassports

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.InfoAlert
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TertiaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Country
import com.rarilabs.rarime.util.SendErrorUtil.saveErrorDetailsToFile
import com.rarilabs.rarime.util.SendErrorUtil.sendErrorEmail
import com.google.gson.Gson
import com.rarilabs.rarime.modules.home.components.JoinWaitlistCongratsModalContent
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import org.json.JSONObject

@Composable
fun WaitlistPassportScreen(
    modifier: Modifier = Modifier, eDocument: EDocument, onClose: () -> Unit
) {
    val mainViewModel = LocalMainViewModel.current

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            Log.i("result Code", it.resultCode.toString())

            mainViewModel.joinWaitlist()
            mainViewModel.setModalContent {
                JoinWaitlistCongratsModalContent(
                    onClose = {
                        mainViewModel.setModalVisibility(false)
                    }
                )
            }
            mainViewModel.setModalVisibility(true)

            onClose.invoke()
        })

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
                    .background(RarimeTheme.colors.backgroundPure, CircleShape)
                    .border(2.dp, RarimeTheme.colors.backgroundPrimary, CircleShape)
            ) {
                Text(
                    text = Country.fromISOCode(eDocument.personDetails!!.nationality)!!.flag,
                    style = RarimeTheme.typography.h5,
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
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary,
                )
                Text(
                    text = Country.fromISOCode(eDocument.personDetails!!.nationality)!!.localizedName,
                    style = RarimeTheme.typography.body3,
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
                    style = RarimeTheme.typography.subtitle2
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(id = R.string.waitlist_description),
                    color = RarimeTheme.colors.textPrimary,
                    style = RarimeTheme.typography.body3
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
                onClick = {
                    val gson = Gson()
                    val eDoc = gson.toJson(eDocument)
                    val errorDetails = JSONObject()
                    errorDetails.put("eDoucument", eDoc)
                    val file = saveErrorDetailsToFile(errorDetails.toString(), context)
                    launcher.launch(sendErrorEmail(file, context))
                },
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
    WaitlistPassportScreen(eDocument = eDocument, onClose = {})
}