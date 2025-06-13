package com.rarilabs.rarime.modules.manageWidgets

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
    val managedWidgets by viewModel.managedWidgets.collectAsState()
    val visibleWidgets by viewModel.visibleWidgets.collectAsState()

    ManageWidgetsBottomSheetContent(
        onClose = {
            viewModel.setVisibleWidgets()
            onClose()
        },
        onRemove = { widgetType -> viewModel.remove(widgetType) },
        onAdd = { widgetType -> viewModel.add(widgetType) },
        colorScheme = colorScheme,
        managedWidgets = managedWidgets,
        visibleWidgets = visibleWidgets,
        isVisible = { widgetType -> viewModel.isVisible(widgetType) }
    )

}

@Composable
fun ManageWidgetsBottomSheetContent(
    onClose: () -> Unit,
    onRemove: (WidgetType) -> Unit,
    onAdd: (WidgetType) -> Unit,
    colorScheme: AppColorScheme,
    managedWidgets: List<WidgetType>,
    visibleWidgets: List<WidgetType>,
    isVisible: (WidgetType) -> Boolean
) {
    val pagerState = rememberPagerState(pageCount = { managedWidgets.size })
    val isDark = when (colorScheme) {
        AppColorScheme.SYSTEM -> isSystemInDarkTheme()
        AppColorScheme.DARK -> true
        AppColorScheme.LIGHT -> false
    }
    Column {
        Row {
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
            when (managedWidgets.get(page)) {
                WidgetType.FREEDOMTOOL -> {
                    ManageWidgetsItem(
                        imageResId =
                            if (isDark) {
                                R.drawable.ic_freedomtool_widget_dark
                            } else {
                                R.drawable.ic_freedomtool_widget
                            },
                        title = stringResource(R.string.freedomtool_widget_title),
                        description = stringResource(R.string.freedomtool_widget_description)
                    )
                }

//                WidgetType.LIKENESS -> {
//                    ManageWidgetsItem(
//                        imageResId =
//                            if (isDark) {
//                                R.drawable.ic_digital_likeness_widget_dark
//                            } else {
//                                R.drawable.ic_digital_likeness_widget_light
//                            },
//                        title = stringResource(R.string.digital_likeness_widget_title),
//                        description = stringResource(R.string.digital_likeness_widget_description)
//                    )
//                }

                WidgetType.HIDDEN_PRIZE -> {
                    ManageWidgetsItem(
                        imageResId =
                            if (isDark) {
                                R.drawable.ic_hidden_keys_widget_dark
                            } else {
                                R.drawable.ic_hidden_keys_widget
                            },
                        title = stringResource(R.string.hidden_prize_widget_title),
                        description = stringResource(R.string.hidden_prize_widget_description)
                    )
                }

                WidgetType.RECOVERY_METHOD -> {
                    ManageWidgetsItem(
                        imageResId =
                            if (isDark) {
                                R.drawable.ic_recovery_method_widget_dark
                            } else {
                                R.drawable.ic_recovery_method_widget
                            },
                        title = stringResource(R.string.recovery_method_widget_title),
                        description = stringResource(R.string.recovery_method_widget_description)
                    )
                }
                // todo implement all managing widgets
                else -> {}
            }
        }

        HorizontalPageIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            numberOfPages = managedWidgets.size,
            selectedPage = pagerState.currentPage,
            defaultRadius = 6.dp,
            selectedColor = RarimeTheme.colors.primaryMain,
            defaultColor = RarimeTheme.colors.primaryLight,
            selectedLength = 16.dp,
            space = 8.dp
        )

    }
    if (isVisible(managedWidgets[pagerState.currentPage])) {
        SecondaryButton(
            onClick = { onRemove(managedWidgets[pagerState.currentPage]) },
            text = stringResource(R.string.manage_widgets_remove_btn_label),
            size = ButtonSize.Large,
            enabled = managedWidgets[pagerState.currentPage] != WidgetType.RECOVERY_METHOD,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 17.dp)
                .fillMaxWidth()
        )
    } else {
        PrimaryButton(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 17.dp)
                .fillMaxWidth(),
            onClick = { onAdd(managedWidgets[pagerState.currentPage]) },
            size = ButtonSize.Large,
            text = stringResource(R.string.manage_widgets_add_btn_label)
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
        managedWidgets = WidgetType.values().toList(),
        visibleWidgets = WidgetType.values().toList()
    )
}

