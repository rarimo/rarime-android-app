package com.rarilabs.rarime.modules.manageWidgets.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.FillBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun FreedomtoolWidget(
    colorScheme: AppColorScheme
) {
    val isDark = when (colorScheme) {
        AppColorScheme.SYSTEM -> isSystemInDarkTheme()
        AppColorScheme.DARK -> true
        AppColorScheme.LIGHT -> false
    }

    val widgetRes = remember(isDark) {
        if (isDark) R.drawable.ic_freedomtool_widget_dark
        else R.drawable.ic_freedomtool_widget_light
    }

    Column(modifier = Modifier.fillMaxWidth()){
        Image(
            painter = painterResource(id = widgetRes),
            contentDescription = "",
            contentScale = Crop,
            modifier = Modifier
                .size(width = 190.dp, height = 220.dp)
                .clip(RoundedCornerShape(32.dp))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.freedomtool_widget_title),
            style = RarimeTheme.typography.h3,
            color = RarimeTheme.colors.textPrimary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 20.dp,)
        )
        Text(text = stringResource(R.string.freedomtool_widget_description),
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