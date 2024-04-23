package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
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
        title = "Other passport holders",
        description = "short description text here"
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors ",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = buildAnnotatedString {
                        append("Full functional available on: ")
                        withStyle(RarimeTheme.typography.subtitle5.toSpanStyle()) {
                            append("July")
                        }
                    },
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.warningMain
                )
            }
            PrimaryButton(
                text = "Join the waitlist",
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