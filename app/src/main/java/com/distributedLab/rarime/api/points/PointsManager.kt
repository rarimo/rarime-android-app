package com.distributedLab.rarime.api.points

import com.distributedLab.rarime.manager.IdentityManager
import javax.inject.Inject

class PointsManager @Inject constructor(
    private val pointsAPIManager: PointsAPIManager,
    private val identityManager: IdentityManager
) {
    suspend fun verifyPassport() = pointsAPIManager.verifyPassport()
}