package com.rarilabs.rarime.store

import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.data.enums.AppLanguage
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.util.data.ZkProof

interface SecureSharedPrefsManager {

    fun savePrivateKey(privateKey: String)

    fun readPrivateKey(): String?

    fun readIsIntroFinished(): Boolean
    fun saveIsIntroFinished(isFinished: Boolean)

    fun readPasscodeState(): SecurityCheckState
    fun savePasscodeState(state: SecurityCheckState)

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
    fun saveRegistrationProof(proof: ZkProof)

    fun readRegistrationProof(): ZkProof?

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
}