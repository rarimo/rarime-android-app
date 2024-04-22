package com.distributedLab.rarime.modules.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

enum class BottomTab(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val activeIcon: Int
) {
    Home(
        Screen.Main.Home.route,
        R.drawable.ic_house_simple,
        R.drawable.ic_house_simple_fill
    ),
    Wallet(
        Screen.Main.Wallet.route,
        R.drawable.ic_wallet,
        R.drawable.ic_wallet_filled
    ),
    Rewards(
        Screen.Main.Rewards.route,
        R.drawable.ic_gift,
        R.drawable.ic_gift_fill
    ),
    Profile(
        Screen.Main.Profile.route,
        R.drawable.ic_user,
        R.drawable.ic_user
    )
}

@Composable
fun BottomTabBar(currentRoute: String?, onRouteSelected: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(vertical = 32.dp)
                .background(RarimeTheme.colors.secondaryDark, CircleShape)
                .padding(4.dp)
                .height(48.dp)
        ) {
            BottomTab.entries.forEach { tab ->
                TabItem(
                    tab = tab,
                    isSelected = currentRoute == tab.route,
                    onTabSelected = { onRouteSelected(it.route) }
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: BottomTab,
    isSelected: Boolean,
    onTabSelected: (BottomTab) -> Unit,
) {
    val animatedColor by animateColorAsState(
        if (isSelected) RarimeTheme.colors.primaryMain else Color.Transparent,
        label = "animated_tab_bar_bg"
    )

    Column(
        modifier = Modifier
            .width(48.dp)
            .height(48.dp)
            .clip(CircleShape)
            .background(animatedColor)
            .pointerInput(Unit) {
                detectTapGestures {
                    onTabSelected(tab)
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppIcon(
            id = if (isSelected) tab.activeIcon else tab.icon,
            tint = if (isSelected) RarimeTheme.colors.baseBlack else RarimeTheme.colors.baseWhite,
            modifier = if (isSelected) Modifier else Modifier.alpha(0.5f)
        )
    }
}

@Preview
@Composable
private fun BottomTabBarPreview() {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    BottomTabBar(
        currentRoute = selectedTab.route,
        onRouteSelected = { route -> selectedTab = BottomTab.entries.first { it.route == route } }
    )
}
