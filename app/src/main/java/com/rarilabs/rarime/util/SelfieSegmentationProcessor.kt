package com.rarilabs.rarime.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import java.nio.FloatBuffer

class BackgroundRemover {

    private val segmenter = Segmentation.getClient(
        SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
    )

    fun removeBackground(bitmap: Bitmap, callback: (Bitmap?) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        segmenter.process(image)
            .addOnSuccessListener { segmentationMask ->
                val maskBitmap = convertMaskToBitmap(segmentationMask, bitmap.width, bitmap.height)
                val resultBitmap = applyMask(bitmap, maskBitmap)
                callback(resultBitmap)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                callback(null)
            }
    }

    private fun convertMaskToBitmap(
        mask: SegmentationMask,
        originalWidth: Int,
        originalHeight: Int
    ): Bitmap {
        val maskWidth = mask.width
        val maskHeight = mask.height

        val buffer = mask.buffer
        val floatBuffer: FloatBuffer = buffer.asFloatBuffer()
        floatBuffer.rewind()

        val maskArray = FloatArray(maskWidth * maskHeight)
        floatBuffer.get(maskArray)

        val maskBitmap = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ALPHA_8)
        val pixels = IntArray(maskWidth * maskHeight)
        for (i in maskArray.indices) {
            var alpha = (maskArray[i] * 255).toInt().coerceIn(0, 255)

            alpha = 255 - alpha //normalize for bitmap TODO: check if it rly 255

            pixels[i] = Color.argb(alpha, 0, 0, 0)
        }
        maskBitmap.setPixels(pixels, 0, maskWidth, 0, 0, maskWidth, maskHeight)

        return Bitmap.createScaledBitmap(maskBitmap, originalWidth, originalHeight, true)
    }


    private fun applyMask(original: Bitmap, maskBitmap: Bitmap): Bitmap {
        val width = original.width
        val height = original.height

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalPixels = IntArray(width * height)
        val maskPixels = IntArray(width * height)

        original.getPixels(originalPixels, 0, width, 0, 0, width, height)
        maskBitmap.getPixels(maskPixels, 0, width, 0, 0, width, height)

        for (i in 0 until width * height) {
            val maskAlpha = maskPixels[i] ushr 24  // число от 0 до 255
            val alpha = 255 - maskAlpha
            originalPixels[i] = (alpha shl 24) or (originalPixels[i] and 0x00FFFFFF)
        }

        result.setPixels(originalPixels, 0, width, 0, 0, width, height)
        return result
    }
}