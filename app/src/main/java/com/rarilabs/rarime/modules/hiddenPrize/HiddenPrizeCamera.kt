package com.rarilabs.rarime.modules.hiddenPrize

import android.graphics.Bitmap
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.manager.WrongFaceException
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors


enum class HiddenPrizeCameraStep {
    CAMERA, CONGRATS, WRONG, PROCESSING_ML, PROCESSING_ZKP, FINISH, ERROR
}


@OptIn(ExperimentalGetImage::class)
@Composable
fun HiddenPrizeCamera(
    modifier: Modifier = Modifier,
    processZK: suspend (Bitmap, List<Float>) -> Unit,
    processML: suspend (Bitmap) -> List<Float>,
    downloadProgress: Int,
    imageLink: String,
    colorScheme: AppColorScheme,
    navigate: (String) -> Unit,
    attemptsLeft: Int,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val meshDetector = remember { FaceMeshDetection.getClient() }
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val scope = rememberCoroutineScope()

    val cameraProvider = remember { ProcessCameraProvider.getInstance(context).get() }

    var featuresBackend: List<Float> by remember {
        mutableStateOf(
            listOf()
        )
    }

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

                ShadowBoxCanvas(
                    boxSize = 270.dp,
                    cornerRadius = 16.dp,
                    borderColor = RarimeTheme.colors.baseWhite,
                    borderWidth = 2.dp,
                    boxColor = RarimeTheme.colors.baseBlackOp40
                )

                FaceMeshCanvas(
                    imageSize = imageSize, detectedMeshes = detectedMeshes
                )

                OverlayControls(
                    selectedBitmap = selectedBitmap, onSelectBitmap = {
                        scope.launch {
                            selectedBitmap = it
                        }
                    }, onClearBitmap = { selectedBitmap = null }, onNext = {
                        currentStep = HiddenPrizeCameraStep.PROCESSING_ML
                    },
                    previewView = previewView, detectedMeshes, onClose = onClose
                )

            }
        }

        HiddenPrizeCameraStep.WRONG -> {
            HiddenPrizeWrongScreen(
                attemptsLeft = attemptsLeft,
                onClose = { navigate(Screen.Main.Home.route) },
                onRetry = {
                    selectedBitmap = null
                    currentStep = HiddenPrizeCameraStep.CAMERA
                })
        }

        HiddenPrizeCameraStep.CONGRATS -> {
            HiddenPrizeCongratsScreen(
                prizeAmount = stringResource(R.string.hidden_prize_prize_pool_value),
                prizeSymbol = {
                    Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
                },
                onClaim = {
                    try {
                        processZK(selectedBitmap!!, featuresBackend)
                    } catch (e: Exception) {
                        currentStep = HiddenPrizeCameraStep.ERROR
                        Log.e("PROCESSING_ZKP", "smth went wrong", e)
                    }
                },
                imageLink = imageLink,
                colorScheme = colorScheme,
                downloadProgress = downloadProgress,
                onShare = {

                },
                onViewWallet = { navigate(Screen.Main.Wallet.route) })
        }

        HiddenPrizeCameraStep.PROCESSING_ML -> {
            HiddenPrizeLoadingML(processingValue = downloadProgress) {
                try {
                    featuresBackend = processML(selectedBitmap!!)
                    currentStep = HiddenPrizeCameraStep.CONGRATS
                } catch (e: WrongFaceException) {
                    Log.e("PROCESSING_ML", "smth went wrong", e)
                    currentStep = HiddenPrizeCameraStep.WRONG
                } catch (e: Exception) {
                    Log.e("PROCESSING_ML", "smth went wrong", e)
                    currentStep = HiddenPrizeCameraStep.ERROR
                }
            }
        }

        HiddenPrizeCameraStep.ERROR -> {
            HiddenPrizeError(
                onBack = {
                    navigate(Screen.Main.Home.route)
                })
        }

        HiddenPrizeCameraStep.PROCESSING_ZKP -> {
            HiddenPrizeLoadingZK(processingValue = (downloadProgress.toFloat() / 100.0f)) {
                try {
                    processZK(selectedBitmap!!, featuresBackend)
                    currentStep = HiddenPrizeCameraStep.FINISH
                } catch (e: Exception) {
                    currentStep = HiddenPrizeCameraStep.ERROR
                    Log.e("PROCESSING_ZKP", "smth went wrong", e)
                }
            }
        }

        HiddenPrizeCameraStep.FINISH -> {
            HiddenPrizeFinish(
                prizeAmount = stringResource(R.string.hidden_prize_prize_pool_value),
                prizeSymbol = {
                    Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
                }, onViewWallet = {}, onShareWallet = {})
        }
    }

}


@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderPreviewOrImage(
    previewView: PreviewView, selectedBitmap: Bitmap?, isBlurred: Boolean
) {
    // This is the shape the AppBottomSheet uses by default.
    val sheetShape = BottomSheetDefaults.ExpandedShape

    val targetBlur = if (isBlurred) 50f else 0f
    val targetShadow = if (isBlurred) 0.5f else 0f

    val blurValue by animateFloatAsState(
        targetValue = targetBlur,
        animationSpec = tween(durationMillis = 500),
        label = "blurAnimation"
    )

    val shadowValue by animateFloatAsState(
        targetValue = targetShadow,
        animationSpec = tween(durationMillis = 1000),
        label = "ShadowAnimation"
    )

    val commonModifier = Modifier
        .fillMaxSize()
        .clip(sheetShape)

    if (selectedBitmap != null) {
        Image(
            bitmap = selectedBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = commonModifier
                .background(Color.Black.copy(alpha = shadowValue))
                .blur(blurValue.dp)
        )
    } else {
        AndroidView(factory = { previewView }, modifier = commonModifier)
    }
}

