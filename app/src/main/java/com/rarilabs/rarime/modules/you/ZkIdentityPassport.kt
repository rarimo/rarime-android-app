package com.rarilabs.rarime.modules.you

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler

@Composable
fun ZkIdentityPassport(
    navigate: (String) -> Unit,
) {

    val homeViewModel = LocalZkIdentityScreenViewModel.current

    val passport by homeViewModel.passport.collectAsState()

    val passportCardLook by homeViewModel.passportCardLook
    val passportIdentifiers by homeViewModel.passportIdentifiers
    val isIncognito by homeViewModel.isIncognito
    val passportStatus by homeViewModel.passportStatus.collectAsState()

    val registrationStatus by homeViewModel.uiState.collectAsState()

    val retryRegistration = homeViewModel::retryRegistration

    val updatePassportStatus = homeViewModel.updatePassportStatus

    LaunchedEffect(Unit) {
        Log.i("Status", passportStatus.name)
        if (passportStatus == PassportStatus.UNREGISTERED) {
            try {
                homeViewModel.performRegistration(passport!!)
            } catch (error: Exception) {
                ErrorHandler.logError(
                    "Passport registration", error.message ?: "Registration error", error
                )
            }

        }
    }

    ZkIdentityPassportContent(
        passport = passport!!,
        look = passportCardLook,
        identifiers = passportIdentifiers,
        isIncognito = isIncognito,
        passportStatus = passportStatus,
        onLookChange = homeViewModel::onPassportCardLookChange,
        onIdentifiersChange = homeViewModel::onPassportIdentifiersChange,
        onIncognitoChange = homeViewModel::onIncognitoChange,
        registrationStatus = registrationStatus,
        retryRegistration = retryRegistration,
    )
}

@Composable
fun ZkIdentityPassportContent(
    passport: EDocument,
    look: PassportCardLook,
    identifiers: List<PassportIdentifier>,
    isIncognito: Boolean,
    passportStatus: PassportStatus,
    onLookChange: (PassportCardLook) -> Unit,
    onIncognitoChange: (Boolean) -> Unit,
    onIdentifiersChange: (List<PassportIdentifier>) -> Unit,
    registrationStatus: IdentityCardBottomBarUiState,
    retryRegistration: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "YOU",
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )
            Column(
                Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(RarimeTheme.colors.componentPrimary)
            ) {
                AppIcon(modifier = Modifier.padding(10.dp), id = R.drawable.ic_plus)
            }
        }

        Column(Modifier.padding(start = 12.dp, end = 12.dp, top = 20.dp)) {
            IdentityCard(
                passport = passport,
                isIncognito = isIncognito,
                look = look,
                identifiers = identifiers,
                onLookChange = onLookChange,
                onIncognitoChange = onIncognitoChange,
                passportStatus = passportStatus,
                onIdentifiersChange = onIdentifiersChange,
                registrationStatus = registrationStatus,
                retryRegistration = retryRegistration
            )
        }
    }
}

@Preview
@Composable
private fun ZkIdentityPassportPreview() {
    var isIncognito by remember { mutableStateOf(false) }
    var look by remember { mutableStateOf(PassportCardLook.BLACK) }
    var identifiers by remember {
        mutableStateOf(
            listOf(
                PassportIdentifier.NATIONALITY, PassportIdentifier.DOCUMENT_ID
            )
        )
    }
    Surface {

        ZkIdentityPassportContent(
            passport = EDocument(
                personDetails = PersonDetails(
                    name = "John",
                    surname = "Doe",
                    birthDate = "01.01.1990",
                    expiryDate = "01.01.2025",
                    nationality = "USA",
                    serialNumber = "123456789",
                    faceImageInfo = null
                )
            ),
            look = look,
            identifiers = identifiers,
            isIncognito = isIncognito,
            onLookChange = { look = it },
            onIncognitoChange = { isIncognito = it },
            registrationStatus = IdentityCardBottomBarUiState(),
            retryRegistration = {},
            passportStatus = PassportStatus.NOT_ALLOWED,
            onIdentifiersChange = { identifiers = it })
    }
}