package com.rarilabs.rarime.modules.passportScan.camera

import android.media.Image
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

    private fun filterScannedText(text: Text.Element) {
        scannedTextBuffer += text.text
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
            val inputImage: InputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

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
}