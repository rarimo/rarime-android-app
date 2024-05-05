package com.distributedLab.rarime.data.manager

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.contracts.Registration
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.tx.gas.DefaultGasProvider
import javax.inject.Inject

class ContractManager @Inject constructor(private val web3j: Web3j) {
    fun getRegistration(): Registration {
        val ecKeyPair = Keys.createEcKeyPair()

        val credentials = Credentials.create(ecKeyPair)
        val gasProvider = DefaultGasProvider()

        return Registration.load(
            BaseConfig.REGISTER_CONTRACT_ADDRESS, web3j, credentials, gasProvider
        )

    }
}