package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun AppSkeleton(modifier: Modifier = Modifier, cornerRadius: Float = 100f) {
    Box(
        modifier = modifier
            .background(RarimeTheme.colors.componentPrimary, RoundedCornerShape(cornerRadius))
    )
}

@Preview
@Composable
private fun AppSkeletonPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPure)
            .padding(20.dp)
    ) {
        AppSkeleton(
            modifier = Modifier
                .width(60.dp)
                .height(12.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AppSkeleton(
                modifier = Modifier
                    .width(140.dp)
                    .height(30.dp)
            )
            AppSkeleton(
                modifier = Modifier
                    .width(60.dp)
                    .height(20.dp)
            )
        }
        AppSkeleton(
            modifier = Modifier
                .width(200.dp)
                .height(12.dp)
        )
        AppSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}
