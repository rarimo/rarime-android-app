package com.distributedLab.rarime.modules.rewards.components.rewards_leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun NumberCircle (
    modifier: Modifier = Modifier,
    number: Int
) {
    val fontSize = when (number.toString().length) {
        1 -> 14.sp
        2 -> 14.sp
        3 -> 12.sp
        4 -> 8.sp
        else -> 6.sp
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(32.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(RarimeTheme.colors.backgroundPure)
            .zIndex(1f)
    ) {
        Text(
            text = number.toString(),
            style = RarimeTheme.typography.subtitle4,
            fontSize = fontSize,
            color = RarimeTheme.colors.textPrimary
        )
    }
}

@Preview
@Composable
fun NumberCirclePreview() {
    Row (
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(24.dp)
            .width(500.dp)
            .height(500.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NumberCircle(number = 1)
        NumberCircle(number = 11)
        NumberCircle(number = 222)
        NumberCircle(number = 3333)
        NumberCircle(number = 44444)
    }
}