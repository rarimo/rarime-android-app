package com.rarilabs.rarime.modules.digitalLikeness

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
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
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun DigitalLikenessCamera(
    modifier: Modifier = Modifier,
    onNext: (Bitmap) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val cameraProvider = remember {
        ProcessCameraProvider.getInstance(context).get()
    }

    LaunchedEffect(Unit) {
        val previewUseCase = androidx.camera.core.Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, previewUseCase
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {

        if (selectedBitmap != null) {
            selectedBitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            AndroidView(
                factory = { previewView }, modifier = Modifier.fillMaxSize()
            )
        }

        CameraMask()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon(
                id = R.drawable.ic_face_scan, size = 40.dp, tint = RarimeTheme.colors.baseWhite
            )
            Text(
                text = "Turn your head slightly to the left",
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.baseWhite
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Your face never leaves the device. You create an anonymous record that carries your rules, so AI knows how to treat you.",
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )

            if (selectedBitmap == null) {

                PrimaryButton(
                    onClick = {
                        // Freeze camera by capturing current frame
                        selectedBitmap = previewView.bitmap
                    }, text = "Photo"
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    PrimaryButton(
                        modifier = modifier.weight(1f),
                        leftIcon = R.drawable.ic_restart_line,
                        onClick = {
                            // Unfreeze: clear captured frame
                            selectedBitmap = null
                        },
                    )

                    PrimaryButton(
                        text = "Continue",
                        onClick = {
                            onNext(selectedBitmap!!)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraMask(
    aspectRatio: Float = 395f / 290f, horizontalPadding: Dp = 50.dp, topPadding: Dp = 200.dp
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(2.dp)
    ) {
        val hpPx = horizontalPadding.toPx()
        val ovalW = size.width - 2f * hpPx
        val ovalH = ovalW * aspectRatio
        val topPx = topPadding.toPx()

        val path = Path().apply {
            addOval(
                Rect(offset = Offset(hpPx, topPx), size = Size(ovalW, ovalH))
            )
        }

        clipPath(path, clipOp = ClipOp.Difference) {
            drawRect(color = Color.Black.copy(alpha = 0.6f), size = size)
        }
    }
}

@Preview
@Composable
private fun DigitalLikenessCameraPreview() {
    Surface {
        DigitalLikenessCamera {}
    }
}
