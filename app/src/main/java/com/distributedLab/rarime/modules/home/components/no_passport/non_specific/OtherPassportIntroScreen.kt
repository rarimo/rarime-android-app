package com.distributedLab.rarime.modules.home.components.no_passport.non_specific

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.home.components.HomeIntroLayout
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun OtherPassportIntroScreen(onStart: () -> Unit) {
    HomeIntroLayout(
        icon = {
            AppIcon(
                id = R.drawable.ic_globe_simple,
                size = 32.dp,
                tint = RarimeTheme.colors.textPrimary
            )
        },
        title = stringResource(R.string.other_passport_title),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = stringResource(R.string.other_passport_text),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textPrimary
            )
            PrimaryButton(
                text = stringResource(R.string.join_waitlist_btn),
                rightIcon = R.drawable.ic_arrow_right,
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                onClick = onStart
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtherPassportIntroScreenPreview() {
    OtherPassportIntroScreen(onStart = {})
}