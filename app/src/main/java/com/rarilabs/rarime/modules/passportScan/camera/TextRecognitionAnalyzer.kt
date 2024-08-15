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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun buildTempMrz(
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

fun isMrzValid(mrzInfo: MRZInfo): Boolean {
    return mrzInfo.documentNumber != null && mrzInfo.documentNumber.length >= 8 && mrzInfo.dateOfBirth != null && mrzInfo.dateOfBirth.length == 6 && mrzInfo.dateOfExpiry != null && mrzInfo.dateOfExpiry.length == 6
}

class TextRecognitionAnalyzer(
    private val onDetectedTextUpdated: (MRZInfo) -> Unit
) : ImageAnalysis.Analyzer {


    private var scannedTextBuffer: String? = null
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val textRecognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val PASSPORT_TD_3_LINE_1_REGEX = "(P[A-Z0-9<]{1})([A-Z]{3})([A-Z0-9<]{39})"

    private val PASSPORT_TD_3_LINE_2_REGEX = Regex(
        "[0-9A-Z<]{10}[A-Z]{3}[0-9]{7}[MFX][0-9]{7}"
    )

    private fun onProcess(
        results: Text,
    ) {
        filterScannedTextPassport(results.text.replace(" ", ""))
    }

    fun getFourthOfSixParts(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val partHeight = height / 6

        val x = 0
        val y = partHeight * 3

        return Bitmap.createBitmap(bitmap, x, y, width, partHeight)
    }

    private fun verifyChecksum(input: String): Boolean {

        // Ensure the input length is either 7 or 10
        if (input.length != 7 && input.length != 10) {
            throw IllegalArgumentException("Input must be either 7 or 10 characters long")
        }

        fun charToNumber(char: Char): Int {
            return when (char) {
                in '0'..'9' -> char.toString().toInt()
                in 'A'..'Z' -> (char - 'A' + 10) % 10
                in '<'..'<' -> 0
                else -> throw IllegalArgumentException("Invalid character in input")
            }
        }

        // Extract the digits and checksum from the input
        val digits = input.substring(0, input.length - 1).map { charToNumber(it) }
        val checksum = charToNumber(input.last())

        // Define the multipliers for length 7 and 10
        val multipliers =
            if (input.length == 7) listOf(7, 3, 1, 7, 3, 1) else listOf(7, 3, 1, 7, 3, 1, 7, 3, 1)

        // Multiply the digits by the multipliers and sum the results
        val sum = digits.zip(multipliers).sumOf { (digit, multiplier) -> digit * multiplier }


        // Calculate the checksum from the sum
        val calculatedChecksum = sum % 10

        println("Calculated :$calculatedChecksum, waited for: " + input.last())

        // Return whether the calculated checksum matches the provided checksum
        return calculatedChecksum == checksum
    }

    fun getFourthOfSixPartsByWidth(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val partWidth = width / 6
        val x = partWidth * 3
        val y = 0
        return Bitmap.createBitmap(bitmap, x, y, partWidth, height)
    }

    private fun filterScannedTextPassport(text: String) {
        val updatedText = text.replace("«", "<<").uppercase()

        val match = PASSPORT_TD_3_LINE_2_REGEX.find(updatedText)

        try {
            match?.let {

                Log.i("PASSPORT_TD_3_LINE_2_REG", it.value)
                var documentNumberWithCheckSum = it.value.substring(0, 10)
                documentNumberWithCheckSum = documentNumberWithCheckSum.replace("O", "0")
                val dateOfBirthWithCheckSum = it.value.substring(13, 20)
                val expiryDateWithCheckSum = it.value.substring(21, 28)

                val check1 = verifyChecksum(dateOfBirthWithCheckSum)
                val check2 = verifyChecksum(
                    documentNumberWithCheckSum
                )
                val check3 = verifyChecksum(expiryDateWithCheckSum)

                if (!check1 || !check2 || !check3) {
                    Log.i("Check", "$check1 $check2 $check3")
                    return
                }

                val documentNumber = match.value.substring(0, 9)
                val dateOfBirth = match.value.substring(13, 19)
                val expiryDate = match.value.substring(21, 27)

                val mrz = buildTempMrz(
                    documentNumber,
                    dateOfBirth,
                    expiryDate,
                ) ?: return
                onDetectedTextUpdated(mrz)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        scope.launch {
            val mediaImage: Image = imageProxy.image ?: run { imageProxy.close(); return@launch }

            val bitmap = mediaImage.toBitmap()
            val grayscaleBitmap = bitmap!!.toGrayscaleHighContrast()

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