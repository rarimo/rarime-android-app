package com.rarilabs.rarime.modules.hiddenPrize

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


@OptIn(ExperimentalGetImage::class)
@Composable
fun HiddenPrizeCamera(
    modifier: Modifier = Modifier,
    onNext: (Bitmap) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val meshDetector: FaceMeshDetector = remember {
        FaceMeshDetection.getClient()
    }

    var imageSize by remember { mutableStateOf(Size.Zero) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var detectedMeshes by remember { mutableStateOf<List<FaceMesh>>(emptyList()) }

    val scope = rememberCoroutineScope()

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val previewView = remember {
        return@remember PreviewView(context).apply {
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
                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, previewUseCase
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val analysisUseCase =
            ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val rotation = imageProxy.imageInfo.rotationDegrees
                            val origW = mediaImage.width
                            val origH = mediaImage.height
                            val (rotW, rotH) = if (rotation == 90 || rotation == 270) {
                                origH to origW
                            } else {
                                origW to origH
                            }
                            imageSize = Size(rotW.toFloat(), rotH.toFloat())

                            val inputImage = InputImage.fromMediaImage(mediaImage, rotation)
                            meshDetector.process(inputImage).addOnSuccessListener { faceMeshList ->
                                detectedMeshes = faceMeshList
                            }.addOnCompleteListener {
                                imageProxy.close()
                            }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, previewUseCase, analysisUseCase
        )
    }


    Box(
        modifier = modifier.fillMaxSize()
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

        Canvas(modifier = Modifier.fillMaxSize()) {
            val viewW = size.width
            val viewH = size.height

            if (imageSize.width == 0f || imageSize.height == 0f) return@Canvas


            val scale = maxOf(
                viewW / imageSize.width, viewH / imageSize.height
            )
            val scaledW = imageSize.width * scale
            val scaledH = imageSize.height * scale


            val dx = (scaledW - viewW) / 2f
            val dy = (scaledH - viewH) / 2f


            detectedMeshes.forEach { faceMesh ->

                faceMesh.allTriangles.forEachIndexed { idx, tri ->


                    val pts = tri.allPoints
                    if (pts.size == 3) {

                        val mapped = pts.map { p ->
                            val rawX = p.position.x * scale - dx
                            val rawY = p.position.y * scale - dy

                            Offset(viewW - rawX, rawY)
                        }

                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(mapped[0].x, mapped[0].y)
                            lineTo(mapped[1].x, mapped[1].y)
                            lineTo(mapped[2].x, mapped[2].y)
                            close()
                        }
                        mapped.forEach { vertex ->
                            drawCircle(
                                center = vertex,
                                radius = 2.dp.toPx(),
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }


                        drawPath(
                            path = path,
                            color = Color.White.copy(alpha = 0.5f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                }


            }


        }

        //CameraMask()

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
                text = "Keep your face in the frame",
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

            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (selectedBitmap == null) {

                    PrimaryButton(
                        size = ButtonSize.Large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        onClick = {
                            scope.launch {
                                selectedBitmap = previewView.bitmap
                            }

                        },
                        text = "Photo"
                    )
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PrimaryButton(
                            modifier = Modifier.weight(3f),
                            size = ButtonSize.Large,
                            leftIcon = R.drawable.ic_restart_line,
                            onClick = {
                                // Unfreeze: clear captured frame
                                selectedBitmap = null
                            },
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        PrimaryButton(
                            modifier = Modifier.weight(7f),
                            size = ButtonSize.Large,
                            text = "Continue",
                            onClick = {
//                                val density = context.resources.displayMetrics.density
//                                val horizontalPaddingPx = 50f * density
//                                val topPaddingPx = 200f * density

                                onNext(selectedBitmap!!)

//                                    cropBitmapToOval(
//                                    src = selectedBitmap!!,
//                                    aspectRatio = 395f / 290f,
//                                    horizontalPaddingPx = horizontalPaddingPx,
//                                    topPaddingPx = topPaddingPx
//                                )
                                //onNext(bitmap)
                            })
                    }
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


        val path = androidx.compose.ui.graphics.Path().apply {
            addOval(
                Rect(offset = Offset(hpPx, topPx), size = Size(ovalW, ovalH))
            )
        }

        clipPath(path, clipOp = ClipOp.Difference) {
            drawRect(color = Color.Black.copy(alpha = 0.6f), size = size)
        }
    }
}

//
//@SuppressLint("UseKtx")
//fun cropBitmapToOval(
//    src: Bitmap,
//    aspectRatio: Float = 395f / 290f,
//    horizontalPaddingPx: Float = 50f,
//    topPaddingPx: Float = 200f
//): Bitmap {
//    val w = src.width
//    val h = src.height
//
//    val output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(output)
//    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
//
//    canvas.drawBitmap(src, 0f, 0f, paint)
//
//    val ovalW = w - 2f * horizontalPaddingPx
//    val ovalH = ovalW * aspectRatio
//    val rectF = RectF(
//        horizontalPaddingPx, topPaddingPx, horizontalPaddingPx + ovalW, topPaddingPx + ovalH
//    )
//    val maskPath = Path().apply {
//        addOval(rectF, Path.Direction.CW)
//    }
//    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
//    canvas.drawPath(maskPath, paint)
//    paint.xfermode = null
//
//    return output
//}

@Composable
fun BoxWithRectBorder(rect: android.graphics.Rect) {
    Box(
        modifier = Modifier
            .zIndex(100f)
            .fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 2.dp.toPx()

            drawRect(
                color = Color.Red,
                topLeft = Offset(rect.left.toFloat(), rect.top.toFloat()),
                size = Size(
                    rect.width().toFloat(), rect.height().toFloat()
                ),
                style = Stroke(width = strokeWidthPx)
            )
        }
    }
}

@Preview
@Composable
private fun HiddenPrizeCameraPreview() {
    Surface {
        HiddenPrizeCamera {}
    }
}