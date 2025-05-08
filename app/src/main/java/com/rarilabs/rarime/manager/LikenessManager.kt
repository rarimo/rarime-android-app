package com.rarilabs.rarime.manager

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.bionet.BionetAnalizer
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.tflite.RunTFLiteFeatureExtractorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import identity.CallDataBuilder
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
    UNSET(0), USE_AND_PAY(1), NOT_USE(2), ASK_FIRST(3);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}

data class LivenessInputs(
    val image: List<List<List<Int>>>,
    val features: List<Int>,
    val nonce: Int,
    val address: Int, // Nullifier
    val threshold: Int
)

enum class LivenessProcessingStatus(val title: String) {
    DOWNLOADING("Downloading circuit data"), EXTRACTING_FEATURES("Extracting image features"), RUNNING_ZKML(
        "Running ZKML"
    ),
    FINSH("")
}

@Singleton
class LikenessManager @Inject constructor(
    @ApplicationContext private val application: Context,
    val sharedPrefsManager: SecureSharedPrefsManager,
    val rarimoContractManager: RarimoContractManager,
    val identityManager: IdentityManager
) {

    private val zkeyFileName = "likeness.zkey"

    private val _selectedRule = MutableStateFlow(sharedPrefsManager.getSelectedLikenessRule())

    private val _isScanned = MutableStateFlow(sharedPrefsManager.getLivenessProof() != null)

    private var _state = MutableStateFlow(LivenessProcessingStatus.DOWNLOADING)

    val state: StateFlow<LivenessProcessingStatus>
        get() = _state.asStateFlow()


    private val _faceImage: MutableStateFlow<Bitmap?> = MutableStateFlow(loadFaceImage())

    val faceImage: StateFlow<Bitmap?> = _faceImage.asStateFlow()

    private val _downloadProgress: MutableStateFlow<Int> = MutableStateFlow(0)

    val downloadProgress: StateFlow<Int>
        get() = _downloadProgress.asStateFlow()


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


    suspend fun changeLikenessRule(newRule: LikenessRule) {
        val ruleValue = newRule.value


        val nullifier = identityManager.getNullifierForFaceLikeness()
        val eventId = ""
        val nonce = ""

        val assetContext: Context =
            (application).createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(
            assetContext, assetManager
        )

//        val inclusionProof = withContext(Dispatchers.Default) {
//            zkp.generateZKP(
//                "face_registry_no_inclusion.zkey",
//                datFile = R.raw.face_registry_no_inclusion,
//                inputs = inclusionProofInputs,
//                proofFunction = ZkpUtil::faceRegistryNoInclusion
//            )
//        }

        // send proof

    }


    private fun generateChangeLikenessRuleProofInputs() {
        val faceRegContract = rarimoContractManager.getFaceRegistry()

        val eventId = ""

        //val nonce = faceRegContract.getVerificationNonce()
    }

    private suspend fun downloadLivenessZkey(): File {
        val fileDownloader = FileDownloaderInternal(application)

        val file =
            fileDownloader.downloadFileBlocking(BaseConfig.FACE_REGISTRY_ZKEY_URL, zkeyFileName) {
                if (_downloadProgress.value != it) {
                    Log.i("Proggress", it.toString())
                    _downloadProgress.value = it
                }
            }

        return file
    }


    suspend fun livenessProofGeneration(bitmap: Bitmap): ZkProof {

        _state.value = LivenessProcessingStatus.DOWNLOADING

        val file = downloadLivenessZkey()

        _state.value = LivenessProcessingStatus.EXTRACTING_FEATURES

        val bionetAnalizer = BionetAnalizer()

        val preparedImage = bionetAnalizer.getPreparedInputForML(bitmap)!!

        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(application, assetManager)


        val tfLite = RunTFLiteFeatureExtractorUseCase(
            context = application, modelName = "bio_net_v3.tflite"
        )

        val features = tfLite.invoke(preparedImage)

        val quantizedFeatures = features.map { (it * 2.0.pow(15.0)).toInt() }

        val quantizedImage =
            listOf(preparedImage.map { it.map { it2 -> (it2 * 2.0.pow(15.0)).toInt() } })

        _state.value = LivenessProcessingStatus.RUNNING_ZKML


        val inputs = LivenessInputs(
            image = quantizedImage,
            features = quantizedFeatures,
            address = 123,
            threshold = 123,
            nonce = 107374182
        )

        return withContext(Dispatchers.Default) {
            val zkproof = zkp.bioent(
                zkeyFilePath = file.absolutePath,
                zkeyFileLen = file.length(),
                inputs = Gson().toJson(inputs)
            )


            val callDataBuilder = CallDataBuilder()
            val callData =
                callDataBuilder.buildFaceRegistryRegisterUser(Gson().toJson(zkproof).toByteArray())

            //TODO send to backend

            sharedPrefsManager.saveLivenessProof(zkproof)

            _state.value = LivenessProcessingStatus.FINSH

            return@withContext zkproof
        }

    }


    fun setIsLikenessScanned(isScanned: Boolean) {
        //sharedPrefsManager.saveIsLikenessScanned(isScanned)
        _isScanned.value = isScanned
    }

}