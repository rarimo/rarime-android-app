package com.distributedLab.rarime.api.auth

import android.content.Context
import android.util.Log
import com.auth0.android.jwt.JWT
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeBody
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeData
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeDataAttributes
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.ZkpUtil
import com.distributedLab.rarime.util.data.ZkProof
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.Base64
import javax.inject.Inject

data class AuthProofInputs(
    val skIdentity: String = "12313821973981928319891283921839891237198298",
    val eventID: String = "1",
    val eventData: String = "0x967fd907a718896e38ce183e505623defd634d131e1a73769ab380f843a21c",
    val revealPkIdentityHash: Int = 0
)

class AuthManager @Inject constructor(
    private val context: Context,
    private val authAPIManager: AuthAPIManager,
    private val identityManager: IdentityManager,
    private val dataStoreManager: SecureSharedPrefsManager,
) {
    private var _accessToken = MutableStateFlow(dataStoreManager.readAccessToken())
    private var _refreshToken = MutableStateFlow(dataStoreManager.readRefreshToken())

    val accessToken: StateFlow<String?>
        get() = _accessToken.asStateFlow()

    val refreshToken: StateFlow<String?>
        get() = _refreshToken.asStateFlow()

    private fun getIsAuthorized(): Boolean {
        return _accessToken.value != null && isAccessTokenExpired() == true
    }

    private var _isAuthorized = MutableStateFlow(getIsAuthorized())

    val isAuthorized: StateFlow<Boolean>
        get() = _isAuthorized.asStateFlow()

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun getAuthQueryProof(nullifierHex: String): ZkProof {
        val challengeBody = authAPIManager.getChallenge(nullifierHex)

        val decodedChallenge = Base64.getDecoder().decode(challengeBody.data.attributes.challenge)

        val assetContext: Context = context.createPackageContext("com.distributedLab.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)

        val authProofInputs = AuthProofInputs(
            skIdentity = "0x" + identityManager.privateKey.value,
            eventID = BaseConfig.POINTS_SVC_ID,
            eventData = "0x" + decodedChallenge.toHexString(),
            revealPkIdentityHash = 0
        )

        val queryProof = withContext(Dispatchers.Default) {
            zkp.generateZKP(
                "circuit_auth_final.zkey",
                R.raw.auth,
                Gson().toJson(authProofInputs).toByteArray(),
                ZkpUtil::auth
            )
        }

        return queryProof
    }

    suspend fun login() {
        val nullifierHex = identityManager.getUserPointsNullifierHex()

        val queryProof = getAuthQueryProof(nullifierHex)

        val response = authAPIManager.authorize(
            RequestAuthorizeBody(
                data = RequestAuthorizeData(
                    id = nullifierHex,

                    attributes = RequestAuthorizeDataAttributes(
                        proof = queryProof.proof,
                    )
                )
            )
        )

        _accessToken.value = response.data.attributes.access_token.token
        _refreshToken.value = response.data.attributes.refresh_token.token

        _isAuthorized.value = getIsAuthorized()

        Log.i("AuthManager", "login: Access token: ${_accessToken.value}")
        Log.i("AuthManager", "login: Refresh token: ${_refreshToken.value}")
    }

    suspend fun refresh() {
        if (_accessToken.value == null) {
            throw IllegalStateException("Access token is not set")
        }

        val response = authAPIManager.refresh(
            authorization = _accessToken.value!!
        )

        _accessToken.value = response.data.attributes.access_token.token
        _refreshToken.value = response.data.attributes.refresh_token.token

        _isAuthorized.value = getIsAuthorized()
    }

    fun isAccessTokenExpired(): Boolean {
        val accessJWT = JWT(_accessToken.value!!)

        return accessJWT.expiresAt?.let {
            LocalDate.now().isAfter(
                it.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            )
        } ?: false
    }
}