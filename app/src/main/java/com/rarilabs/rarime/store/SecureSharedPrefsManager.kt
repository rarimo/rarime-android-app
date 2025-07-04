package com.rarilabs.rarime.store

import android.graphics.Bitmap
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.data.enums.AppLanguage
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.LikenessRule
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.data.UniversalProof

interface SecureSharedPrefsManager {

    fun savePrivateKey(privateKey: String)

    fun readPrivateKey(): String?

    fun readPasscodeState(): SecurityCheckState
    fun savePasscodeState(state: SecurityCheckState)

    fun readVisibleWidgets(): List<WidgetType>?
    fun saveVisibleWidgets(visibleCard: List<WidgetType>)

    fun readBiometricsState(): SecurityCheckState
    fun saveBiometricsState(state: SecurityCheckState)

    fun readPassportCardLook(): PassportCardLook
    fun savePassportCardLook(look: PassportCardLook)

    fun readIsPassportIncognitoMode(): Boolean
    fun saveIsPassportIncognitoMode(isIncognito: Boolean)

    fun readPassportIdentifiers(): List<PassportIdentifier>
    fun savePassportIdentifiers(identifiers: List<PassportIdentifier>)

    fun readColorScheme(): AppColorScheme
    fun saveColorScheme(scheme: AppColorScheme)

    fun readLanguage(): AppLanguage
    fun saveLanguage(language: AppLanguage)

    fun readWalletAssets(assetsToPopulate: List<WalletAsset>): List<WalletAsset>
    fun saveWalletAssets(walletAssets: List<WalletAsset>)

    fun readSelectedWalletAsset(walletAssets: List<WalletAsset>): WalletAsset

    fun saveSelectedWalletAsset(walletAsset: WalletAsset)

    fun saveEDocument(eDocument: EDocument)
    fun readEDocument(): EDocument?

    @Deprecated("use Universal proof")
    fun saveRegistrationProof(proof: GrothProof)

    @Deprecated("use Universal proof")
    fun readRegistrationProof(): GrothProof?

    fun readTransactions(): List<Transaction>
    fun addTransaction(transaction: Transaction)

    fun readPasscode(): String
    fun savePasscode(passcode: String)

    fun readLockTimestamp(): Long
    fun saveLockTimestamp(timestamp: Long)

    fun savePassportStatus(passportStatus: PassportStatus)
    fun readPassportStatus(): PassportStatus

    fun saveAccessToken(accessToken: String)
    fun readAccessToken(): String?

    fun saveRefreshToken(refreshToken: String)
    fun readRefreshToken(): String?
    fun clearAllData()

    fun readIsInWaitlist(): Boolean
    fun saveIsInWaitlist(isInWaitlist: Boolean)
    fun deletePassport()

    fun readIsLogsDeleted(): Boolean
    fun saveIsLogsDeleted(isLogsDeleted: Boolean)

    fun saveDeferredReferralCode(referralCode: String)
    fun getDeferredReferralCode(): String?

    fun saveGuessReferralCode(referralCode: String)
    fun getGuessReferralCode(): String?

    fun saveLightRegistrationData(lightRegistrationData: LightRegistrationData)
    fun getLightRegistrationData(): LightRegistrationData?


    fun saveIsAlreadyReserved(isAlreadyReserved: Boolean)
    fun getIsAlreadyReserved(): Boolean

    fun saveSelectedLikenessRule(likenessRule: LikenessRule)
    fun getSelectedLikenessRule(): LikenessRule?

    fun saveLikenessFace(face: Bitmap)
    fun getLikenessFace(): Bitmap?

    fun saveLivenessProof(proof: GrothProof)
    fun getLivenessProof(): GrothProof?


    fun saveUniversalProof(proof: UniversalProof)
    fun readUniversalProof(): UniversalProof?

    fun saveIsShownWelcome(isShown: Boolean)
    fun getIsShownWelcome(): Boolean

}