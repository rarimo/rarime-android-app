package com.rarilabs.rarime.modules.manageWidgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.modules.manageWidgets.widgets.DigitalLikenessWidget
import com.rarilabs.rarime.modules.manageWidgets.widgets.FreedomtoolWidget
import com.rarilabs.rarime.modules.manageWidgets.widgets.HiddenPrizeWidget
import com.rarilabs.rarime.modules.manageWidgets.widgets.RecoveryMethodWidget
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalPageIndicator
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import androidx.compose.runtime.getValue

enum class Widgets(val id: Int, val isVisible: Boolean) {
    FREEDOMTOOL(0, true),
    LIKENESS(1, true),
    HIDDEN_PRIZE(2, true),
    RECOVERY_METHOD(3, true),
}

@Composable
fun ManageWidgetsBottomSheet(
    onClose: () -> Unit,
    onRemove: () -> Unit,
    onAdd: () -> Unit,
    viewModel: ManageWidgetsViewModel = hiltViewModel()
) {
    val colorScheme by viewModel.colorScheme.collectAsState()

    ManageWidgetsBottomSheetContent(
        onClose = onClose,
        onRemove = onRemove,
        onAdd = onAdd,
        colorScheme = colorScheme
    )

}

@Composable
fun ManageWidgetsBottomSheetContent(
    onClose: () -> Unit,
    onRemove: () -> Unit,
    onAdd: () -> Unit,
    colorScheme: AppColorScheme
) {

    val pagerState = rememberPagerState(pageCount = { Widgets.values().size })
    Column() {
        Row() {
            Text(
                text = stringResource(R.string.manage_widgets_bottom_sheet_title),
                style = RarimeTheme.typography.h3,
                color = RarimeTheme.colors.textPrimary,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(start = 20.dp, top = 30.dp, bottom = 30.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .padding(end = 20.dp, top = 24.dp, bottom = 24.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = RarimeTheme.colors.componentPrimary)
            ) {
                AppIcon(
                    id = R.drawable.ic_close_fill,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }

        }
        HorizontalPager(state = pagerState) { page ->
            when (Widgets.values()[page]) {
                Widgets.FREEDOMTOOL -> {
                    FreedomtoolWidget(colorScheme = colorScheme)
                }

                Widgets.LIKENESS -> {
                    DigitalLikenessWidget(colorScheme = colorScheme)
                }

                Widgets.HIDDEN_PRIZE -> {
                    HiddenPrizeWidget(colorScheme = colorScheme)
                }

                Widgets.RECOVERY_METHOD -> {
                    RecoveryMethodWidget(colorScheme = colorScheme)
                }

                else -> {}
            }
        }

        HorizontalPageIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            numberOfPages = Widgets.values().size,
            selectedPage = pagerState.currentPage,
            defaultRadius = 6.dp,
            selectedColor = RarimeTheme.colors.primaryMain,
            defaultColor = RarimeTheme.colors.primaryLight,
            selectedLength = 16.dp,
            space = 8.dp
        )

        if (Widgets.values()[pagerState.currentPage].isVisible) {//todo how we can prove card is deleted
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 17.dp)
                    .fillMaxWidth(),
                onClick = onAdd,
                size = ButtonSize.Large,
                text = stringResource(R.string.manage_widgets_add_btn_label)
            )
        } else {
            BaseButton(
                onClick = onRemove,
                text = stringResource(R.string.manage_widgets_remove_btn_label),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.errorLight,
                    contentColor = RarimeTheme.colors.errorDark
                ),
                size = ButtonSize.Large,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 17.dp)
                    .fillMaxWidth()
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun ManageWidgetsBottomSheetPreview() {
    ManageWidgetsBottomSheetContent(
        onClose = {},
        onAdd = {},
        onRemove = {},
        colorScheme = AppColorScheme.LIGHT
    )
}

