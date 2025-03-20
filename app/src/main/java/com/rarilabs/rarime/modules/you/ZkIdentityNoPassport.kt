package com.rarilabs.rarime.modules.you

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class IdentityScreenType {
    NONE,
    LIVENESS,
}

data class IdentityItemData(
    val imageId: Int,
    val nameResId: Int,
    val isActive: Boolean,
    val onClick: () -> Unit
)

@Composable
fun ZkIdentityNoPassport(modifier: Modifier = Modifier, navigate: (String) -> Unit) {
    val guideSheetState = rememberAppSheetState(false)
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()
    var currentScreen by remember { mutableStateOf(IdentityScreenType.NONE) }

    AppBottomSheet(
        state = guideSheetState,
    ) {
        when (currentScreen) {
            IdentityScreenType.NONE -> {}
            IdentityScreenType.LIVENESS -> ZkLiveness(navigate = navigate)
            //  TODO: Implement other sheets
        }
    }

    val identityItems = remember {
        listOf(
            IdentityItemData(
                imageId = R.drawable.ic_passport_line,
                nameResId = R.string.zk_identity_no_passport_list_item_1,
                isActive = true,
                onClick = {}
            ),
            IdentityItemData(
                imageId = R.drawable.ic_body_scan_fill,
                nameResId = R.string.zk_identity_no_passport_list_item_2,
                isActive = true,
                onClick = {
                    currentScreen = IdentityScreenType.LIVENESS
                    guideSheetState.show()
                }
            ),
            IdentityItemData(
                imageId = R.drawable.ic_rarimo,
                nameResId = R.string.zk_identity_no_passport_list_item_3,
                isActive = false,
                onClick = {}
            ),
            IdentityItemData(
                imageId = R.drawable.ic_rarimo,
                nameResId = R.string.zk_identity_no_passport_list_item_4,
                isActive = false,
                onClick = {}
            ),
            IdentityItemData(
                imageId = R.drawable.ic_rarimo,
                nameResId = R.string.zk_identity_no_passport_list_item_5,
                isActive = false,
                onClick = {}
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp,
                top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp + 70.dp
            )
            .then(modifier)
    ) {
        Text(
            stringResource(R.string.zk_identity_no_passport_title_1),
            style = RarimeTheme.typography.h1,
            color = RarimeTheme.colors.textPrimary
        )
        Text(
            stringResource(R.string.zk_identity_no_passport_title_2),
            style = RarimeTheme.typography.additional1,
            color = RarimeTheme.colors.successMain
        )

        Spacer(modifier = Modifier.height(88.dp))

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.zk_identity_no_passport_list_caption),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary
        )

        IdentityList(items = identityItems)
    }
}

@Composable
fun IdentityList(items: List<IdentityItemData>) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEachIndexed { index, item ->
            IdentityCardTypeItem(
                imageId = item.imageId,
                name = stringResource(item.nameResId),
                isActive = item.isActive,
                onClick = item.onClick
            )
            if (index < items.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
private fun ZkIdentityNoPassportPreview() {
    Surface {
        ZkIdentityNoPassport {}
    }
}
