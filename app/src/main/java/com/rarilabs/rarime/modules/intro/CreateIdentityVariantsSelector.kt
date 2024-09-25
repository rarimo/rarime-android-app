package com.rarilabs.rarime.modules.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

data class IdentityVariant(
    val title: String,
    val icon: Int,
    val onSelect: () -> Unit
)

@Composable
fun CreateIdentityVariantsSelector(
    variants: List<IdentityVariant>
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.create_identity_selector_title),
            style = RarimeTheme.typography.h5,
            color = RarimeTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.create_identity_selector_subtitle),
            style = RarimeTheme.typography.body2,
            color = RarimeTheme.colors.textSecondary,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            variants.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            RarimeTheme.colors.componentPrimary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                        .clickable { it.onSelect() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                RarimeTheme.colors.backgroundPure,
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppIcon(id = it.icon, tint = RarimeTheme.colors.textPrimary)
                    }

                    Text(
                        text = it.title,
                        style = RarimeTheme.typography.subtitle4,
                        color = RarimeTheme.colors.textPrimary,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateIdentityVariantsSelectorPreview() {
    CreateIdentityVariantsSelector(
        variants = listOf(
            IdentityVariant(
                title = "Option 1",
                icon = R.drawable.ic_share,
                onSelect = {}
            ),
            IdentityVariant(
                title = "Option 2",
                icon = R.drawable.ic_arrow_counter_clockwise,
                onSelect = {}
            ),
            IdentityVariant(
                title = "Option 3",
                icon = R.drawable.ic_share_1,
                onSelect = {}
            ),
        )
    )
}