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
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


class BinetAnalyzer {

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()


    private val realTimeOpts =
        FaceDetectorOptions.Builder().setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build()

    @OptIn(ExperimentalGetImage::class)
    suspend fun getPreparedInputForZKML(bitmap: Bitmap): Array<FloatArray>? =
        suspendCancellableCoroutine { cont ->

            val detector = FaceDetection.getClient(realTimeOpts)
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
                    bmp.scale(40, 40).copy(Bitmap.Config.ARGB_8888, false)
                }

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


    suspend fun checkBound(bitmap: Bitmap): Bitmap? = suspendCancellableCoroutine { cont ->

        val detector = FaceDetection.getClient(realTimeOpts)
        val image = InputImage.fromBitmap(bitmap, 0)

        detector.process(image).addOnSuccessListener { faces ->
            if (faces.isEmpty()) {
                cont.resume(null)
                return@addOnSuccessListener
            }

            val face = faces.first()
            val bounds = face.boundingBox

            val side = min(bounds.width(), bounds.height())
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

//                val f = File("/data/data/com.rarilabs.rarime/files/tg_image_2487144994.jpeg")
//
//
//                val filePath: String = f.path
//                val faceBmp = BitmapFactory.decodeFile(filePath)
//                    .let { bmp ->
//                    bmp.scale(112, 112).copy(Bitmap.Config.ARGB_8888, false)
//                }


            val faceBmp = Bitmap.createBitmap(
                bitmap, squareRect.left, squareRect.top, cropSize, cropSize
            ).resize(112, 112).copy(Bitmap.Config.ARGB_8888, false)


            cont.resume(faceBmp)

        }.addOnFailureListener { cont.resume(null) }

        cont.invokeOnCancellation { detector.close() }
    }

    @OptIn(ExperimentalGetImage::class)
    suspend fun getPreparedInputForML(bitmap: Bitmap): FloatArray? =
        suspendCancellableCoroutine { cont ->

            val detector = FaceDetection.getClient(realTimeOpts)
            val image = InputImage.fromBitmap(bitmap, 0)

            detector.process(image).addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    cont.resume(null)
                    return@addOnSuccessListener
                }

                val face = faces.first()
                val bounds = face.boundingBox

                val side = min(bounds.width(), bounds.height())
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

//                val f = File("/data/data/com.rarilabs.rarime/files/tg_image_2487144994.jpeg")
//
//
//                val filePath: String = f.path
//                val faceBmp = BitmapFactory.decodeFile(filePath)
//                    .let { bmp ->
//                    bmp.scale(112, 112).copy(Bitmap.Config.ARGB_8888, false)
//                }


                val faceBmp = Bitmap.createBitmap(
                    bitmap, squareRect.left, squareRect.top, cropSize, cropSize
                ).let { bmp ->
                    bmp.resize(112, 112).copy(Bitmap.Config.ARGB_8888, false)
                }


                val imageDataIntArray = bitmapToNormalizedRgbFloatArray(faceBmp)

                val normalizerRgbData = imageDataIntArray.map { it.toFloat() / 255f }

                cont.resume(normalizerRgbData.toFloatArray())
            }.addOnFailureListener { cont.resume(null) }

            cont.invokeOnCancellation { detector.close() }
        }

    private fun bitmapToNormalizedRgbFloatArray(bitmap: Bitmap): IntArray {
        val width = bitmap.width
        val height = bitmap.height

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val intArray = IntArray(width * height * 3)


        var outIndex = 0
        for (pixel in pixels) {
            intArray[outIndex++] = Color.red(pixel)
            intArray[outIndex++] = Color.green(pixel)
            intArray[outIndex++] = Color.blue(pixel)
        }

        return intArray
    }
}

@Throws(IOException::class)
fun Bitmap.resize(width: Int, height: Int): Bitmap {
    // 1) Размер «квадрата» по большей из запрошенных сторон
    val dimensionSize = max(width, height)
    val maxSize = dimensionSize.toFloat()

    // 2) Исходные размеры
    val originalW = this.width.toFloat()
    val originalH = this.height.toFloat()

    // 3) Масштаб, чтобы вписаться в квадрат maxSize×maxSize
    val scale = min(maxSize / originalW, maxSize / originalH)

    // 4) Вычисляем целевые размеры с сохранением пропорций
    val targetW = (originalW * scale).roundToInt()
    val targetH = (originalH * scale).roundToInt()

    // 5) Масштабируем Bitmap
    val scaled = this.scale(targetW, targetH)

    // 6) Вычисляем смещение для центрированного кропа
    val (offsetX, offsetY) = if (width < height) {
        // обрезаем по горизонтали
        val dx = scaled.width - width
        Pair(if (dx > 0) dx / 2 else 0, 0)
    } else {
        // обрезаем по вертикали
        val dy = scaled.height - height
        Pair(0, if (dy > 0) dy / 2 else 0)
    }

    // 7) Пытаемся вырезать нужный кусок
    try {
        return Bitmap.createBitmap(scaled, offsetX, offsetY, width, height)
    } catch (e: IllegalArgumentException) {
        throw IOException("Failed to crop image to $width×$height", e)
    }
}