package com.distributedLab.rarime.feature.home

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppScreen()
            }
        }
    }
}

@Composable
fun AppScreen() {
    val textFieldState = rememberAppTextFieldState("")
    var switchState = rememberAppSwitchState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = RarimeTheme.colors.backgroundPrimary
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            List()
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
        }
    }
}

@Composable
fun List(homeViewModel: HomeViewModel = viewModel()) {
    LazyColumn {
        items(homeViewModel.templateData.getOrThrow()) { item ->
            Greeting(item)
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