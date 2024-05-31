package com.distributedLab.rarime

import com.distributedLab.rarime.data.tokens.Erc20Token
import com.distributedLab.rarime.data.tokens.RarimoToken
import com.distributedLab.rarime.modules.common.WalletAsset

object BaseConfig {
    const val RELAYER_URL = "https://api.orgs.app.stage.rarime.com"
    const val EVM_RPC_URL = "https://rpc.evm.node1.mainnet-beta.rarimo.com"
    const val REGISTER_CONTRACT_ADDRESS = "0x31C9baC74fa7482cDCf33d09B8476250b460B058"
    const val COSMOS_RPC_URL = "https://rpc-api.node1.mainnet-beta.rarimo.com"

    const val CHAIN_ID = "rarimo_42-1"
    const val DENOM = "stake"
    const val RPC_IP = "104.196.227.66:9090"

    const val ICAO_COSMOS_RPC = "core-api.node1.mainnet-beta.rarimo.com:443"
    const val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
    const val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"

    val DEFAULT_WALLET_ASSETS = listOf(
        WalletAsset("", RarimoToken()),
        WalletAsset("", Erc20Token("0x0000000000000000000000000000000000000000"))
    )
}