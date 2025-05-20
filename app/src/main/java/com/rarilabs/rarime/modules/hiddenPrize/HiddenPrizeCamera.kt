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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    processZK: suspend (Bitmap, List<Float>) -> Unit,
    processML: suspend (Bitmap) -> List<Float>,
    checkCrop: suspend (Bitmap) -> Bitmap?,
    downloadProgress: Int
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
//                FaceMeshCanvas(
//                    imageSize = imageSize, detectedMeshes = detectedMeshes
//                )

                OverlayControls(
                    selectedBitmap = selectedBitmap,
                    onSelectBitmap = {
                        scope.launch {
                            selectedBitmap = checkCrop(it)
                        }
                    },
                    onClearBitmap = { selectedBitmap = null },
                    onNext = {
                        currentStep = HiddenPrizeCameraStep.PROCESSING_ZKP //TODO: rename
                    },
                    previewView = previewView
                )
            }
        }


        HiddenPrizeCameraStep.WRONG -> {
            HiddenPrizeWrongScreen(
                onRetry = {
                    selectedBitmap = null

                    currentStep = HiddenPrizeCameraStep.CAMERA
                })
        }

        HiddenPrizeCameraStep.CONGRATS -> {
            HiddenPrizeCongratsScreen(prizeAmount = 2.0f, prizeSymbol = {
                Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
            }, onClaim = {
                currentStep = HiddenPrizeCameraStep.PROCESSING_ZKP
            })
        }

        HiddenPrizeCameraStep.PROCESSING_ML -> {
            HiddenPrizeLoadingML(processingValue = (downloadProgress.toFloat() / 100.0f)) {
                try {
                    featuresBackend = processML(selectedBitmap!!)
                    currentStep = HiddenPrizeCameraStep.CONGRATS
                } catch (e: Exception) {
                    Log.e("PROCESSING_ML", "smth went wrong", e)
                    currentStep = HiddenPrizeCameraStep.WRONG
                }
            }
        }

        HiddenPrizeCameraStep.PROCESSING_ZKP -> {
            HiddenPrizeLoadingZK(processingValue = (downloadProgress.toFloat() / 100.0f)) {
                try {

                    featuresBackend = originalFeaturesDev.map { it.toFloat() }

                    processZK(selectedBitmap!!, featuresBackend)
                    currentStep = HiddenPrizeCameraStep.FINISH
                } catch (e: Exception) {
                    Log.e("PROCESSING_ZKP", "smth went wrong", e)

                }
            }
        }

        HiddenPrizeCameraStep.FINISH -> {
            HiddenPrizeFinish(prizeAmount = 2.0f, prizeSymbol = {
                Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
            }, onViewWallet = {}, onShareWallet = {})
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
                            previewView.bitmap?.let {
                                onSelectBitmap(it)
                            }
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
            ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

                .build().also {
                    it
                        .setAnalyzer(cameraExecutor) { imageProxy ->
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


val originalFeaturesDev = listOf(
    -0.05455792,
    0.09697727,
    0.084496394,
    0.024740713,
    0.022895254,
    -0.056219414,
    -0.005138728,
    -0.030730363,
    0.06675692,
    -0.08572025,
    0.032246657,
    0.03084425,
    -0.0033083248,
    0.09361579,
    -0.024216084,
    -0.021499969,
    -0.020634847,
    -0.08815066,
    0.023205837,
    -0.070614226,
    -0.036048695,
    -0.03658558,
    -0.03341574,
    0.016811445,
    0.019127665,
    0.05159616,
    -0.022709994,
    0.007331311,
    -0.09703335,
    -0.01540119,
    -0.012532849,
    0.029667037,
    0.019004554,
    -0.008384877,
    -0.0689305,
    0.035499204,
    -0.043367073,
    -0.010998965,
    0.06664832,
    -0.050020512,
    0.023825763,
    0.034303546,
    -0.004597539,
    0.027514987,
    -0.0012840091,
    0.079883665,
    -0.06680102,
    0.033119567,
    -0.02854176,
    -0.021894583,
    -0.03834081,
    0.059996307,
    -0.024722768,
    -0.036513202,
    0.12228193,
    -0.10570943,
    0.003026887,
    -0.0060105547,
    -0.045053888,
    0.03892264,
    -0.021496331,
    -0.022703743,
    0.025605181,
    -0.013905472,
    0.012078154,
    0.001079644,
    -0.03069769,
    0.04469816,
    -0.027798843,
    0.029944878,
    -0.0395032,
    0.052142214,
    0.023790168,
    -0.03156995,
    -0.062580235,
    0.08615049,
    -0.086297564,
    0.01753698,
    -0.048379336,
    -0.055314727,
    -0.017057534,
    0.031971343,
    0.03798748,
    0.037602074,
    0.0078545995,
    0.011234191,
    0.032262575,
    -0.018295242,
    0.048415165,
    0.0066785337,
    0.057133112,
    -0.006383548,
    0.015600951,
    -0.060073078,
    0.02375236,
    -0.012852348,
    0.033413723,
    0.0043240823,
    -0.003777304,
    0.014398517,
    -0.006304345,
    0.050300255,
    -0.0068077506,
    0.059221502,
    -0.01962531,
    -0.08472493,
    -0.012611941,
    0.07071432,
    0.011240514,
    -0.0031490836,
    0.002539699,
    -0.03370014,
    -0.039418608,
    0.06968715,
    -0.026650568,
    0.027782647,
    0.034169782,
    0.09724382,
    -0.05802518,
    -0.037530854,
    -0.02396451,
    -0.023780901,
    0.0048193256,
    0.02609762,
    0.030856138,
    -0.06461595,
    0.018155841,
    0.052492015,
    -0.05315633,
    0.051778365,
    0.07582122,
    -0.07653453,
    -0.020020155,
    0.013529331,
    0.011118989,
    0.038110062,
    -1.9438348E-4,
    0.0336236,
    0.0015719826,
    0.029118693,
    0.01818193,
    0.024714839,
    0.050281927,
    -0.022083525,
    -0.047007248,
    0.08594048,
    -0.07080298,
    -0.05495682,
    -0.026126938,
    0.07751921,
    0.046676923,
    0.029397644,
    -0.055666737,
    0.029883469,
    -0.0022536672,
    0.04823845,
    0.022914233,
    0.03385481,
    0.038034808,
    -0.08699474,
    0.055257764,
    -0.013134937,
    0.036281932,
    -0.008461029,
    -0.07731531,
    -0.0452719,
    0.0042004013,
    0.013266353,
    -0.0034945833,
    -0.008467005,
    0.008809779,
    -0.059671316,
    -0.007508411,
    -0.02267565,
    0.043420456,
    -0.01739526,
    -0.049220175,
    0.071596354,
    0.033689126,
    0.042485874,
    -0.024015656,
    -0.057585638,
    -0.008580588,
    0.001550555,
    -0.07119207,
    0.04609799,
    0.03381421,
    -0.0072722915,
    -0.0012685094,
    -0.031787753,
    -0.009349606,
    -0.029060889,
    -0.01925746,
    0.02807567,
    -0.020216838,
    0.027660359,
    0.059490606,
    0.03539569,
    -0.026667155,
    -0.0568885,
    0.03843398,
    0.04842951,
    -0.06071142,
    0.023874268,
    -0.0070304554,
    0.0030882463,
    0.0492608,
    0.055390157,
    -0.005875016,
    0.016741155,
    -0.023736255,
    6.07478E-4,
    0.03069574,
    -0.046671886,
    -0.005613379,
    0.018783677,
    0.02610228,
    0.039317053,
    -0.023824751,
    0.050125338,
    0.033467017,
    0.0033598654,
    0.067650154,
    -0.005868946,
    -0.023458755,
    -5.0451137E-5,
    0.04861351,
    0.02706537,
    0.06242866,
    -0.05610276,
    0.0050114207,
    0.13136001,
    -0.008367436,
    0.051844805,
    0.018536543,
    0.064381294,
    -0.01138815,
    -0.011460537,
    0.074645154,
    -0.038570765,
    0.021154718,
    0.015624945,
    -0.05109947,
    -0.03220778,
    -0.10760195,
    0.0036282085,
    -0.030197881,
    -0.01564616,
    -0.007578685,
    0.028563682,
    -0.006246547,
    0.031213706,
    -0.089032166,
    0.015531612,
    -0.065364,
    0.05007589,
    -0.014847185,
    0.013931941,
    0.019992027,
    -0.07949478,
    -0.003271901,
    -0.012358066,
    0.020315483,
    0.058673233,
    -0.007640481,
    -0.053037055,
    0.032579325,
    -0.022605145,
    -0.005764669,
    0.053018734,
    0.010396969,
    0.04008844,
    -0.009579903,
    0.01223921,
    -0.112363465,
    -0.014919918,
    0.024757927,
    0.008908626,
    -0.03137526,
    -0.004998156,
    0.02993424,
    0.07790703,
    -0.024197916,
    0.012528902,
    0.051543195,
    0.09079179,
    -0.06700345,
    0.013840318,
    0.010823228,
    -0.05496108,
    -0.044502597,
    -0.02320307,
    0.014926657,
    0.08878975,
    -0.039657447,
    -0.006370472,
    0.05064073,
    0.016347343,
    -0.035243217,
    0.01108547,
    0.03404938,
    0.011824932,
    0.00819012,
    0.0018669914,
    0.004422633,
    -5.732921E-4,
    0.053864393,
    -0.026744362,
    -0.009375831,
    -0.008676434,
    -0.03724355,
    0.06026117,
    -0.034291364,
    -0.023659538,
    -0.0016456547,
    0.05681912,
    0.015432814,
    -0.079816185,
    0.05162367,
    0.016384047,
    -0.0024274648,
    -0.061625827,
    -0.011171601,
    -0.07383528,
    0.02681254,
    0.029137727,
    0.09388641,
    9.008495E-4,
    0.0026496854,
    -0.0024849966,
    -0.06384408,
    0.0730679,
    -0.03016578,
    0.056086734,
    -4.6760307E-4,
    -6.9878827E-4,
    -0.033754233,
    -6.0615287E-4,
    -0.017535636,
    -0.014524829,
    -0.033339094,
    -0.0630102,
    -0.0054346584,
    0.0044565843,
    -0.01574253,
    0.07480256,
    0.05213241,
    -0.020400532,
    0.0574215,
    0.037901692,
    0.045066435,
    -0.0399348,
    -0.02374307,
    -0.088074505,
    -0.017846577,
    0.039711513,
    -0.033098456,
    -0.059188943,
    -0.028317679,
    0.104153425,
    0.005380508,
    -0.03277442,
    0.066479914,
    -0.11313223,
    -0.048695028,
    -0.0044764797,
    0.032353614,
    0.0076781493,
    0.04364661,
    0.0013774958,
    3.7662714E-4,
    -0.04453911,
    0.0042904215,
    0.06565319,
    0.012317286,
    0.049573936,
    0.036705267,
    0.027925892,
    -0.015081595,
    0.050988656,
    -0.030517295,
    0.0072858487,
    0.038768325,
    -0.051354874,
    0.048977837,
    -0.05868164,
    8.3950604E-4,
    -0.04168808,
    0.09107378,
    0.010959003,
    -0.048744522,
    -0.019126156,
    0.072892055,
    -0.0015080185,
    0.056734417,
    0.06606894,
    -0.04302453,
    -0.044864155,
    -0.05636599,
    -0.036249798,
    0.026340336,
    -0.003612534,
    0.04900588,
    0.010079106,
    -0.05580639,
    -0.04095569,
    -0.012131531,
    -0.026141701,
    0.037887,
    0.04735449,
    -0.04855219,
    0.007594838,
    -0.09690703,
    -0.020852245,
    3.178798E-4,
    -0.018709479,
    0.01789057,
    0.053818263,
    -0.053865865,
    0.0030258738,
    -0.02352038,
    0.01625086,
    0.02649483,
    0.026700387,
    -0.007218683,
    0.009177928,
    0.017787836,
    -0.06453416,
    -0.05955882,
    -0.033256948,
    0.059896536,
    0.053261973,
    0.081204884,
    0.046062544,
    -0.009677372,
    -0.024137,
    -0.052499887,
    -0.1294006,
    -0.017262181,
    0.09157268,
    -0.1114546,
    0.029635098,
    -0.039954774,
    -0.060882796,
    0.12623028,
    0.018082768,
    -0.049499117,
    -0.08703919,
    -0.06305878,
    0.017032241,
    -0.0063811988,
    -0.015666641,
    -0.030758208,
    -0.0062431623,
    -0.018782796,
    0.024934677,
    0.04750068,
    0.115742944,
    0.0061698905,
    0.06755426,
    0.13178132,
    -0.032829385,
    -0.048745245,
    -0.026421625,
    0.0215959,
    -0.08784737,
    -0.01902876,
    0.012871224,
    -0.015762845,
    -0.040650304,
    0.024084589,
    0.06549682,
    -0.031067232,
    -0.025607677,
    -0.0011277088,
    -0.0011261055,
    6.707128E-4,
    0.019411987,
    0.0041143266,
    -0.008580891,
    0.023119556,
    -0.024488717,
    0.066364676,
    -0.025100105,
    -0.013705256,
    0.0045021735,
    -0.0039520073,
    0.06708247,
    0.0423564,
    0.048627753,
    -0.023822963,
    -0.057412717,
    0.038611524,
    0.024845812,
    0.003345156,
    0.013712445,
    0.005800242,
    0.054337483,
    -0.027781554,
    0.054501962,
    0.006068027,
    0.05374293,
    -0.0075567835,
    -0.057043064,
    0.049845316,
    0.031575147,
    0.039324638,
    -0.023788905,
    -0.024533628,
    0.03746515,
    -0.060785588,
    -0.03993464
)