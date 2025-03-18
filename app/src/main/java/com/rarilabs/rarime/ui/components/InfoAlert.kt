package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun InfoAlert(
    text: String,
    actionBar: @Composable () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.warningLighter, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        AppIcon(id = R.drawable.ic_info, tint = RarimeTheme.colors.warningDarker)
        Text(
            text = text,
            style = RarimeTheme.typography.body5,
            color = RarimeTheme.colors.warningDarker,
            modifier = Modifier.weight(1f)
        )
        actionBar()
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoAlertPreview() {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoAlert("This is very important text to understand")
        InfoAlert("This is very important text to understand and follow") {
            PrimaryTextButton(leftIcon = R.drawable.ic_caret_right, onClick = { /*TODO*/ })
        }
    }
}