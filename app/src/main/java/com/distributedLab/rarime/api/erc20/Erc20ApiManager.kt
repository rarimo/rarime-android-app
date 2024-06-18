package com.distributedLab.rarime.api.erc20

import android.util.Log
import com.distributedLab.rarime.api.erc20.models.FeeResponse
import com.distributedLab.rarime.api.erc20.models.PermitHashRequest
import com.distributedLab.rarime.api.erc20.models.PermitHashResponse
import com.distributedLab.rarime.api.erc20.models.TransferErc20Request
import com.distributedLab.rarime.api.erc20.models.TransferErc20Response
import java.math.BigInteger
import javax.inject.Inject

class Erc20ApiManager @Inject constructor(
    private val erc20API: Erc20API
) {
    suspend fun getBalance(address: String): BigInteger? {
        val result = erc20API.getBalance(address)
        if (!result.isSuccessful) {
            return null
        }

        return result.body()?.data?.attributes?.amount?.let { BigInteger(it) }
    }

    suspend fun sendErc20Transfer(transferErc20Request: TransferErc20Request): TransferErc20Response? {
        val result = erc20API.transfer(transferErc20Request)
        if (!result.isSuccessful) {
            Log.i("transfer", result.errorBody()!!.string())
            return null
        }

        return result.body()
    }

    suspend fun permitHash(request: PermitHashRequest): PermitHashResponse? {
        val result = erc20API.permitHash(request)
        if (!result.isSuccessful) {
            return null
        }
        return result.body()
    }

    suspend fun getFee(transferErc20Request: TransferErc20Request): FeeResponse? {
        val result = erc20API.getFee(transferErc20Request)
        if (!result.isSuccessful) {
            return null
        }
        return result.body()
    }
}