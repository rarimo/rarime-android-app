package com.rarilabs.rarime.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun TipAlert(
    title: String = "Tip:",
    text: String,
    actionBar: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.gradient10, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(id = R.drawable.ic_bulb, tint = Color(0xFF863AC4))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle7,
                color = Color(0xFF863AC4),
                modifier = Modifier.weight(1f)
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = RarimeTheme.typography.body4,
            color = RarimeTheme.colors.textPrimary,
        )
        actionBar()
    }
}

@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun TipAlertPreviewLight() {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TipAlert("Tip:", "This is very important text to understand")
        TipAlert("Tip:", "This is very important text to understand and follow") {
            PrimaryTextButton(leftIcon = R.drawable.ic_caret_right, onClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun TipAlertPreviewDark() {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TipAlert("Tip:", "This is very important text to understand")
        TipAlert("Tip:", "This is very important text to understand and follow") {
            PrimaryTextButton(leftIcon = R.drawable.ic_caret_right, onClick = { /*TODO*/ })
        }
    }
}
