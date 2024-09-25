package com.rarilabs.rarime.modules.qr

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanQrProcessor(
    modifier: Modifier = Modifier,
    onCompletion: (String) -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted)
            cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.status.isGranted) {
        QrCodeScanner(
            modifier = Modifier.fillMaxSize().then(modifier),
            onCompletion = onCompletion,
            flashlightOn = false
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrCodeScanner(
    modifier: Modifier,
    flashlightOn: Boolean,
    onCompletion: (String) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted)
            cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.status.isGranted) {
        ScanQrProcessorContent(
            modifier = modifier,
            flashlightOn = flashlightOn,
            onCompletion = onCompletion,
        )
    }
}

@Composable
fun ScanQrProcessorContent(
    modifier: Modifier = Modifier,
    onCompletion: (String) -> Unit,
    flashlightOn: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }

    var camera by remember { mutableStateOf<Camera?>(null) }

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)

    DisposableEffect(cameraProviderFuture) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    if (hasCamPermission) {
        AndroidView(
            modifier = modifier,
            factory = { AndroidViewContext ->
                PreviewView(AndroidViewContext).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = { previewView ->
                val cameraSelector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

                cameraProviderFuture.addListener({
                    preview = Preview.Builder()
                        .build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                    val barcodeAnalyser = BarcodeAnalyser() { barcodes ->
                        barcodes.forEach { barcode ->
                            barcode.rawValue?.let { barcodeValue ->
                                onCompletion(barcodeValue)
                            }
                        }
                    }
                    val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                        }

                    val imageCapture = ImageCapture.Builder()
                        .setFlashMode(ImageCapture.FLASH_MODE_ON)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("qr code", e.message ?: "")
                    }

                    if (camera?.cameraInfo?.hasFlashUnit() == true) {
                        camera?.cameraControl?.enableTorch(flashlightOn)
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )
    }
}

class BarcodeAnalyser(
    private val onBarcodeDetected: (barcodes: List<Barcode>) -> Unit,
) : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimeStamp = 0L

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimeStamp >= TimeUnit.SECONDS.toMillis(1)) {
            image.image?.let { imageToAnalyze ->
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
                val barcodeScanner = BarcodeScanning.getClient(options)
                val imageToProcess =
                    InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)

                barcodeScanner.process(imageToProcess)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            onBarcodeDetected(barcodes)
                        }
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
                    .addOnCompleteListener {
                        image.close()
                    }
            }
            lastAnalyzedTimeStamp = currentTimestamp
        } else {
            image.close()
        }
    }
}