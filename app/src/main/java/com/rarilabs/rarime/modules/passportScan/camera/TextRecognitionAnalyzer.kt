package com.rarilabs.rarime.modules.passportScan.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.sf.scuba.data.Gender
import org.jmrtd.lds.icao.MRZInfo
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class TextRecognitionAnalyzer(
    private val onDetectedTextUpdated: (MRZInfo) -> Unit
) : ImageAnalysis.Analyzer {


    private var scannedTextBuffer: String? = null
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val textRecognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val PASSPORT_TD_3_LINE_1_REGEX = "(P[A-Z0-9<]{1})([A-Z]{3})([A-Z0-9<]{39})"

    private val PASSPORT_TD_3_LINE_2_REGEX =
        "([A-Z0-9<]{9})([0-9]{1})([A-Z]{3})([0-9]{6})([0-9]{1})([M|F|X|<]{1})([0-9]{6})([0-9]{1})([A-Z0-9<]{14})([0-9<]{1})([0-9]{1})"


    private fun onProcess(
        results: Text,
    ) {
        Log.i("Text", results.text)
        scannedTextBuffer = ""
        val blocks = results.textBlocks
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    filterScannedText(elements[k])
                }
            }
        }
    }

    fun getFourthOfSixParts(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val partHeight = height / 6

        val x = 0
        val y = partHeight * 3

        return Bitmap.createBitmap(bitmap, x, y, width, partHeight)
    }

    fun getFourthOfSixPartsByWidth(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val partWidth = width / 6

        val x = partWidth * 3
        val y = 0

        return Bitmap.createBitmap(bitmap, x, y, partWidth, height)
    }

    private fun filterScannedText(text: Text.Element) {
        scannedTextBuffer += text.text.replace("«", "<<")
        val patternPassportTD3Line1 = Pattern.compile(PASSPORT_TD_3_LINE_1_REGEX)
        val matcherPassportTD3Line1 = patternPassportTD3Line1.matcher(scannedTextBuffer)
        val patternPassportTD3Line2 = Pattern.compile(PASSPORT_TD_3_LINE_2_REGEX)
        val matcherPassportTD3Line2 = patternPassportTD3Line2.matcher(scannedTextBuffer)
        if (matcherPassportTD3Line1.find() && matcherPassportTD3Line2.find()) {
            val line2 = matcherPassportTD3Line2.group(0)
            var documentNumber = line2.substring(0, 9)
            documentNumber = documentNumber.replace("O", "0")
            val dateOfBirthDay = line2.substring(13, 19)
            val expiryDate = line2.substring(21, 27)
            ErrorHandler.logDebug(
                "Tag",
                "Scanned Text Buffer Passport ->>>> Doc Number: $documentNumber DateOfBirth: $dateOfBirthDay ExpiryDate: $expiryDate"
            )
            val mrz = buildTempMrz(documentNumber, dateOfBirthDay, expiryDate) ?: return
            onDetectedTextUpdated(mrz)
        }
    }

    private fun buildTempMrz(
        documentNumber: String, dateOfBirth: String, expiryDate: String
    ): MRZInfo? {
        var mrzInfo: MRZInfo? = null
        try {
            mrzInfo = MRZInfo(
                "P",
                "NNN",
                "",
                "",
                documentNumber,
                "NNN",
                dateOfBirth,
                Gender.UNSPECIFIED,
                expiryDate,
                ""
            )
            return if (isMrzValid(mrzInfo)) {
                mrzInfo
            } else {
                null
            }
        } catch (e: Exception) {
            ErrorHandler.logDebug(
                "MRZ ERROR", "MRZInfo error : " + e.localizedMessage
            )
        }
        return mrzInfo
    }

    private fun isMrzValid(mrzInfo: MRZInfo): Boolean {
        return mrzInfo.documentNumber != null && mrzInfo.documentNumber.length >= 8 && mrzInfo.dateOfBirth != null && mrzInfo.dateOfBirth.length == 6 && mrzInfo.dateOfExpiry != null && mrzInfo.dateOfExpiry.length == 6
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        scope.launch {
            val mediaImage: Image = imageProxy.image ?: run { imageProxy.close(); return@launch }

            val bitmap = mediaImage.toBitmap()
            val croppedBitmap = getFourthOfSixPartsByWidth(bitmap!!)
            val grayscaleBitmap = croppedBitmap!!.toGrayscaleHighContrast()

            val inputImage =
                InputImage.fromBitmap(grayscaleBitmap, imageProxy.imageInfo.rotationDegrees)

            suspendCoroutine { continuation ->
                textRecognizer.process(inputImage).addOnSuccessListener { visionText: Text ->
                    val detectedText: String = visionText.text
                    if (detectedText.isNotBlank()) {
                        onProcess(visionText)
                    }
                }.addOnCompleteListener {
                    continuation.resume(Unit)
                }
            }

            delay(THROTTLE_TIMEOUT_MS)
        }.invokeOnCompletion { exception ->
            exception?.printStackTrace()
            imageProxy.close()
        }
    }


    companion object {
        const val THROTTLE_TIMEOUT_MS = 41L
    }

    private fun Image.toBitmap(): Bitmap? {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun Bitmap.toGrayscaleHighContrast(): Bitmap {
        val bmpGrayscale = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f)
            val scale = 255f
            val contrast = 2f // change this value to adjust contrast
            val translate = (-0.5f * scale * contrast + 0.5f * scale).toInt()
            set(
                floatArrayOf(
                    contrast,
                    0f,
                    0f,
                    0f,
                    translate.toFloat(),
                    0f,
                    contrast,
                    0f,
                    0f,
                    translate.toFloat(),
                    0f,
                    0f,
                    contrast,
                    0f,
                    translate.toFloat(),
                    0f,
                    0f,
                    0f,
                    1f,
                    0f
                )
            )
        }
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(this, 0f, 0f, paint)
        return bmpGrayscale
    }


}