package com.rarilabs.rarime.modules.hiddenPrize

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
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
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors


enum class HiddenPrizeCameraStep {
    CAMERA, CONGRATS, WRONG, PROCESSING_ML, PROCESSING_ZKP, FINISH
}


@OptIn(ExperimentalGetImage::class)
@Composable
fun HiddenPrizeCamera(
    modifier: Modifier = Modifier,
    processZK: suspend (List<Float>, Bitmap) -> Unit,
    processML: suspend (Bitmap) -> List<Float>,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val meshDetector = remember { FaceMeshDetection.getClient() }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val cameraProvider = remember { ProcessCameraProvider.getInstance(context).get() }

    var imageSize by remember { mutableStateOf(Size.Zero) }
    var detectedMeshes by remember { mutableStateOf<List<FaceMesh>>(emptyList()) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var currentStep by remember {
        mutableStateOf(HiddenPrizeCameraStep.CAMERA)
    }

    SetupCamera(
        cameraProvider = cameraProvider,
        lifecycleOwner = lifecycleOwner,
        previewView = previewView,
        meshDetector = meshDetector,
        cameraExecutor = cameraExecutor,
        onImageSizeUpdated = { imageSize = it },
        onMeshDetected = { detectedMeshes = it })

    RenderPreviewOrImage(
        previewView, selectedBitmap, isBlurred = (currentStep != HiddenPrizeCameraStep.CAMERA)
    )

    when (currentStep) {
        HiddenPrizeCameraStep.CAMERA -> {
            Box(modifier = modifier.fillMaxSize()) {
                FaceMeshCanvas(
                    imageSize = imageSize, detectedMeshes = detectedMeshes
                )

                OverlayControls(
                    selectedBitmap = selectedBitmap,
                    onSelectBitmap = { selectedBitmap = it },
                    onClearBitmap = { selectedBitmap = null },
                    onNext = { currentStep = HiddenPrizeCameraStep.CONGRATS },
                    previewView = previewView
                )
            }
        }


        HiddenPrizeCameraStep.WRONG -> {
            HiddenPrizeWrongScreen()
        }

        HiddenPrizeCameraStep.CONGRATS -> {
            HiddenPrizeCongratsScreen(
                prizeAmount = 2.0f,
                prizeSymbol = { AppIcon(id = R.drawable.ic_restart_line) },
                onClaim = {

                    currentStep = HiddenPrizeCameraStep.PROCESSING_ZKP
                })
        }

        HiddenPrizeCameraStep.PROCESSING_ML -> {
            HiddenPrizeLoadingML(processingValue = 0.5f) {
                processML(selectedBitmap!!)
            }
        }

        HiddenPrizeCameraStep.PROCESSING_ZKP -> {
            HiddenPrizeLoadingZK(processingValue = 0.3f) {
                //processZK()
            }
        }

        HiddenPrizeCameraStep.FINISH -> {
            HiddenPrizeFinish(
                prizeAmount = 2.0f,
                prizeSymbol = { AppIcon(id = R.drawable.ic_restart_line) },
                onViewWallet = {},
                onShareWallet = {})
        }
    }

}


@Composable
fun RenderPreviewOrImage(
    previewView: PreviewView, selectedBitmap: Bitmap?, isBlurred: Boolean
) {
    val targetBlur = if (isBlurred) 50f else 0f
    val targetShadow = if (isBlurred) 0.5f else 0f

    val blurValue by animateFloatAsState(
        targetValue = targetBlur,
        animationSpec = tween(durationMillis = 500), // Adjust duration for smoothness
        label = "blurAnimation"
    )

    val shadowValue by animateFloatAsState(
        targetValue = targetShadow,
        animationSpec = tween(durationMillis = 1000), // Adjust duration for smoothness
        label = "ShadowAnimation"
    )

    if (selectedBitmap != null) {
        Image(
            bitmap = selectedBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = shadowValue))
                .blur(blurValue.dp)
        )
    } else {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun OverlayControls(
    selectedBitmap: Bitmap?,
    onSelectBitmap: (Bitmap) -> Unit,
    onClearBitmap: () -> Unit,
    onNext: (Bitmap) -> Unit,
    previewView: PreviewView
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
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
            textAlign = TextAlign.Center
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
                            previewView.bitmap?.let { onSelectBitmap(it) }
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
                        onClick = { onClearBitmap() })

                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        modifier = Modifier.weight(7f),
                        size = ButtonSize.Large,
                        text = "Continue",
                        onClick = { onNext(selectedBitmap) })
                }
            }
        }
    }
}

@Composable
fun FaceMeshCanvas(
    imageSize: Size, detectedMeshes: List<FaceMesh>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val viewW = size.width
        val viewH = size.height

        if (imageSize.width == 0f || imageSize.height == 0f) return@Canvas

        val scale = maxOf(viewW / imageSize.width, viewH / imageSize.height)
        val scaledW = imageSize.width * scale
        val scaledH = imageSize.height * scale
        val dx = (scaledW - viewW) / 2f
        val dy = (scaledH - viewH) / 2f

        detectedMeshes.forEach { faceMesh ->

            faceMesh.allTriangles.forEach { tri ->
                val pts = tri.allPoints
                if (pts.size == 3) {
                    val mapped = pts.map { p ->
                        val rawX = p.position.x * scale - dx
                        val rawY = p.position.y * scale - dy
                        Offset(rawX, rawY)
                    }

                    val path = Path().apply {
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
}


@OptIn(ExperimentalGetImage::class)
@Composable
fun SetupCamera(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    meshDetector: FaceMeshDetector,
    cameraExecutor: Executor,
    onImageSizeUpdated: (Size) -> Unit,
    onMeshDetected: (List<FaceMesh>) -> Unit
) {
    LaunchedEffect(Unit) {
        val previewUseCase = androidx.camera.core.Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
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
                            val (rotW, rotH) = if (rotation == 90 || rotation == 270) origH to origW else origW to origH
                            onImageSizeUpdated(Size(rotW.toFloat(), rotH.toFloat()))

                            val inputImage = InputImage.fromMediaImage(mediaImage, rotation)
                            meshDetector.process(inputImage)
                                .addOnSuccessListener { onMeshDetected(it) }
                                .addOnCompleteListener { imageProxy.close() }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, previewUseCase, analysisUseCase
            )
        } catch (e: Exception) {
            ErrorHandler.logError("CameraError", e.toString())
        }
    }
}

//@Preview
//@Composable
//private fun HiddenPrizeCameraPreview() {
//    HiddenPrizeCamera(processML = {}, processZK = {}) {
//
//    }
//}