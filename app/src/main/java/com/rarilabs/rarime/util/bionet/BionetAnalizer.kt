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
    suspend fun getPreparedInputForML(bitmap: Bitmap): Array<FloatArray>? {

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

                    val result = Bitmap.createBitmap(bitmap, x, y, size, size).scale(40, 40)

                    val grayArray = Array(result.height) { FloatArray(result.width) }

                    val pixels = IntArray(result.width * result.height)
                    bitmap.getPixels(pixels, 0, result.width, 0, 0, result.width, result.height)

                    for (y in 0 until result.height) {
                        val rowOffset = y * result.width
                        for (x in 0 until result.width) {
                            val color = pixels[rowOffset + x]

                            val r = Color.red(color)
                            val g = Color.green(color)
                            val b = Color.blue(color)

                            //(luminance)
                            val gray = (0.299f * r + 0.587f * g + 0.114f * b) / 255f

                            grayArray[y][x] = gray.coerceIn(0f, 1f)
                        }
                    }

                    cont.resume(grayArray)
                }
            }.addOnFailureListener {
                cont.resume(null)
            }
        }
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
}