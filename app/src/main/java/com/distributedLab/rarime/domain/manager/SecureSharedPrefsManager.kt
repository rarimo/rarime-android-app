package com.distributedLab.rarime.domain.manager

import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.SecurityCheckState

interface SecureSharedPrefsManager {

    fun readIsIntroFinished(): Boolean
    suspend fun saveIsIntroFinished(isFinished: Boolean)

    fun readPasscodeState(): SecurityCheckState
    suspend fun savePasscodeState(state: SecurityCheckState)

    fun readBiometricsState(): SecurityCheckState
    suspend fun saveBiometricsState(state: SecurityCheckState)

    fun readPassportCardLook(): PassportCardLook
    suspend fun savePassportCardLook(look: PassportCardLook)

    fun readIsPassportIncognitoMode(): Boolean
    suspend fun saveIsPassportIncognitoMode(isIncognito: Boolean)

    fun readColorScheme(): AppColorScheme
    suspend fun saveColorScheme(scheme: AppColorScheme)

    fun readLanguage(): AppLanguage
    suspend fun saveLanguage(language: AppLanguage)

    fun readWalletBalance(): Double
    suspend fun saveWalletBalance(balance: Double)

}