package com.rarilabs.rarime.modules.qr

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ScanQrScreen(
    onBack: () -> Unit,
    onScan: (String) -> Unit,
    innerPaddings: Map<ScreenInsets, Number> = mapOf(
        ScreenInsets.TOP to 0,
        ScreenInsets.BOTTOM to 0
    )
) {
    ScanQrScreenContent(onBack, onScan, innerPaddings)
}

@Composable
fun ScanQrScreenContent(
    onBack: () -> Unit,
    onScan: (String) -> Unit,
    innerPaddings: Map<ScreenInsets, Number>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        ScanQrProcessor(
            onCompletion = onScan
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            CameraMask(boxSize = 300.dp)
            AppIcon(
                id = R.drawable.ic_caret_left,
                size = 20.dp,
                tint = RarimeTheme.colors.baseWhite,
                modifier = Modifier
                    .padding(
                        top = (20.0 + innerPaddings[ScreenInsets.TOP]!!.toInt()).dp,
                        start = 20.dp
                    )
                    .clickable { onBack() }
            )
            Text(
                text = stringResource(R.string.scan_qr_title),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.baseWhite,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (20.0 + innerPaddings[ScreenInsets.TOP]!!.toInt()).dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qr_frame),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
                Text(
                    text = stringResource(R.string.scan_qr_description),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Composable
private fun CameraMask(
    boxSize: Dp = 220.dp, topPadding: Dp = 200.dp
) {
    val cornerRadius = 8.dp

    Canvas(modifier = Modifier.fillMaxSize()) {
        clipPath(
            clipOp = ClipOp.Difference,
            path = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = size.width / 2 - (boxSize / 2).toPx(),
                        top = topPadding.toPx(),
                        right = size.width / 2 + (boxSize / 2).toPx(),
                        bottom = (topPadding + boxSize).toPx(),
                        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                    )
                )
            }
        ) {
            drawRect(
                color = Color.Black.copy(alpha = 0.7f),
                topLeft = Offset(-size.width / 2, 0f),
                size = Size(size.width * 2, size.height * 2),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScanQrScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        ScanQrScreen(onScan = {}, onBack = {})
    }
}