package com.rarilabs.rarime.manager

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.likeness.LikenessApiManager
import com.rarilabs.rarime.contracts.rarimo.FaceRegistry
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.bionet.BionetAnalizer
import com.rarilabs.rarime.util.tflite.RunTFLiteFeatureExtractorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import identity.CallDataBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
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
    val image: List<List<List<String>>>,
    val features: List<String>,
    val nonce: String,
    val address: String, // Nullifier
    val threshold: String
)

data class FaceRegistryNoInclusionInputs(
    val event_id: String,
    val nonce: String,
    val value: String,
    val sk_identity: String,
)

enum class LivenessProcessingStatus(val title: String) {
    DOWNLOADING("Downloading circuit data"),
    EXTRACTING_FEATURES("Extracting image features"),
    RUNNING_ZKML("Running ZKML"),
    FINISH(""),
}

@Singleton
class LikenessManager @Inject constructor(
    @ApplicationContext private val application: Context,
    private val sharedPrefsManager: SecureSharedPrefsManager,
    private val rarimoContractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
    private val likenessApiManager: LikenessApiManager
) {
    private val zkeyFileName = "likeness.zkey"

    private val _selectedRule = MutableStateFlow(sharedPrefsManager.getSelectedLikenessRule())

    private val _isRegistered = MutableStateFlow(sharedPrefsManager.getLivenessProof() != null)

    private var _state = MutableStateFlow(LivenessProcessingStatus.DOWNLOADING)

    private var _errorState = MutableStateFlow<LivenessProcessingStatus?>(null)

    val errorState: StateFlow<LivenessProcessingStatus?>
        get() = _errorState.asStateFlow()

    val state: StateFlow<LivenessProcessingStatus>
        get() = _state.asStateFlow()


    private val _faceImage: MutableStateFlow<Bitmap?> = MutableStateFlow(loadFaceImage())

    val faceImage: StateFlow<Bitmap?> = _faceImage.asStateFlow()

    private val _downloadProgress: MutableStateFlow<Int> = MutableStateFlow(0)

    val downloadProgress: StateFlow<Int>
        get() = _downloadProgress.asStateFlow()


    val isRegistered: StateFlow<Boolean>
        get() = _isRegistered.asStateFlow()

    val selectedRule: StateFlow<LikenessRule?>
        get() = _selectedRule.asStateFlow()

    suspend fun setSelectedRule(selectedRule: LikenessRule) {
        if (selectedRule == _selectedRule.value)
            return

        if (_isRegistered.value) {
            changeLikenessRule(selectedRule)
        }
        sharedPrefsManager.saveSelectedLikenessRule(selectedRule)
        _selectedRule.value = selectedRule
    }

    fun saveFaceImage(face: Bitmap) {
        sharedPrefsManager.saveLikenessFace(face)
        _faceImage.value = face
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initData()
        }
    }

    private suspend fun initData() {
        _isRegistered.value = isUserRegistered()
        _selectedRule.value = getRule()
    }

    private fun loadFaceImage(): Bitmap? = sharedPrefsManager.getLikenessFace()


    private suspend fun isUserRegistered(): Boolean {
        if (sharedPrefsManager.getLivenessProof() != null) {
            return true
        }

        val address =
            BigInteger(Numeric.hexStringToByteArray(identityManager.getNullifierForFaceLikeness()))

        val faceContract = rarimoContractManager.getFaceRegistry()

        val isAlreadyRegistered = withContext(Dispatchers.IO) {
            faceContract.isUserRegistered(address).send()
        }

        return isAlreadyRegistered

    }

    suspend fun changeLikenessRule(newRule: LikenessRule) {
        val ruleValue = newRule.value

        val address =
            BigInteger(Numeric.hexStringToByteArray(identityManager.getNullifierForFaceLikeness()))

        Log.i("Nullifier", address.toString())

        val faceContract = rarimoContractManager.getFaceRegistry()

        val nonce = withContext(Dispatchers.IO) {
            faceContract.getVerificationNonce(address).send().toString()
        }

        val privateKey = identityManager.privateKeyBytes

        val assetContext: Context =
            (application).createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(
            assetContext, assetManager
        )

        val inclusionProofInputs = FaceRegistryNoInclusionInputs(
            value = "0",
            nonce = nonce,
            sk_identity = BigInteger(privateKey).toString(),
            event_id = FaceRegistry.EVENT_ID
        )

        val inclusionProof = withContext(Dispatchers.Default) {
            zkp.generateZKP(
                "face_registry_no_inclusion.zkey",
                datFile = R.raw.face_registry_no_inclusion,
                inputs = Gson().toJson(inclusionProofInputs).toByteArray(),
                proofFunction = ZkpUtil::faceRegistryNoInclusion
            )
        }

        val updateRuleCallData = CallDataBuilder().buildFaceRegistryUpdateRule(
            ruleValue.toString(),
            Gson().toJson(inclusionProof).toByteArray(),
        )

        val response = likenessApiManager.likenessRegistry(Numeric.toHexString(updateRuleCallData))

        val isSuccessful =
            rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)

        if (!isSuccessful) {
            throw Exception("Cant change rule")
        }
    }

    private suspend fun getRule(): LikenessRule? {

        if (_selectedRule.value != null) {
            return _selectedRule.value!!
        }

        val contract = rarimoContractManager.getFaceRegistry()

        val address = identityManager.getNullifierForFaceLikeness()

        try {

            val rule = withContext(Dispatchers.IO) {
                contract.getRule(BigInteger(Numeric.hexStringToByteArray(address))).send()
            }

            val likenessRule = LikenessRule.fromInt(rule.toInt())

            _selectedRule.value = likenessRule
            sharedPrefsManager.saveSelectedLikenessRule(likenessRule)
            return likenessRule
        } catch (e: Exception) {
            return null
        }
    }

    private val fileDownloader = FileDownloaderInternal(application)

    private suspend fun downloadLivenessZkey(): File {
        fileDownloader.cancel()
        _downloadProgress.value = 0

        return fileDownloader.downloadFileBlocking(
            BaseConfig.FACE_REGISTRY_ZKEY_URL,
            zkeyFileName
        ) { progress ->
            if (_downloadProgress.value != progress) {
                Log.i("Progress", progress.toString())
                _downloadProgress.value = progress
            }
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun livenessProofGeneration(bitmap: Bitmap) {
        try {
            _errorState.value = null
            _state.value = LivenessProcessingStatus.DOWNLOADING

            // TODO: Restore option I
            val file =
                // Option I
                downloadLivenessZkey()
            // Option II
            // File("/data/data/com.rarilabs.rarime/files/likeness.zkey")

            _state.value = LivenessProcessingStatus.EXTRACTING_FEATURES

            val bionetAnalizer = BionetAnalizer()


            val address =
                BigInteger(Numeric.hexStringToByteArray(identityManager.getNullifierForFaceLikeness()))

            val preparedImage = bionetAnalizer.getPreparedInputForML(bitmap)!!

            val faceContract = rarimoContractManager.getFaceRegistry()

            val isAlreadyRegistered = withContext(Dispatchers.IO) {
                faceContract.isUserRegistered(address).send()
            }

            if (isAlreadyRegistered) {
                Log.i("Likeness", "Already registered")
                return
            }

            val nonce = withContext(Dispatchers.IO) {
                faceContract.getVerificationNonce(address).send().toString()
            }

            val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
            val assetManager = assetContext.assets

            val zkp = ZKPUseCase(application, assetManager)

            val tfLite = RunTFLiteFeatureExtractorUseCase(
                context = application, modelName = "bio_net_v3.tflite"
            )

            val features = tfLite.invoke(preparedImage)

            val quantizedFeatures = features.map { (it * 2.0.pow(15.0)).toInt().toString() }

            val quantizedImage =
                listOf(preparedImage.map {
                    it.map { it2 ->
                        (it2 * 2.0.pow(15.0)).toInt().toString()
                    }
                })

            _state.value = LivenessProcessingStatus.RUNNING_ZKML


            val inputs = LivenessInputs(
                image = quantizedImage,
                features = quantizedFeatures,
                address = address.toString(),
                threshold = "107374182",
                nonce = nonce
            )

            Log.i("Inputs", GsonBuilder().setPrettyPrinting().create().toJson(inputs))

            withContext(Dispatchers.Default) {

                val zkproof = zkp.bioent(
                    zkeyFilePath = file.absolutePath,
                    zkeyFileLen = file.length(),
                    inputs = Gson().toJson(inputs)
                )


                Log.i("Tag", GsonBuilder().setPrettyPrinting().create().toJson(zkproof))


                val callDataBuilder = CallDataBuilder()
                val callData =
                    callDataBuilder.buildFaceRegistryRegisterUser(
                        Gson().toJson(zkproof).toByteArray()
                    )

                val response = likenessApiManager.likenessRegistry("0x" + callData.toHexString())

                val isSuccessful =
                    rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)

                if (!isSuccessful) {
                    throw Exception("cant send registration")
                }

                sharedPrefsManager.saveLivenessProof(zkproof)

                changeLikenessRule(_selectedRule.value!!)


                _state.value = LivenessProcessingStatus.FINISH
            }
        } catch (e: Exception) {
            ErrorHandler.logError("livenessProofGeneration error", e.message.toString())
            _errorState.value = _state.value
        }

    }

}