package com.rarilabs.rarime.modules.recoveryMethod

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Composable
fun RecoveryMethodDetailScreen(
    onClose: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            // horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                AppIcon(id = R.drawable.ic_arrow_left_s_line, tint = RarimeTheme.colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(100.dp))
            Text(
                text = stringResource(R.string.recover_method_details_screen_title),
                style = RarimeTheme.typography.buttonMedium.copy(color = RarimeTheme.colors.textPrimary),
                modifier = Modifier.align(alignment = Alignment.CenterVertically)

            )

        }
        Card { Card { } }
        Card {}
        Card {}
        Card {}
    }

}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewRecoveryMethodDetailScreenLight(

) {

    RecoveryMethodDetailScreen(onClose = {})

}

//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
//)
//@Composable
//fun PreviewRecoveryMethodDetailScreenDark(
//
//) {
//
//    RecoveryMethodDetailScreen(onClose = {})
//
//}