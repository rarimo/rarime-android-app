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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.model.CardType
import com.rarilabs.rarime.modules.manageWidgets.widgets.DigitalLikenessWidget
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalPageIndicator
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class Widgets(val id:Int,val title: String, val description: String, val isVisible: Boolean,){
    FREEDOMTOOL(0,"title","description",true),
    IDENTITY(1,"","",true),
    LIKENESS(2,"","",true),
    CLAIM(3,"","",true),
    HIDDEN_PRIZE(4,"","",true),
    RECOVERY_METHOD(5,"","",true),

}

@Composable
fun ManageWidgetsBottomSheet(
    onClose: () -> Unit,
    onRemove: () -> Unit,
    onAdd: ()  -> Unit
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
                    id = R.drawable.ic_close,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }

        }
        HorizontalPager(state = pagerState) { page ->
            when (Widgets.values()[page]) {
                Widgets.FREEDOMTOOL ->{}
                Widgets.IDENTITY -> {
                    DigitalLikenessWidget()

                }

                Widgets.LIKENESS -> {}
                Widgets.CLAIM -> {}
                Widgets.HIDDEN_PRIZE -> {}
                Widgets.RECOVERY_METHOD -> {}
            }
        }

            Text(
                text = Widgets.values()[pagerState.currentPage].title,
                style = RarimeTheme.typography.h3,
                color = RarimeTheme.colors.textPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp, bottom = 20.dp, )
            )
            Text(text = Widgets.values()[pagerState.currentPage].description,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 40.dp)
            )


        HorizontalPageIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            numberOfPages = CardType.values().size,
            selectedPage = pagerState.currentPage,
            defaultRadius = 6.dp,
            selectedColor = RarimeTheme.colors.primaryMain,
            defaultColor = RarimeTheme.colors.primaryLight,
            selectedLength = 16.dp,
            space = 8.dp
        )

        if (Widgets.values()[pagerState.currentPage].isVisible) {//todo how we can prove i card is deleted
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 17.dp)
                    .fillMaxWidth(),
                onClick = onAdd,
                text = stringResource(R.string.manage_widgets_add_btn_label)
            )
        } else {
            BaseButton(onClick = onRemove,
                text = stringResource(R.string.manage_widgets_remove_btn_label),
                colors = ButtonDefaults.buttonColors(containerColor = RarimeTheme.colors.errorLight,
                    contentColor = RarimeTheme.colors.errorDark),
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
    ManageWidgetsBottomSheet(onClose = {}, onAdd ={}, onRemove = {})
}

