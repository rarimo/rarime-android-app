package com.rarilabs.rarime.modules.you

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ZkIdentityPohScreen(modifier: Modifier = Modifier, navigate: (String) -> Unit) {
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()

    ZkIdentityPohScreenContent(
        innerPaddings = innerPaddings, navigate = navigate, modifier = modifier
    )
}

@Composable
private fun ZkIdentityPohScreenContent(
    // TODO: Implement navigate
    navigate: (String) -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Bottom, modifier = modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp,
                top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp
            )
            .then(modifier)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        LivenessTitle()
        Spacer(modifier = Modifier.weight(1f))
        LivenessList()
        Spacer(modifier = Modifier.height(38.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            size = ButtonSize.Large,
            text = stringResource(R.string.zk_liveness_poh_btn),
            onClick = { TODO("Implement onClick handler") },
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LivenessTitle(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()
    ) {
        LivenessIcon(
            iconId = R.drawable.ic_body_scan_fill, containerSize = 88.dp, iconSize = 44.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.zk_liveness_poh_title), style = RarimeTheme.typography.h2
        )
    }
}

@Composable
private fun LivenessList(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier,
    ) {
        LivenessRow(
            iconId = R.drawable.ic_smartphone_line, labelId = R.string.zk_liveness_poh_list_item_1
        )
        LivenessRow(
            iconId = R.drawable.ic_stack_line, labelId = R.string.zk_liveness_poh_list_item_2
        )
        LivenessRow(
            iconId = R.drawable.ic_shield_check, labelId = R.string.zk_liveness_poh_list_item_3
        )
    }
}

@Composable
private fun LivenessRow(
    @DrawableRes iconId: Int, @StringRes labelId: Int, modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        LivenessIcon(iconId = iconId, containerSize = 40.dp, iconSize = 24.dp)
        Text(text = stringResource(labelId), style = RarimeTheme.typography.body3)
    }
}

@Composable
private fun LivenessIcon(@DrawableRes iconId: Int, containerSize: Dp, iconSize: Dp) {
    Box(
        modifier = Modifier
            .size(containerSize)
            .clip(CircleShape)
            .background(brush = RarimeTheme.colors.gradient1), contentAlignment = Alignment.Center
    ) {
        AppIcon(
            id = iconId,
            tint = RarimeTheme.colors.textPrimary,
            size = iconSize,
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun ZkIdentityPohScreenPreview() {
    ZkIdentityPohScreenContent(
        navigate = {}, innerPaddings = mapOf(ScreenInsets.TOP to 40, ScreenInsets.BOTTOM to 40)
    )
}