package com.distributedLab.rarime

import com.distributedLab.rarime.data.AppCurrency
import com.distributedLab.rarime.data.Bech32Config
import com.distributedLab.rarime.data.Bip44
import com.distributedLab.rarime.data.ChainInfo
import com.distributedLab.rarime.data.FeeCurrency
import com.distributedLab.rarime.data.GasPriceStep
import com.distributedLab.rarime.data.RarimoChains

object BaseConfig {
    const val RELAYER_URL = "https://api.orgs.app.stage.rarime.com"
    const val EVM_RPC_URL = "https://rpc.evm.node1.mainnet-beta.rarimo.com"
    const val REGISTER_CONTRACT_ADDRESS = "0x31C9baC74fa7482cDCf33d09B8476250b460B058"
    const val COSMOS_RPC_URL = "https://rpc-api.node1.mainnet-beta.rarimo.com"

    val RARIMO_CHAINS: Map<String, ChainInfo> = mapOf(
        RarimoChains.MainnetBeta.chainId to ChainInfo(
            chainId = "rarimo_42-1",
            chainName = "Rarimo Testnet",
            chainSymbolImageUrl = "https://raw.githubusercontent.com/rarimo/js-sdk/2.0.0-rc.14/assets/logos/ra-dark-logo.png",
            rpc = "104.196.227.66:9090", // "https://rpc.node1.mainnet-beta.rarimo.com",
            rest = "https://rpc-api.node1.mainnet-beta.rarimo.com",
            stakeCurrency = AppCurrency(
                coinDenom = "STAKE",
                coinMinimalDenom = "stake",
                coinDecimals = 6
            ),
            currencies = listOf(
                AppCurrency(
                    coinDenom = "STAKE",
                    coinMinimalDenom = "stake",
                    coinDecimals = 6
                )
            ),
            feeCurrencies = listOf(
                FeeCurrency(
                    coinDenom = "STAKE",
                    coinMinimalDenom = "stake",
                    coinDecimals = 6,
                    gasPriceStep = GasPriceStep(
                        low = 0.0,
                        average = 0.1,
                        high = 0.5
                    )
                )
            ),
            bip44 = Bip44(
                coinType = 118
            ),
            bech32Config = Bech32Config(
                bech32PrefixAccAddr = "rarimo",
                bech32PrefixAccPub = "rarimopub",
                bech32PrefixValAddr = "rarimovaloper",
                bech32PrefixValPub = "rarimovaloperpub",
                bech32PrefixConsAddr = "rarimovalcons",
                bech32PrefixConsPub = "rarimovalconspub"
            ),
            beta = true,
            rpcEvm = "https://rpc.evm.node1.mainnet-beta.rarimo.com",
            stateContractAddress = "0x753a8678c85d5fb70A97CFaE37c84CE2fD67EDE8"
        ),
        RarimoChains.Mainnet.chainId to ChainInfo(
            chainId = "rarimo_201411-1",
            chainName = "Rarimo",
            chainSymbolImageUrl = "https://raw.githubusercontent.com/rarimo/js-sdk/2.0.0-rc.14/assets/logos/ra-dark-logo.png",
            rpc = "https://rpc.mainnet.rarimo.com",
            rest = "https://rpc-api.mainnet.rarimo.com",
            stakeCurrency = AppCurrency(
                coinDenom = "RMO",
                coinMinimalDenom = "urmo",
                coinDecimals = 6
            ),
            currencies = listOf(
                AppCurrency(
                    coinDenom = "RMO",
                    coinMinimalDenom = "urmo",
                    coinDecimals = 6
                )
            ),
            feeCurrencies = listOf(
                FeeCurrency(
                    coinDenom = "RMO",
                    coinMinimalDenom = "urmo",
                    coinDecimals = 6,
                    gasPriceStep = GasPriceStep(
                        low = 0.0,
                        average = 0.1,
                        high = 0.5
                    )
                )
            ),
            bip44 = Bip44(
                coinType = 118
            ),
            bech32Config = Bech32Config(
                bech32PrefixAccAddr = "rarimo",
                bech32PrefixAccPub = "rarimopub",
                bech32PrefixValAddr = "rarimovaloper",
                bech32PrefixValPub = "rarimovaloperpub",
                bech32PrefixConsAddr = "rarimovalcons",
                bech32PrefixConsPub = "rarimovalconspub"
            ),
            beta = false,
            rpcEvm = "https://rpc.evm.mainnet.rarimo.com",
            stateContractAddress = "0x5ac96945a771d417B155Cb07A3D7E4b8e2F33FdE"
        )
    )

    const val ICAO_COSMOS_RPC = "core-api.node1.mainnet-beta.rarimo.com:443"
    const val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
    const val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"
}