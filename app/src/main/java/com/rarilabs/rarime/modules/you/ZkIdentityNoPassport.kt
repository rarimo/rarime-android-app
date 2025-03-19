package com.rarilabs.rarime.modules.you

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ZkIdentityNoPassport(modifier: Modifier = Modifier, navigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 70.dp)
            .then(modifier)
    ) {
        Text("Your", style = RarimeTheme.typography.h1, color = RarimeTheme.colors.textPrimary)
        Text(
            "ZK Identity",
            style = RarimeTheme.typography.additional1,
            color = RarimeTheme.colors.successMain
        )

        Spacer(modifier = Modifier.height(88.dp))

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "Select IdentityType",
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary
        )
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IdentityCardTypeItem(
                imageId = R.drawable.ic_rarimo,
                name = "Passport",
                isActive = true,
                onClick = {}
            )

            HorizontalDivider()


            IdentityCardTypeItem(
                imageId = R.drawable.ic_rarimo,
                name = "POH",
                isActive = true,
                onClick = {
                }
            )

            HorizontalDivider()


            IdentityCardTypeItem(
                imageId = R.drawable.ic_rarimo,
                name = "Driver License",
                isActive = false,
                onClick = {}
            )

            HorizontalDivider()


            IdentityCardTypeItem(
                imageId = R.drawable.ic_rarimo,
                name = "Driver License",
                isActive = false,
                onClick = {}
            )

            HorizontalDivider()


            IdentityCardTypeItem(
                imageId = R.drawable.ic_rarimo,
                name = "Driver License",
                isActive = false,
                onClick = {}
            )

        }

        Spacer(Modifier.height(24.dp))

    }
}


@Preview
@Composable
private fun ZkIdentityNoPassportPreview() {
    Surface {
        ZkIdentityNoPassport {}
    }
}