package com.rarilabs.rarime.modules.you

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun IdentityCard(modifier: Modifier = Modifier) {
    Card {
        Column {
            Text("ID CARD")
        }
    }
}


@Preview
@Composable
private fun IdentityCardPreview() {
    Surface {
        IdentityCard()
    }
}