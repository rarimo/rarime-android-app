package com.rarilabs.rarime.modules.passportScan.camera

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }

    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clipToBounds()
        ) {
            AndroidView(modifier = modifier.fillMaxSize().zIndex(1f), factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    startTextRecognition(context = ctx,
                        cameraController = cameraController,
                        lifecycleOwner = lifecycleOwner,
                        previewView = previewView,
                        onDetectedTextUpdated = { onMrzDetected(it) })
                }
            })

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

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (MRZInfo) -> Unit
) {

    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}