package com.distributedLab.rarime.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AppWebView(url: String, title: String, onBack: () -> Unit) {
    val webViewBgColor = RarimeTheme.colors.backgroundPrimary.toArgb()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            PrimaryTextButton(
                leftIcon = R.drawable.ic_caret_left,
                onClick = onBack
            )
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    clipToOutline = true
                    setBackgroundColor(webViewBgColor)
                    loadUrl(url)
                }
            },
            update = {
                it.loadUrl(url)
            }
        )
    }
}

@Preview
@Composable
fun AppWebViewPreview() {
    AppWebView(
        url = "https://www.google.com",
        title = "Web View",
        onBack = {}
    )
}
