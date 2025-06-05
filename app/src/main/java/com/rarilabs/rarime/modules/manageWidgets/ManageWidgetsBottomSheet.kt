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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.manageWidgets.widgets.DigitalLikenessWidget
import com.rarilabs.rarime.modules.manageWidgets.widgets.FreedomtoolWidget
import com.rarilabs.rarime.modules.manageWidgets.widgets.HiddenPrizeWidget
import com.rarilabs.rarime.modules.manageWidgets.widgets.RecoveryMethodWidget
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalPageIndicator
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SecondaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Composable
fun ManageWidgetsBottomSheet(
    onClose: () -> Unit,
    viewModel: ManageWidgetsViewModel = hiltViewModel()
) {
    val colorScheme by viewModel.colorScheme.collectAsState()
    val managedCards by viewModel.managedCards.collectAsState()
    val visibleCards by viewModel.visisbleCards.collectAsState()

    ManageWidgetsBottomSheetContent(
        onClose = {
            viewModel.setVisibleCard()
            onClose()
        },
        onRemove = { cardType -> viewModel.remove(cardType) },
        onAdd = { cardType -> viewModel.add(cardType) },
        colorScheme = colorScheme,
        managedCards = managedCards,
        visibleCards = visibleCards,
        isVisible = { cardType -> viewModel.isVisible(cardType) }
    )

}

@Composable
fun ManageWidgetsBottomSheetContent(
    onClose: () -> Unit,
    onRemove: (WidgetType) -> Unit,
    onAdd: (WidgetType) -> Unit,
    colorScheme: AppColorScheme,
    managedCards: List<WidgetType>,
    visibleCards: List<WidgetType>,
    isVisible: (WidgetType) -> Boolean
) {
    val pagerState = rememberPagerState(pageCount = { managedCards.size })
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
            when (managedCards.get(page)) {
                WidgetType.FREEDOMTOOL -> {
                    FreedomtoolWidget(colorScheme = colorScheme)
                }

                WidgetType.LIKENESS -> {
                    DigitalLikenessWidget(colorScheme = colorScheme)
                }

                WidgetType.HIDDEN_PRIZE -> {
                    HiddenPrizeWidget(colorScheme = colorScheme)
                }

                WidgetType.RECOVERY_METHOD -> {
                    RecoveryMethodWidget(colorScheme = colorScheme)
                }
                // todo implement all managing cards
                else -> {}
            }
        }

        HorizontalPageIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            numberOfPages = managedCards.size,
            selectedPage = pagerState.currentPage,
            defaultRadius = 6.dp,
            selectedColor = RarimeTheme.colors.primaryMain,
            defaultColor = RarimeTheme.colors.primaryLight,
            selectedLength = 16.dp,
            space = 8.dp
        )

    }
    if (!isVisible(managedCards.get(pagerState.currentPage))) {
        PrimaryButton(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 17.dp)
                .fillMaxWidth(),
            onClick = { onAdd(managedCards.get(pagerState.currentPage)) },
            size = ButtonSize.Large,
            text = stringResource(R.string.manage_widgets_add_btn_label)
        )
    } else {
        SecondaryButton(
            onClick = { onRemove(managedCards.get(pagerState.currentPage)) },
            text = stringResource(R.string.manage_widgets_remove_btn_label),
            size = ButtonSize.Large,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 17.dp)
                .fillMaxWidth()
        )
    }

}


@Preview(showBackground = true)
@Composable
fun ManageWidgetsBottomSheetPreview() {
    ManageWidgetsBottomSheetContent(
        onClose = {},
        onAdd = {},
        onRemove = {},
        colorScheme = AppColorScheme.LIGHT,
        isVisible = { true },
        managedCards = WidgetType.values().toList(),
        visibleCards = WidgetType.values().toList()
    )
}

