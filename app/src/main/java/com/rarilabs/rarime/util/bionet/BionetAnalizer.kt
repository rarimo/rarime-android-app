package com.rarilabs.rarime.util.bionet

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.core.graphics.scale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.min


class BionetAnalizer {

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()


    private val realTimeOpts =
        FaceDetectorOptions.Builder().setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build()

    @OptIn(ExperimentalGetImage::class)
    suspend fun getPreparedInputForZKML(bitmap: Bitmap): Array<FloatArray>? =
        suspendCancellableCoroutine { cont ->

            val detector = FaceDetection.getClient(highAccuracyOpts)
            val image = InputImage.fromBitmap(bitmap, 0)

            detector.process(image).addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    cont.resume(null)
                    return@addOnSuccessListener
                }

                val face = faces.first()
                val bounds = face.boundingBox

                val side = max(bounds.width(), bounds.height())
                val centerX = bounds.centerX()
                val centerY = bounds.centerY()
                val halfSide = side / 2

                val squareRect = Rect(
                    (centerX - halfSide).coerceAtLeast(0),
                    (centerY - halfSide).coerceAtLeast(0),
                    (centerX + halfSide).coerceAtMost(bitmap.width),
                    (centerY + halfSide).coerceAtMost(bitmap.height)
                )

                val cropSize = min(squareRect.width(), squareRect.height())
                if (cropSize <= 0) {
                    cont.resume(null); return@addOnSuccessListener
                }

                val faceBmp = Bitmap.createBitmap(
                    bitmap, squareRect.left, squareRect.top, cropSize, cropSize
                ).let { bmp ->
                    Bitmap.createScaledBitmap(bmp, 40, 40, true)
                        .copy(Bitmap.Config.ARGB_8888, false)
                }

                // --- в серую шкалу ---
                val w = faceBmp.width
                val h = faceBmp.height
                val pixels = IntArray(w * h)
                faceBmp.getPixels(pixels, 0, w, 0, 0, w, h)

                val gray = Array(h) { FloatArray(w) }
                for (y in 0 until h) {
                    val row = y * w
                    for (x in 0 until w) {
                        val c = pixels[row + x]
                        val r = Color.red(c)
                        val g = Color.green(c)
                        val b = Color.blue(c)
                        gray[y][x] = ((0.299f * r + 0.587f * g + 0.114f * b) / 255f)
                    }
                }

                cont.resume(gray)
            }.addOnFailureListener { cont.resume(null) }

            cont.invokeOnCancellation { detector.close() }
        }

    @OptIn(ExperimentalGetImage::class)
    suspend fun getBounds(bitmap: Bitmap): Rect? {

        return suspendCoroutine { cont ->
            val image = InputImage.fromBitmap(bitmap, 0)

            val detector = FaceDetection.getClient(realTimeOpts)

            detector.process(image).addOnSuccessListener { faces ->
                for (face in faces) {
                    val bounds = face.boundingBox

                    val side = max(bounds.width(), bounds.height())

                    val centerX = bounds.centerX()
                    val centerY = bounds.centerY()


                    val halfSide = side / 2

                    val squareRect = Rect(
                        centerX - halfSide,
                        centerY - halfSide,
                        centerX + halfSide,
                        centerY + halfSide
                    )

                    val x = squareRect.left.coerceAtLeast(0)
                    val y = squareRect.top.coerceAtLeast(0)
                    val maxWidth = bitmap.width - x
                    val maxHeight = bitmap.height - y
                    val size =
                        min(min(squareRect.width(), squareRect.height()), min(maxWidth, maxHeight))

                    Bitmap.createBitmap(bitmap, x, y, size, size)

                    cont.resume(squareRect)
                }
            }.addOnFailureListener {
                cont.resume(null)
            }
        }
    }


    @OptIn(ExperimentalGetImage::class)
    suspend fun getPreparedInputForML(bitmap: Bitmap): Array<Array<IntArray>>? =
        suspendCancellableCoroutine { cont ->

            val detector = FaceDetection.getClient(highAccuracyOpts)
            val image = InputImage.fromBitmap(bitmap, 0)

            detector.process(image).addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    cont.resume(null)
                    return@addOnSuccessListener
                }

                val face = faces.first()
                val bounds = face.boundingBox

                val side = max(bounds.width(), bounds.height())
                val centerX = bounds.centerX()
                val centerY = bounds.centerY()
                val halfSide = side / 2

                val squareRect = Rect(
                    (centerX - halfSide).coerceAtLeast(0),
                    (centerY - halfSide).coerceAtLeast(0),
                    (centerX + halfSide).coerceAtMost(bitmap.width),
                    (centerY + halfSide).coerceAtMost(bitmap.height)
                )

                val cropSize = min(squareRect.width(), squareRect.height())
                if (cropSize <= 0) {
                    cont.resume(null); return@addOnSuccessListener
                }

                val faceBmp = Bitmap.createBitmap(
                    bitmap, squareRect.left, squareRect.top, cropSize, cropSize
                ).let { bmp ->
                    bmp.scale(112, 112).copy(Bitmap.Config.ARGB_8888, false)
                }


                val w = faceBmp.width
                val h = faceBmp.height
                val pixels = IntArray(w * h)
                faceBmp.getPixels(pixels, 0, w, 0, 0, w, h)

                val result = Array(h) { Array(w) { IntArray(3) } }
                for (y in 0 until h) {
                    val row = y * w
                    for (x in 0 until w) {
                        val c = pixels[row + x]
                        val r = Color.red(c)
                        val g = Color.green(c)
                        val b = Color.blue(c)
                        result[y][x] = listOf(r, g, b).toIntArray()
                    }
                }

                cont.resume(result)
            }.addOnFailureListener { cont.resume(null) }

            cont.invokeOnCancellation { detector.close() }
        }

}