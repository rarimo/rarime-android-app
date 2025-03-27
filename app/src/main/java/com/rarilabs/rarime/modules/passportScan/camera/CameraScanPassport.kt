package com.rarilabs.rarime.modules.passportScan.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppAnimation
import org.jmrtd.lds.icao.MRZInfo

@Composable
fun CameraScanPassport(modifier: Modifier, onMrzDetected: (mrz: MRZInfo) -> Unit) {
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val previewUseCase = androidx.camera.core.Preview.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(
                        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY,
                    )
                    .build()
            )
            .build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(
                        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY,
                    )
                    .build()
            )
            .build().also {
                it.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    TextRecognitionAnalyzer(onDetectedTextUpdated = onMrzDetected)
                )
            }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            previewUseCase,
            imageAnalysisUseCase
        )

        onDispose {
            cameraProvider.unbindAll()
        }
    }

    Column {
        Box(
            modifier = modifier
                .clipToBounds()
        ) {
            AndroidView(
                factory = { previewView }, modifier = Modifier
                    .zIndex(1f)
            )

            AppAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.Center)
                    .absoluteOffset(y = 0.dp, x = 0.dp)
                    .zIndex(2f),
                id = R.raw.anim_passport_mrz
            )
        }
    }
}

@Preview
@Composable
private fun CameraScanPassportPreview() {
    CameraScanPassport(Modifier) {}
}

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (MRZInfo) -> Unit
) {

    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(
            AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY,
        )
        .build()

    val previewUseCase = androidx.camera.core.Preview.Builder()
        .setResolutionSelector(resolutionSelector)
        .build()

    val imageAnalysisUseCase = ImageAnalysis.Builder()
        .setResolutionSelector(resolutionSelector)
        .build().also {
            it.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
            )
        }

    val useCaseGroup = UseCaseGroup.Builder()
        .addUseCase(previewUseCase)
        .addUseCase(imageAnalysisUseCase)
        .build()

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}