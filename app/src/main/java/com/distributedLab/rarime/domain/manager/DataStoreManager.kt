package com.distributedLab.rarime.domain.manager

import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.SecurityCheckState
import kotlinx.coroutines.flow.Flow

interface DataStoreManager {

    fun readIsIntroFinished(): Flow<Boolean>
    suspend fun saveIsIntroFinished(isFinished: Boolean)

    fun readPasscodeState(): Flow<SecurityCheckState>
    suspend fun savePasscodeState(state: SecurityCheckState)

    fun readBiometricsState(): Flow<SecurityCheckState>
    suspend fun saveBiometricsState(state: SecurityCheckState)

    fun readPassportCardLook(): Flow<PassportCardLook>
    suspend fun savePassportCardLook(look: PassportCardLook)

    fun readIsPassportIncognitoMode(): Flow<Boolean>
    suspend fun saveIsPassportIncognitoMode(isIncognito: Boolean)

    fun readColorScheme(): Flow<AppColorScheme>
    suspend fun saveColorScheme(scheme: AppColorScheme)

    fun readLanguage(): Flow<AppLanguage>
    suspend fun saveLanguage(language: AppLanguage)

    fun readWalletBalance(): Flow<Double>
    suspend fun saveWalletBalance(balance: Double)

}