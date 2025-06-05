package com.rarilabs.rarime.modules.manageWidgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Composable
fun Widget(
    imageResId: Int,
    title: String,
    description: String
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "",
            contentScale = Crop,
            modifier = Modifier
                .size(width = 190.dp, height = 220.dp)
                .clip(RoundedCornerShape(32.dp))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = title,
            style = RarimeTheme.typography.h3,
            color = RarimeTheme.colors.textPrimary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 20.dp)
        )
        Text(
            text = description,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
            minLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 40.dp)
        )
    }

}