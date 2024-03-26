package com.distributedLab.rarime.modules.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSwitch
import com.distributedLab.rarime.ui.components.AppTextField
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppSwitchState
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.AppTheme
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun HomeScreen() {
    val textFieldState = rememberAppTextFieldState("")
    val switchState = rememberAppSwitchState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        AppTextField(
            state = textFieldState,
            label = "Text Field",
            placeholder = "Enter some text",
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppSwitch(state = switchState)
            Text(
                text = "Switch",
                modifier = Modifier.padding(8.dp, 0.dp),
                color = RarimeTheme.colors.textPrimary,
                style = RarimeTheme.typography.subtitle4
            )
        }
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            size = ButtonSize.Large,
            text = "Rarime Button",
            leftIcon = R.drawable.ic_rarime,
            onClick = {
                textFieldState.updateErrorMessage("Some error message")
            })

        for (i in 0..20) {
            Text("Lorem ipsum dolor sit amet, consectetur adipiscing $i")
        }
    }
}

@Composable
fun List(homeViewModel: HomeViewModel = viewModel()) {
    LazyColumn {
        items(10) { item ->
            Greeting(item.toString())
        }
    }
}

@Composable
fun Greeting(item: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppIcon(
            id = R.drawable.ic_rarime,
            size = 24.dp,
            tint = RarimeTheme.colors.textPrimary
        )
        Text(
            text = item,
            modifier = Modifier.padding(4.dp, 0.dp),
            color = RarimeTheme.colors.textPrimary,
            style = RarimeTheme.typography.subtitle1
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview() {
    AppTheme {
        Surface(color = RarimeTheme.colors.backgroundPrimary) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Greeting(item = "user")
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Rarime Button",
                    leftIcon = R.drawable.ic_rarime,
                    onClick = { /*TODO*/ }) {}
            }
        }
    }
}