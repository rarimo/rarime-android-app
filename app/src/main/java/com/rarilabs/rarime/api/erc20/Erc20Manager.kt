package com.rarilabs.rarime.api.erc20


import com.rarilabs.rarime.api.erc20.models.PermitHashRequest
import com.rarilabs.rarime.api.erc20.models.TransferErc20Request
import javax.inject.Inject

class Erc20Manager @Inject constructor(
    private val erc20ApiManager: Erc20ApiManager
) {
    suspend fun getBalance(address: String) = erc20ApiManager.getBalance(address)

    suspend fun sendErc20Transfer(transferErc20Request: TransferErc20Request) =
        erc20ApiManager.sendErc20Transfer(transferErc20Request)

    suspend fun permitHash(request: PermitHashRequest) = erc20ApiManager.permitHash(request)

    suspend fun getFee(request: TransferErc20Request) = erc20ApiManager.getFee(request)
}