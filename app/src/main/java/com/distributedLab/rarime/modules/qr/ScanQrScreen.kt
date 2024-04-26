package com.distributedLab.rarime.modules.qr

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun ScanQrScreen(onBack: () -> Unit, onScan: (String) -> Unit) {
    // TODO: Implement QR code scanning
    Box(modifier = Modifier.fillMaxSize()) {
        CameraMask()
        AppIcon(
            id = R.drawable.ic_caret_left,
            size = 20.dp,
            tint = RarimeTheme.colors.baseWhite,
            modifier = Modifier
                .padding(20.dp)
                .clickable { onBack() }
        )
        Text(
            text = stringResource(R.string.scan_qr_title),
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.baseWhite,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(21.dp)
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
                modifier = Modifier.size(222.dp)
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

@Composable
private fun CameraMask() {
    val boxSize = 220.dp
    val topPadding = 200.dp
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
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScanQrScreen() {
    ScanQrScreen({}, {})
}
