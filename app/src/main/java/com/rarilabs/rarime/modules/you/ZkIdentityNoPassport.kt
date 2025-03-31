package com.rarilabs.rarime.modules.you

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.passportScan.ScanPassportScreen
import com.rarilabs.rarime.ui.components.AppBackgroundGradient
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.GetCustomContents
import com.rarilabs.rarime.util.ParseEDocumentFromJson
import com.rarilabs.rarime.util.Screen

enum class IdentityScreenType {
    NONE,
    PASSPORT,
    LIVENESS,
}

data class IdentityItemData(
    val iconId: Int,
    val iconSize: Int,
    val nameResId: Int,
    val isActive: Boolean,
    val onClick: () -> Unit
)

@Composable
fun ZkIdentityNoPassport(
    navigate: (String) -> Unit,
    viewModel: ZkIdentityNoPassportViewModel = hiltViewModel()
) {
    val sheetState = rememberAppSheetState(false)
    var currentScreen by remember { mutableStateOf(IdentityScreenType.NONE) }
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()


    val context = LocalContext.current

    val filePicker =
        rememberLauncherForActivityResult(
            contract = GetCustomContents(isMultiple = false),
            onResult = { uris ->
                try {
                    if (uris.isEmpty()) {
                        return@rememberLauncherForActivityResult
                    }

                    val uri = uris.first()

                    val eDocument =
                        ParseEDocumentFromJson().parseEDocument(uri, context)

                    viewModel.setJsonEDocument(eDocument)
                    //navigate(Screen.ScanPassport.ScanPassportPoints.route)
                } catch (e: Exception) {
                    Log.e("ScanPassportScreen", "Failed to read file", e)
                }
            })

    val identityItems = remember {
        mutableListOf(
            IdentityItemData(
                iconId = R.drawable.ic_passport_line,
                nameResId = R.string.zk_identity_no_passport_list_item_1,
                isActive = true,
                iconSize = 24,
                onClick = {
                    navigate(Screen.ScanPassport.ScanPassportPoints.route)
                }
            ),
            IdentityItemData(
                iconId = R.drawable.ic_body_scan_fill,
                nameResId = R.string.zk_identity_no_passport_list_item_2,
                isActive = false,
                iconSize = 24,
                onClick = {
                    currentScreen = IdentityScreenType.LIVENESS
                    sheetState.show()
                }
            ),
            IdentityItemData(
                iconId = R.drawable.ic_identification_card,
                nameResId = R.string.zk_identity_no_passport_list_item_3,
                isActive = false,
                iconSize = 24,
                onClick = {}
            ),
            IdentityItemData(
                iconId = R.drawable.ic_twitter_x_fill,
                nameResId = R.string.zk_identity_no_passport_list_item_4,
                isActive = false,
                iconSize = 20,
                onClick = {}
            ),
        ).also {
            if (BuildConfig.isTestnet) {
                it.add(
                    IdentityItemData(
                        iconId = R.drawable.ic_passport_fill,
                        nameResId = R.string.from_json_test_only,
                        isActive = true,
                        iconSize = 24,
                        onClick = {
                            filePicker.launch("application/json")
                        }
                    ))
            }
        }
    }

    ZkIdentityNoPassportContent(
        modifier = Modifier,
        navigate = navigate,
        identityItems = identityItems,
        currentScreen = currentScreen,
        sheetState = sheetState,
        innerPaddings = innerPaddings
    )

    AppBottomSheet(
        state = sheetState,
    ) {
        when (currentScreen) {
            IdentityScreenType.PASSPORT -> {
                ScanPassportScreen(onClose = {}, onClaim = {})
            }

            IdentityScreenType.NONE -> {}
            IdentityScreenType.LIVENESS -> ZkLiveness(navigate = navigate)
        }
    }
}

@Composable
fun ZkIdentityNoPassportContent(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    identityItems: List<IdentityItemData>,
    currentScreen: IdentityScreenType,
    sheetState: AppSheetState,
    innerPaddings: Map<ScreenInsets, Number>
) {
    AppBackgroundGradient()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp + 20.dp,
                bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp,
            )
            .padding(horizontal = 20.dp)
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
            color = RarimeTheme.colors.textSecondary
        )

        IdentityList(items = identityItems)
    }
}

@Composable
fun IdentityList(items: List<IdentityItemData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(24.dp)),
    ) {
        items.forEachIndexed { index, item ->
            IdentityCardTypeItem(
                iconId = item.iconId,
                iconSize = item.iconSize,
                name = stringResource(item.nameResId),
                isActive = item.isActive,
                onClick = item.onClick
            )
            if (index < items.lastIndex) {
                HorizontalDivider(
                    color = RarimeTheme.colors.componentPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ZkIdentityNoPassportPreview() {
    val mockNavigate: (String) -> Unit = {}
    val mockIdentityItems = listOf(
        IdentityItemData(
            iconId = R.drawable.ic_passport_line,
            iconSize = 24,
            nameResId = R.string.zk_identity_no_passport_list_item_1,
            isActive = true,
            onClick = {}
        )
    )
    val mockInnerPaddings = mapOf(ScreenInsets.TOP to 20, ScreenInsets.BOTTOM to 20)

    ZkIdentityNoPassportContent(
        modifier = Modifier,
        navigate = mockNavigate,
        identityItems = mockIdentityItems,
        currentScreen = IdentityScreenType.NONE,
        sheetState = rememberAppSheetState(false),
        innerPaddings = mockInnerPaddings
    )
}
