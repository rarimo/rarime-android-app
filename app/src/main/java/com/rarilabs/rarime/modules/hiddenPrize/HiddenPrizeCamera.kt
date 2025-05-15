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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
            ErrorHandler.logError("CameraError", e.toString())
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

                                selectedBitmap = null
                            },
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        PrimaryButton(
                            modifier = Modifier.weight(7f),
                            size = ButtonSize.Large,
                            text = "Continue",
                            onClick = {
                                onNext(selectedBitmap!!)
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun HiddenPrizeWrongScreen(
    modifier: Modifier = Modifier,
    attemptsLeft: Int = 0,
    tip: String? = null,
    onRetry: () -> Unit = {}
) {
    val canRetry = true
    val description = buildString {
        append(stringResource(R.string.hidden_prize_wrong_screen_description_1))
        if (canRetry) append(stringResource(R.string.hidden_prize_wrong_screen_description_2))
    }
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .blur(120.dp)
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.baseBlack.copy(alpha = 0.7f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_wrong_face),
                        contentDescription = null,
                        tint = RarimeTheme.colors.baseWhite,
                    )
                    Spacer(Modifier.height(32.dp))
                    Text(
                        stringResource(R.string.hidden_prize_wrong_screen_title),
                        color = RarimeTheme.colors.baseWhite,
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.h3
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        description,
                        color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(230.dp),
                    )
                }
            }

            tip?.let {
                Text(
                    it,
                    color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (canRetry) {
                BaseButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    size = ButtonSize.Large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RarimeTheme.colors.baseWhite.copy(0.1f),
                        contentColor = RarimeTheme.colors.invertedLight,
                        disabledContainerColor = RarimeTheme.colors.componentDisabled,
                        disabledContentColor = RarimeTheme.colors.textDisabled
                    ),
                    onClick = onRetry
                ) {
                    Text(
                        stringResource(R.string.hidden_prize_wrong_screen_rescan_btn),
                        color = RarimeTheme.colors.baseWhite
                    )
                }
            }
        }
    }
}

@Composable
fun HiddenPrizeSuccessScreen(
    modifier: Modifier = Modifier,
    prizeAmount: Float,
    prizeSymbol: @Composable () -> Unit = {},
    onViewWallet: () -> Unit,
    onShareWallet: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .blur(120.dp)
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.baseBlack.copy(alpha = 0.7f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(
                        Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(
                                Color(0xB4AEA2E2).toArgb(),
                                Color(0xF1EDD9FF).toArgb(),
                            ),
                            position = Position.Relative(0.5, 0.3),
                            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                        )
                    )
                )
                Column(
                    modifier = Modifier.width(230.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_success_color),
                        contentDescription = null,
                        tint = RarimeTheme.colors.baseWhite,
                    )
                    Spacer(Modifier.height(32.dp))
                    Text(
                        stringResource(R.string.hidden_prize_success_screen_title),
                        color = RarimeTheme.colors.baseWhite,
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.h3
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.hidden_prize_success_screen_description),
                        color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(32.dp))
                    HorizontalDivider(
                        modifier = Modifier.width(280.dp),
                        color = RarimeTheme.colors.baseWhite.copy(alpha = 0.05f)
                    )
                    Spacer(Modifier.height(32.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(
                                RarimeTheme.colors.baseWhite.copy(0.05f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(vertical = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.hidden_prize_success_screen_prize),
                            color = RarimeTheme.colors.baseWhite.copy(0.6f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                prizeAmount.toString().format(),
                                style = RarimeTheme.typography.h3,
                                color = RarimeTheme.colors.baseWhite
                            )
                            prizeSymbol()
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BaseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                size = ButtonSize.Large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.baseWhite.copy(0.1f),
                    contentColor = RarimeTheme.colors.invertedLight,
                    disabledContainerColor = RarimeTheme.colors.componentDisabled,
                    disabledContentColor = RarimeTheme.colors.textDisabled
                ),
                onClick = onViewWallet
            ) {
                Text(
                    stringResource(R.string.hidden_prize_success_screen_wallet_btn),
                    color = RarimeTheme.colors.baseWhite
                )
            }
            BaseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                size = ButtonSize.Large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.baseWhite,
                    contentColor = RarimeTheme.colors.baseBlack,
                    disabledContainerColor = RarimeTheme.colors.componentDisabled,
                    disabledContentColor = RarimeTheme.colors.textDisabled
                ),
                onClick = onShareWallet
            ) {
                Text(
                    stringResource(R.string.hidden_prize_success_share_btn),
                    color = RarimeTheme.colors.baseBlack,
                )
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

@Composable
@Preview(showBackground = true)
fun WrongScreenPreview_WithBlur() {
    Box(Modifier.fillMaxSize()) {
        // Image for blur example
        Image(
            painter = painterResource(R.drawable.drawable_digital_likeness),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
        )

        HiddenPrizeWrongScreen(
            attemptsLeft = 2,
            tip = "Tip: I think there's something as light as ether in that face..."
        )
    }
}

@Composable
@Preview
fun SuccessScreenPreview_WithBlur() {
    Box(Modifier.fillMaxSize()) {
        // Image for blur example
        Image(
            painter = painterResource(R.drawable.drawable_digital_likeness),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
        )

        HiddenPrizeSuccessScreen(
            prizeAmount = 2.2f,
            onViewWallet = {},
            prizeSymbol = {
                Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
            },
            onShareWallet = {}
        )
    }
}