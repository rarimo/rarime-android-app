package com.rarilabs.rarime.manager

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.bionet.BionetAnalizer
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.tflite.RunTFLiteFeatureExtractorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

enum class LikenessRule(val value: Int) {
    ALWAYS_ALLOW(0), REJECT(1), ASK_EVERYTIME(2);

    companion object {
        fun fromInt(value: Int) = LikenessRule.entries.first { it.value == value }
    }
}


data class LivenessInputs(
    val image: List<List<List<Int>>>,
    val features: List<Int>,
    val nonce: Int,   ///number from contract
    val address: Int,
    val threshold: Int
)

@Singleton
class LikenessManager @Inject constructor(
    @ApplicationContext private val application: Context,
    val sharedPrefsManager: SecureSharedPrefsManager
) {

    private val _selectedRule = MutableStateFlow(sharedPrefsManager.getSelectedLikenessRule())

    private val _isScanned = MutableStateFlow(sharedPrefsManager.getIsLikenessScanned())

    private val bionetAnalizer = BionetAnalizer()


    private val _faceImage: MutableStateFlow<Bitmap?> = MutableStateFlow(loadFaceImage())

    val faceImage: StateFlow<Bitmap?> = _faceImage.asStateFlow()

    val isScanned: StateFlow<Boolean>
        get() = _isScanned.asStateFlow()

    val selectedRule: StateFlow<LikenessRule?>
        get() = _selectedRule.asStateFlow()

    fun setSelectedRule(selectedRule: LikenessRule) {
        sharedPrefsManager.saveSelectedLikenessRule(selectedRule)
        _selectedRule.value = selectedRule
    }

    fun saveFaceImage(face: Bitmap) {
        sharedPrefsManager.saveLikenessFace(face)
        _faceImage.value = face
    }

    private fun loadFaceImage(): Bitmap? = sharedPrefsManager.getLikenessFace()


    suspend fun livenessProofGeneration(bitmap: Bitmap): ZkProof {

        val preparedImage = bionetAnalizer.getPreparedInputForML(bitmap)!!


        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(application, assetManager)

        val tfLite = RunTFLiteFeatureExtractorUseCase(
            context = application, modelName = "bio_net_v3.tflite"
        )

        val features = tfLite.invoke(preparedImage)

        val quantizedFeatures = features.map { it -> (it * 2.0.pow(15.0)).toInt() }

        val quantizedImage =
            listOf(preparedImage.map { it -> it.map { it2 -> (it2 * 2.0.pow(15.0)).toInt() } })


        val file = File("/data/data/com.rarilabs.rarime/likeness.zkey")

        val inputs = LivenessInputs(
            image = quantizedImage,
            features = quantizedFeatures,
            address = 123,
            threshold = 123,
            nonce = 107374182
        )
        return withContext(Dispatchers.Default) {
            zkp.bioent(
                zkeyFilePath = file.absolutePath,
                zkeyFileLen = file.length(),
                inputs = Gson().toJson(inputs)
            )
        }

    }


    fun setIsLikenessScanned(isScanned: Boolean) {
        sharedPrefsManager.saveIsLikenessScanned(isScanned)
        _isScanned.value = isScanned
    }

}