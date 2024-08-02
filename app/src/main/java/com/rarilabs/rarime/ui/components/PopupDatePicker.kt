package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil.convertToDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupDatePicker(
    state: DatePickerState = rememberDatePickerState(),
    togglerContent: @Composable (toggle: () -> Unit) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var isDialogShown by remember { mutableStateOf(false) }

    LaunchedEffect(state.selectedDateMillis) {
        scope.launch {
            delay(300)
            isDialogShown = false
        }
    }

    togglerContent { isDialogShown = !isDialogShown }

    if (isDialogShown) {
        DatePickerDialog(
            onDismissRequest = { isDialogShown = false },
            confirmButton = {},
            dismissButton = {}) {
            DatePicker(
                state = state,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldTypeDatePicker(
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    placeholder: String = stringResource(id = R.string.field_type_date_picker_placeholder),
) {
    var text by remember { mutableStateOf(placeholder) }



    LaunchedEffect(state.selectedDateMillis) {
        text = if (state.selectedDateMillis == null) {
            placeholder
        } else convertToDate(state.selectedDateMillis)
    }

    PopupDatePicker(state) { toggle ->
        Row(modifier = Modifier
            .clickable { toggle() }
            .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(12.dp))
            .defaultMinSize(
                minWidth = OutlinedTextFieldDefaults.MinWidth,
                minHeight = OutlinedTextFieldDefaults.MinHeight
            )
            .padding(OutlinedTextFieldDefaults.contentPadding())
            .then(modifier),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = text,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )
            AppIcon(
                id = R.drawable.ic_calendar_blank,
                size = 18.dp,
                tint = RarimeTheme.colors.textSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PopupDatePickerPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        FieldTypeDatePicker()
    }
}