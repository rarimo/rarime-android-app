package com.rarilabs.rarime.manager

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.hiddenPrize.HiddenPrizeApiManager
import com.rarilabs.rarime.api.hiddenPrize.models.Included
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.bionet.BinetAnalyzer
import com.rarilabs.rarime.util.tflite.RunTFLiteFeatureRGBExtractorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import identity.CallDataBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import javax.inject.Inject
import kotlin.math.pow


data class UserStats(
    val resetTime: Long,
    val attemptsLeft: Int,
    val extraAttemptsLeft: Int,
    val totalAttemptsCount: Int
)

data class Shares(
    val isSocialShare: Boolean, val referralsCount: Int, val referralsLimit: Int
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
    private val faceZkeyName: String = "face-recognition.zkey"

    private val faceRecognitionTFileHash = "3814d30ed40b217e18d321c9c0f13d1b"
    private val faceZkeyHash = "eecb7976f40e7bce22f310ed3d66f8f0"

    private val _downloadProgressZkey = MutableStateFlow(0)

    val downloadProgressZkey: StateFlow<Int>
        get() = _downloadProgressZkey.asStateFlow()

    private val _referralCode = MutableStateFlow("")

    val referralCode: StateFlow<String>
        get() = _referralCode.asStateFlow()

    private val _userStats = MutableStateFlow<UserStats?>(null)

    val userStats: StateFlow<UserStats?>
        get() = _userStats.asStateFlow()

    private var _celebrity: MutableStateFlow<Celebrity?> = MutableStateFlow(null)

    val celebrity: StateFlow<Celebrity?>
        get() = _celebrity.asStateFlow()

    private val _shares = MutableStateFlow<Shares?>(null)

    val shares: StateFlow<Shares?>
        get() = _shares.asStateFlow()


    suspend fun createUser(referredBy: String? = null) {

        val nullifier = identityManager.getUserPointsNullifierHex()


        val res = apiManager.createNewUser(referredBy, nullifier)
        val attrs = res.data.attributes
        val included = res.included
        _shares.value = Shares(
            isSocialShare = attrs.social_share,
            referralsLimit = attrs.referrals_limit,
            referralsCount = attrs.referrals_count
        )

    }

    suspend fun loadUserInfo() {
        val nullifier = identityManager.getUserPointsNullifierHex()
        val res = apiManager.getUserInfo(nullifier)
        val attributes = res.data.attributes


        _shares.value = Shares(
            isSocialShare = attributes.social_share,
            referralsCount = attributes.referrals_count,
            referralsLimit = attributes.referrals_limit
        )
        _referralCode.value = attributes.referral_code

        _userStats.value =
            res.included?.filterIsInstance<Included.UserStats>()?.firstOrNull()?.let {
                UserStats(

                    resetTime = it.attributes?.reset_time ?: 0,
                    attemptsLeft = it.attributes?.attempts_left ?: 0,
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
                    attemptsLeft = it.attempts_left,
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

    private suspend fun downloadFile(url: String, fileName: String, hash: String): File {
        fileDownloader.cancel()


        return fileDownloader.downloadFileBlocking(
            url, fileName, fileHash = hash
        ) { progress ->
            if (_downloadProgressZkey.value != progress) {
                _downloadProgressZkey.value = progress
            }
        }
    }

    suspend fun generateFaceFeatures(bitmap: Bitmap): List<Float> {

        return withContext(Dispatchers.Default) {
            val tfLiteFile = downloadFile(
                BaseConfig.FACE_RECOGNITION_MODEL_URL,
                fileName = tfLiteFileName,
                hash = faceRecognitionTFileHash
            )

            val binetAnalyzer = BinetAnalyzer()

            val preparedRGB = binetAnalyzer.getPreparedInputForML(bitmap)!!


            val tfLite = RunTFLiteFeatureRGBExtractorUseCase(
                modelFile = tfLiteFile
            )

            val features = tfLite.compute(preparedRGB)

            val backendFeatures = submitCelebrityGuess(features.toList())

            if (backendFeatures == null) {
                throw Exception("no")
            } else {
                _downloadProgressZkey.value = 0
                return@withContext backendFeatures
            }
        }
    }


    suspend fun addExtraAttempts() {
        val nullifier = identityManager.getUserPointsNullifierHex()

        apiManager.addExtraAttemptSocialShare(nullifier)
        loadUserInfo()
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun claimTokens(features: List<Float>, bitmap: Bitmap) {

        withContext(Dispatchers.Default) {
            _downloadProgressZkey.value = 0

            val zkeyFile =
                downloadFile(BaseConfig.FACE_REGISTRY_ZKEY_URL, faceZkeyName, faceZkeyHash)

            val binetAnalyzer = BinetAnalyzer()

            val address = identityManager.evmAddress()

            val preparedImage = binetAnalyzer.getPreparedInputForZKML(bitmap)!!

            val faceContract = rarimoContractManager.getFaceRegistry()

            val nonce = withContext(Dispatchers.IO) {
                faceContract.getVerificationNonce(BigInteger(address.drop(2), 16)).send().toString()
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
                address = BigInteger(1, Numeric.hexStringToByteArray(address)).toString(),
                threshold = "74088185856",
                nonce = nonce
            )

            val zkproof = zkp.bioent(
                zkeyFilePath = zkeyFile.absolutePath,
                zkeyFileLen = zkeyFile.length(),
                inputs = Gson().toJson(inputs)
            )

            Log.i(
                "Pub signals",
                GsonBuilder().setPrettyPrinting().create().toJson(zkproof.pub_signals)
            )


            val callDataBuilder = CallDataBuilder()
            val callData = callDataBuilder.buildGuessCelebrityClaimRewardCalldata(
                address, Gson().toJson(zkproof).toByteArray()
            )

            val response = apiManager.claimTokens("0x" + callData.toHexString())


            Log.i("tx_hash", response.data.attributes.tx_hash)

            val isSuccessful =
                rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)

            if (!isSuccessful) {
                throw Exception("cant send registration")
            }
        }
    }
}