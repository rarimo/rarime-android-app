package com.rarilabs.rarime.modules.digitalLikeness

import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun DigitalLikenessCamera(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // bind the camera once
    LaunchedEffect(Unit) {

        // make sure permission is granted (you can request it before calling this Composable)
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val previewUseCase = androidx.camera.core.Preview.Builder()
            .build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                previewUseCase
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        CameraMask()


        // Overlay UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon(
                id = R.drawable.ic_face_scan,
                size = 40.dp,
                tint = RarimeTheme.colors.baseWhite
            )
            Text(
                text = "Turn your head slightly to the left",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )


            Spacer(modifier = Modifier.weight(1f))

            // Bottom explanatory text
            Text(
                text = "Your face never leaves the device. You create an anonymous record that carries your rules, so AI knows how to treat you.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun CameraMask(
    aspectRatio: Float = 395f / 290f,      // height : width now for vertical oval
    horizontalPadding: Dp = 50.dp,
    topPadding: Dp = 200.dp
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val hpPx = horizontalPadding.toPx()
        val ovalWidth = size.width - 2f * hpPx
        val ovalHeight = ovalWidth * aspectRatio
        val topPx = topPadding.toPx()

        val ovalRect = Rect(
            offset = Offset(hpPx, topPx),
            size = Size(ovalWidth, ovalHeight)
        )

        val path = Path().apply { addOval(ovalRect) }

        clipPath(path, clipOp = ClipOp.Difference) {
            drawRect(
                color = Color.Black.copy(alpha = 0.7f),
                size = size
            )
        }
    }
}

@Preview
@Composable
private fun DigitalLikenessCameraPreview() {
    Surface {
        DigitalLikenessCamera()
    }
}