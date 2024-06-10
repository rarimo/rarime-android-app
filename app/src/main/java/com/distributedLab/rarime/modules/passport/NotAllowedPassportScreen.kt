package com.distributedLab.rarime.modules.passport

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
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.InfoAlert
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.TertiaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.SendErrorUtil.saveErrorDetailsToFile
import com.distributedLab.rarime.util.SendErrorUtil.sendErrorEmail
import com.google.gson.Gson
import org.json.JSONObject

@Composable
fun NotAllowedPassportScreen(
    modifier: Modifier = Modifier, eDocument: EDocument, onClose: () -> Unit, onNext: () -> Unit,
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
                AppIcon(id = R.drawable.ic_rarimo, size = 31.dp)

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
                        R.string.unsupported_country_title
                    ),
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary,
                )
                Text(
                    text = eDocument.personDetails?.issuerAuthority ?: "",
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
                    text = stringResource(id = R.string.unsupported_country_description),
                    color = RarimeTheme.colors.textPrimary,
                    style = RarimeTheme.typography.body3
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

        }


        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimaryButton(
                text = stringResource(R.string.unsupported_country_btn),
                size = ButtonSize.Large,
                rightIcon = R.drawable.ic_arrow_right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                onClick = {
                    onNext.invoke()
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
@Preview
fun NotAllowedPassportScreenPreview() {
    val eDocument: EDocument = EDocument(personDetails = PersonDetails(issuerAuthority = "GEO"))
    NotAllowedPassportScreen(eDocument = eDocument, onClose = {}, onNext = {})
}