@Composable
fun OverlayControls(
    selectedBitmap: Bitmap?,
    onSelectBitmap: (Bitmap) -> Unit,
    onClearBitmap: () -> Unit,
    onNext: (Bitmap) -> Unit,
    previewView: PreviewView,
    detectedMeshes: List<FaceMesh>,
    onClose: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Box {
        BaseIconButton(
            onClick = onClose,
            icon = R.drawable.ic_close_fill,
            colors = ButtonDefaults.buttonColors(
                containerColor = RarimeTheme.colors.componentPrimary,
                contentColor = RarimeTheme.colors.baseWhite
            ),
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopEnd)
                .size(40.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIcon(
                id = R.drawable.ic_user_focus, size = 32.dp, tint = RarimeTheme.colors.baseWhite
            )
            Text(
                text = stringResource(R.string.hidden_prize_camera_up_title),
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.baseWhite
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.hidden_prize_camera_description),
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (selectedBitmap == null) {
                    PrimaryButton(
                        enabled = detectedMeshes.isNotEmpty(),
                        size = ButtonSize.Large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 20.dp),
                        onClick = {
                            scope.launch {
                                previewView.bitmap?.let {
                                    onSelectBitmap(it)
                                }
                            }
                        },
                        text = "Photo"
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
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
}

@Composable
fun FaceMeshCanvas(
    imageSize: Size,
    detectedMeshes: List<FaceMesh>,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {

        val viewW = size.width
        val viewH = size.height
        if (imageSize.width == 0f || imageSize.height == 0f) return@Canvas

        val scale = maxOf(viewW / imageSize.width, viewH / imageSize.height)
        val dx = (imageSize.width * scale - viewW) / 2f
        val dy = (imageSize.height * scale - viewH) / 2f


        val contours: List<List<Int>> = listOf(
            listOf(57, 84, 314, 287, 311, 13, 81, 57, 37, 0, 267, 287),//libs
            listOf(
                152, 150, 172, 132, 234, 162, 54, 67, 10, 297, 284, 389, 454, 361, 397, 379, 152

            ),//circuit face
            listOf(97, 129, 5, 358, 326, 97, 129, 193, 417, 358, 417, 336, 107, 193), //nose
            listOf(70, 105, 107),//left eyebrows
            listOf(336, 334, 300),//right eyebrows
            listOf(33, 144, 153, 133, 159, 33), //left eye
            listOf(362, 386, 263, 373, 380, 362), //right eye
            listOf(
                150,
                84,
                314,
                379,
                397,
                314,
                287,
                397,
                287,
                426,
                358,
                5,
                129,
                206,
                57,
                172,
                84,
                150,
                172,
                132,
                234,
                206,
                129,
                5,
                358,
                426,
                454
            )//additional line
        )
        detectedMeshes.forEach { mesh ->
            contours.forEach { pathIndices ->

                for (i in 0 until pathIndices.size - 1) {
                    val startIdx = pathIndices[i]
                    val endIdx = pathIndices[i + 1]

                    val startP = mesh.allPoints[startIdx].position
                    val endP = mesh.allPoints[endIdx].position

                    val start = Offset(startP.x * scale - dx, startP.y * scale - dy)
                    val end = Offset(endP.x * scale - dx, endP.y * scale - dy)
                    drawCircle(
                        center = start, radius = 4.dp.toPx(), color = Color.White
                    )
                    drawCircle(
                        center = end, radius = 4.dp.toPx(), color = Color.White
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.9f),
                        start = start,
                        end = end,
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }

            mesh.allPoints.forEach { p ->
                val cx = p.position.x * scale - dx
                val cy = p.position.y * scale - dy
                drawCircle(
                    center = Offset(cx, cy),
                    radius = 1.8.dp.toPx(),
                    color = Color.White.copy(alpha = 0.25f)
                )
            }
        }
    }
}

@Composable
fun ShadowBoxCanvas(
    boxSize: Dp, cornerRadius: Dp, borderColor: Color, borderWidth: Dp, boxColor: Color
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val holeSizePx = boxSize.toPx()
            val cornerRadiusPx = cornerRadius.toPx()
            val borderWidthPx = borderWidth.toPx()
            val centerX = size.width / 2
            val centerY = size.height / 2
            val left = centerX - holeSizePx / 2
            val top = centerY - holeSizePx / 2

            val holeRect = RoundRect(
                left = left,
                top = top,
                right = left + holeSizePx,
                bottom = top + holeSizePx,
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )

            val canvasPath = Path().apply {
                addRect(size.toRect())
            }

            val holePath = Path().apply {
                addRoundRect(holeRect)
            }

            val shadowPath = Path.combine(
                PathOperation.Difference, canvasPath, holePath
            )

            drawPath(shadowPath, color = boxColor)

            drawRoundRect(
                color = borderColor,
                topLeft = Offset(holeRect.left, holeRect.top),
                size = Size(holeSizePx, holeSizePx),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = borderWidthPx)
            )
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