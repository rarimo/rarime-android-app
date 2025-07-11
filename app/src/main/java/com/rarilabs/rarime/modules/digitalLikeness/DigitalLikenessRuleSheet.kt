package com.rarilabs.rarime.modules.digitalLikeness

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.LikenessRule
import com.rarilabs.rarime.modules.home.v3.ui.expanded.RuleOptionData
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun DigitalLikenessRuleSheet(
    state: AppSheetState,
    selectedRule: LikenessRule?,
    modifier: Modifier = Modifier,
    onSave: (LikenessRule) -> Unit,
) {

    var localSelectedRule: LikenessRule? by remember {
        mutableStateOf(selectedRule)
    }

    LaunchedEffect(state.showSheet) {
        if (!state.showSheet) {
            localSelectedRule = selectedRule
        }
    }

    val rules = listOf(
        RuleOptionData(
            isSelected = LikenessRule.USE_AND_PAY == localSelectedRule,
            type = LikenessRule.USE_AND_PAY,
            title = "Use my likeness and pay me",
            badgeText = "Soon",
            iconRes = R.drawable.ic_money_dollar_circle_line
        ), RuleOptionData(
            isSelected = LikenessRule.NOT_USE == localSelectedRule,
            type = LikenessRule.NOT_USE,
            title = "Don’t use my face at all",
            badgeText = "Soon",
            iconRes = R.drawable.ic_subtract_fill
        ), RuleOptionData(
            isSelected = LikenessRule.ASK_FIRST == localSelectedRule,
            type = LikenessRule.ASK_FIRST,
            title = "Ask me first",
            badgeText = "Soon",
            iconRes = R.drawable.ic_question
        )
    )

    AppBottomSheet(modifier, state, isHeaderEnabled = false) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
                ) {
                    Text(
                        "Set a rule",
                        style = RarimeTheme.typography.h2,
                        color = RarimeTheme.colors.textPrimary
                    )

                    Text(
                        "The rules are yours to change",
                        color = RarimeTheme.colors.textSecondary
                    )
                }
                IconButton(onClick = { state.hide() }) {
                    Icon(
                        tint = RarimeTheme.colors.textPrimary,
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close"
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = rememberLazyListState(),
                contentPadding = PaddingValues(vertical = 17.dp),
                reverseLayout = false,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior = ScrollableDefaults.flingBehavior(),
                userScrollEnabled = true
            ) {
                items(rules.size) { index ->
                    RuleOption(
                        item = rules[index], onClick = { likenessRule ->
                            localSelectedRule = likenessRule
                        })
                }
            }
            Spacer(Modifier.height(17.dp))
            PrimaryButton(
                text = if (localSelectedRule == null) "Set a rule" else "Save",
                enabled = localSelectedRule != null,
                size = ButtonSize.Large,
                onClick = { onSave(localSelectedRule!!) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RuleOption(
    item: RuleOptionData,
    onClick: (LikenessRule) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconBackground = if (item.isSelected) {
        RarimeTheme.colors.textPrimary
    } else {
        RarimeTheme.colors.componentPrimary
    }

    val iconTint = if (item.isSelected) {
        RarimeTheme.colors.invertedLight
    } else {
        RarimeTheme.colors.textPrimary
    }

    val cardContainerColor = if (item.isSelected) {
        RarimeTheme.colors.componentPrimary
    } else {
        RarimeTheme.colors.backgroundSurface1
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = { onClick(item.type) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, RarimeTheme.colors.componentPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, shape = CircleShape)
                    .padding(vertical = 2.dp, horizontal = 4.dp)
            ) {
                Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier.width(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Status badge
                item.badgeText?.let { text ->
                    Text(
                        text = text.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RarimeTheme.colors.infoDarker,
                        modifier = Modifier
                            .background(
                                color = RarimeTheme.colors.infoLighter,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = item.title,
                    style = RarimeTheme.typography.subtitle6,
                    color = RarimeTheme.colors.textPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun RuleOptionPreview() {
    RuleOption(
        item = RuleOptionData(
            isSelected = false,
            type = LikenessRule.ASK_FIRST,
            title = "Use my likeness and pay me.",
            badgeText = "Soon",
            iconRes = R.drawable.ic_money_dollar_circle_line
        ), onClick = {})
}

@Preview
@Composable
fun DigitalLikenessRuleSheetPreview() {
    val ruleSheetState = rememberAppSheetState(true)
    DigitalLikenessRuleSheet(
        ruleSheetState,
        selectedRule = null,
        //LikenessRule.ASK_FIRST,
        onSave = { ruleSheetState.hide() })
}