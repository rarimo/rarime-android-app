package com.rarilabs.rarime.modules.you

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ZkIdentityPassport(modifier: Modifier = Modifier) {
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
            IdentityCard()
        }


    }
}


@Preview
@Composable
private fun ZkIdentityPassportPreview() {
    Surface {
        ZkIdentityPassport()
    }
}