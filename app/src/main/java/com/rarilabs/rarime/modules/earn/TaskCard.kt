package com.rarilabs.rarime.modules.earn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun TaskCard(
    taskIconId: Int,
    rewardInRMO: Int,
    title: String,
    onClick: () -> Unit,
    description: String,
    currentVal: Int,
    maxVal: Int
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RarimeTheme.colors.backgroundSurface1),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(width = 1.dp, color = RarimeTheme.colors.componentPrimary),
        onClick = onClick,
        modifier = Modifier
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 34.dp, top = 34.dp, end = 34.dp)

            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = RarimeTheme.colors.componentPrimary)
                ) {
                    Icon(
                        painter = painterResource(taskIconId),
                        contentDescription = "",
                        tint = RarimeTheme.colors.textPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(
                            color = RarimeTheme.colors.textPrimary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ("+" + rewardInRMO),
                            style = RarimeTheme.typography.overline3,
                            color = RarimeTheme.colors.invertedLight
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_rarimo),
                            contentDescription = "",
                            tint = RarimeTheme.colors.invertedLight,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 34.dp)
            ) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right_line),
                    contentDescription = "",
                    tint = RarimeTheme.colors.textPrimary,
                    modifier = Modifier
                        .padding(start = 6.dp, top = 3.dp)
                        .size(20.dp)

                )


            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 34.dp)
            ) {
                Text(
                    text = "$description: $currentVal/$maxVal",
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )

        }
    }
}