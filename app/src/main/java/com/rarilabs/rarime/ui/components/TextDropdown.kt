package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

data class DropdownOption(
    val value: String,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextDropdown(
    modifier: Modifier = Modifier,
    options: List<DropdownOption>,
    value: String,
    label: String? = null,
    onChange: (String) -> Unit
) {
    var isDropdownExpanded by remember {
        mutableStateOf(false)
    }

    fun toggleDropdown() {
        isDropdownExpanded = !isDropdownExpanded
    }

    fun closeDropdown() {
        isDropdownExpanded = false
    }

    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = {}
    ) {
        Row(modifier = Modifier.menuAnchor()) {
            Row(
                modifier = modifier
                    .clickable { toggleDropdown() },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = options.find { it.value == value }?.label ?: label ?: "",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textPrimary,
                )

                AppIcon(
                    modifier = Modifier
                        .rotate(if (isDropdownExpanded) 180f else 0f),
                    id = R.drawable.ic_carret_down,
                    size = 12.dp,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }
        }

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp)),
        ) {
            ExposedDropdownMenu(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth(0.5f)
                    .background(RarimeTheme.colors.backgroundPure)
                    .padding(0.dp),
                expanded = isDropdownExpanded,
                onDismissRequest = { closeDropdown() },
            ) {
                options.forEachIndexed { idx, option ->
                    run {
                        DropdownMenuItem(
                            modifier = Modifier.background(RarimeTheme.colors.backgroundPure),
                            trailingIcon = {
                                if (option.value == value) AppIcon(
                                    id = R.drawable.ic_check,
                                    size = 18.dp,
                                    tint = RarimeTheme.colors.textPrimary
                                ) else null
                            },
                            colors = MenuItemColors(
                                textColor = MenuDefaults.itemColors().textColor,
                                leadingIconColor = MenuDefaults.itemColors().leadingIconColor,
                                trailingIconColor = MenuDefaults.itemColors().trailingIconColor,
                                disabledTextColor = MenuDefaults.itemColors().disabledTextColor,
                                disabledLeadingIconColor = MenuDefaults.itemColors().disabledLeadingIconColor,
                                disabledTrailingIconColor = MenuDefaults.itemColors().disabledTrailingIconColor,
                            ),
                            text = {
                                Text(
                                    text = option.label,
                                    color = RarimeTheme.colors.textPrimary
                                )
                            },
                            onClick = {
                                onChange(option.value)
                                closeDropdown()
                            }
                        )
                        if (idx < options.size - 1) {
                            HorizontalDivider(color = RarimeTheme.colors.backgroundPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TextDropdownPreview() {
    val options = listOf(
        DropdownOption("1", "Option 1"),
        DropdownOption("2", "Option 2"),
        DropdownOption("3", "Option 3"),
    )
    var value by remember { mutableStateOf(options.first().value) }


    CardContainer(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                TextDropdown(
                    options = options,
                    value = value,
                    onChange = { value = it }
                )
            }

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                TextDropdown(
                    options = options,
                    value = "",
                    label = "Select an option",
                    onChange = { value = it }
                )
            }
        }
    }
}