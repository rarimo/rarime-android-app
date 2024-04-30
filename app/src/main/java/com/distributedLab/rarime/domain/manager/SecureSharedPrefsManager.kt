package com.distributedLab.rarime.domain.manager

import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.SecurityCheckState

interface SecureSharedPrefsManager {

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

    fun readColorScheme(): AppColorScheme
    fun saveColorScheme(scheme: AppColorScheme)

    fun readLanguage(): AppLanguage
    fun saveLanguage(language: AppLanguage)

    fun readWalletBalance(): Double
    fun saveWalletBalance(balance: Double)

    fun readPasscode(): String
    fun savePasscode(passcode: String)

    fun readLockTimestamp(): Long
    fun saveLockTimestamp(timestamp: Long)

}