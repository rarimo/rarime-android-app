package com.distributedLab.rarime.modules.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSheetState
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun GetStartedSheet(
    sheetState: AppSheetState,
    onCreateClick: () -> Unit,
    onImportClick: () -> Unit
) {
    AppBottomSheet(state = sheetState) {
        Column {
            Text(
                text = stringResource(R.string.get_started_title),
                modifier = Modifier.fillMaxWidth(),
                style = RarimeTheme.typography.h5,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.get_started_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                style = RarimeTheme.typography.body2,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                GetStartedButton(
                    title = stringResource(R.string.create_identity_title),
                    text = stringResource(R.string.create_identity_text),
                    icon = {
                        AppIcon(
                            id = R.drawable.ic_user_plus,
                            tint = RarimeTheme.colors.textPrimary
                        )
                    },
                    onClick = onCreateClick
                )
                GetStartedButton(
                    title = stringResource(R.string.import_identity_title),
                    text = stringResource(R.string.import_identity_text),
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_metamask),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = onImportClick
                )
            }
        }
    }
}

@Composable
private fun GetStartedButton(
    icon: @Composable () -> Unit,
    title: String,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RarimeTheme.colors.componentPrimary),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(RarimeTheme.colors.backgroundOpacity, CircleShape)
                    .padding(10.dp)
            ) {
                icon()
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.buttonMedium,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = text,
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
    }
}

@Preview
@Composable
private fun GetStartedSheetPreview() {
    val sheetState = rememberAppSheetState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            PrimaryButton(
                text = "Show sheet",
                onClick = { sheetState.show() }
            )
        }
        GetStartedSheet(
            sheetState = sheetState,
            onCreateClick = { },
            onImportClick = { }
        )
    }
}