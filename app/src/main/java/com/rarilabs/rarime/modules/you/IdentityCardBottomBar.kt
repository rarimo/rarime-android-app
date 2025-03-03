package com.rarilabs.rarime.modules.you

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun IdentityCardBottomBar(modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = RarimeTheme.colors.backgroundPrimary)
                .padding(horizontal = 16.dp, vertical = 9.dp)
                .then(modifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Column {
                Text("#")
                Spacer(modifier = Modifier.height(4.dp))
                Text("13B294029491")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppIcon(
                    id =
                    R.drawable.ic_eye_slash,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { })
                )


                AppIcon(
                    id = R.drawable.ic_dots_three_outline,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { })
                )
            }


        }
    }

}

@Preview
@Composable
private fun IdentityCardBottomBarPreview() {
    Surface {
        IdentityCardBottomBar()
    }
}