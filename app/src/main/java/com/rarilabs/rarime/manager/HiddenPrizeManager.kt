package com.rarilabs.rarime.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApiManager
import com.rarilabs.rarime.api.hiddenPrize.models.Included
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.bionet.BionetAnalizer
import com.rarilabs.rarime.util.tflite.RunTFLiteFeatureRGBExtractorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import identity.CallDataBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.math.pow

data class UserStats(
    val resetTime: Long, val extraAttemptsLeft: Int, val totalAttemptsCount: Int
)

data class Celebrity(
    val title: String,
    val description: String,
    val status: String,
    val image: String,
    val hint: String
)

class HiddenPrizeManager @Inject constructor(
    @ApplicationContext private val application: Context,
    private val apiManager: HiddenPrizeApiManager,
    private val identityManager: IdentityManager,
    private val rarimoContractManager: RarimoContractManager
) {

    private val tfLiteFileName: String = "face-recognition.tflite"

    private val _downloadProgressZkey = MutableStateFlow(0)

    val downloadProgressZkey: StateFlow<Int>
        get() = _downloadProgressZkey.asStateFlow()

    private val _referralsLimit = MutableStateFlow(0)

    val referralsLimit: StateFlow<Int>
        get () = _referralsLimit.asStateFlow()

    private val _referralsCount= MutableStateFlow(0)

    val referralsCount : StateFlow<Int>
        get () = _referralsCount.asStateFlow()

    private val _socialShare = MutableStateFlow(false)

    val socialShare :StateFlow<Boolean>
        get () = _socialShare.asStateFlow()

    private val _referralCode = MutableStateFlow("")

    val referralCode : StateFlow<String>
        get () =_referralCode.asStateFlow()

    private val _userStats = MutableStateFlow<UserStats?>(null)

    val userStats :StateFlow<UserStats?>
        get () = _userStats.asStateFlow()

    private var _celebrity: MutableStateFlow<Celebrity?> = MutableStateFlow(null)

    val celebrity: StateFlow<Celebrity?>
        get() = _celebrity.asStateFlow()


    suspend fun createUser(referredBy: String? = null) {

        val nullifier = identityManager.getUserPointsNullifierHex()


        val res = apiManager.createNewUser(referredBy, nullifier)
        val attrs = res.data.attributes

        _referralsLimit.value = attrs.referrals_limit
        _referralsCount.value = attrs.referrals_count
        _socialShare.value = attrs.social_share
    }

    suspend fun loadUserInfo() {
        val nullifier = identityManager.getUserPointsNullifierHex()
        val res = apiManager.getUserInfo(nullifier)
        val a = res.data.attributes

        _socialShare.value = a.social_share
        _referralsCount.value = a.referrals_count
        _referralsLimit.value = a.referrals_limit
        _referralCode.value = a.referral_code

        _userStats.value = res.included?.filterIsInstance<Included.UserStats>()?.firstOrNull()?.let {
            UserStats(
                resetTime = it.attributes?.reset_time ?: 0,
                extraAttemptsLeft = it.attributes?.extra_attempts_left ?: 0,
                totalAttemptsCount = it.attributes?.total_attempts_count ?: 0
            )
        }

        _celebrity.value =
            res.included?.filterIsInstance<Included.Celebrity>()?.firstOrNull()?.attributes?.let {
                Celebrity(
                    title = it.title,
                    description = it.description,
                    status = it.status,
                    image = it.image ?: "",
                    hint = it.hint ?: ""
                )
            }
    }

    private suspend fun submitCelebrityGuess(faceFeatures: List<Float>): List<Float>? {
        val nullifier = identityManager.getUserPointsNullifierHex()
        val res = apiManager.submitCelebrityGuess(faceFeatures, nullifier)

        _userStats.value =
            res.included?.filterIsInstance<Included.UserStats>()?.firstOrNull()?.attributes?.let {
                UserStats(
                    resetTime = it.reset_time,
                    extraAttemptsLeft = it.extra_attempts_left,
                    totalAttemptsCount = it.total_attempts_count
                )
            }

        if (!res.data.attributes.success) {
            return null
        }


        _celebrity.value =
            res.included?.filterIsInstance<Included.Celebrity>()?.firstOrNull()?.attributes?.let {
                Celebrity(
                    title = it.title,
                    description = it.description,
                    status = it.status,
                    image = it.image ?: "",
                    hint = it.hint ?: ""
                )
            }

        return res.data.attributes.original_feature_vector
    }


    private val fileDownloader = FileDownloaderInternal(application)

    private suspend fun downloadFile(url: String): File {
        fileDownloader.cancel()

        val faceRecognitionTFileHash = "3814d30ed40b217e18d321c9c0f13d1b"

        return fileDownloader.downloadFileBlocking(
            url, tfLiteFileName, fileHash = faceRecognitionTFileHash
        ) { progress ->
            if (_downloadProgressZkey.value != progress) {
                Log.i("Progress", progress.toString())
                _downloadProgressZkey.value = progress
            }
        }
    }


    suspend fun generateFaceFeatures(bitmap: Bitmap): List<Float> {

        val tfLiteFile = downloadFile(BaseConfig.FACE_RECOGNITION_MODEL_URL)

        val bionetAnalizer = BionetAnalizer()

        val preparedRGB = bionetAnalizer.getPreparedInputForML(bitmap)!!

        val tfLite = RunTFLiteFeatureRGBExtractorUseCase(
            modelFile = tfLiteFile
        )

        val features = tfLite.compute(preparedRGB)


        val backendFeatures = submitCelebrityGuess(features.toList())

        if (backendFeatures == null) {
            throw Exception("no")
        } else {
            return backendFeatures
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun claimTokens(features: List<Float>, bitmap: Bitmap) {

        val zkeyFile = downloadFile(BaseConfig.FACE_REGISTRY_ZKEY_URL)

        val bionetAnalizer = BionetAnalizer()

        val address = identityManager.evmAddress()

        val preparedImage = bionetAnalizer.getPreparedInputForZKML(bitmap)!!

        val faceContract = rarimoContractManager.getGuessCelebrity()

        val nonce = withContext(Dispatchers.IO) {
            faceContract.getVerificationNonce(address).send().toString()
        }

        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(application, assetManager)

        val quantizedFeatures = features.map { (it * 2.0.pow(15.0)).toInt().toString() }

        val quantizedImage = listOf(preparedImage.map {
            it.map { it2 ->
                (it2 * 2.0.pow(15.0)).toInt().toString()
            }
        })

        val inputs = LivenessInputs(
            image = quantizedImage,
            features = quantizedFeatures,
            address = address,
            threshold = "107374182",
            nonce = nonce
        )

        withContext(Dispatchers.Default) {

            val zkproof = zkp.bioent(
                zkeyFilePath = zkeyFile.absolutePath,
                zkeyFileLen = zkeyFile.length(),
                inputs = Gson().toJson(inputs)
            )


            Log.i("Tag", GsonBuilder().setPrettyPrinting().create().toJson(zkproof))


            val callDataBuilder = CallDataBuilder()
            val callData = callDataBuilder.buildFaceRegistryRegisterUser(
                Gson().toJson(zkproof).toByteArray()
            )

            val response = apiManager.claimTokens("0x" + callData.toHexString())

            val isSuccessful =
                rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)

            if (!isSuccessful) {
                throw Exception("cant send registration")
            }

        }

    }

    fun Bitmap.flipVertically(): Bitmap {
        val matrix = Matrix().apply { postScale(1f, -1f, width / 2f, height / 2f) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun normalizeInput(rgb: FloatArray): FloatArray {
        return rgb.map { it / 255f }.toFloatArray()
    }
}
