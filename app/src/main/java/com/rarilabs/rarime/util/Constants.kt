package com.rarilabs.rarime.util

import com.rarilabs.rarime.data.AppCurrency
import com.rarilabs.rarime.data.Bech32Config
import com.rarilabs.rarime.data.Bip44
import com.rarilabs.rarime.data.ChainInfo
import com.rarilabs.rarime.data.FeeCurrency
import com.rarilabs.rarime.data.GasPriceStep
import com.rarilabs.rarime.data.RarimoChains
import kotlin.time.Duration.Companion.minutes

object Constants {
    const val TERMS_URL = "https://rarime.com/general-terms.html"
    const val PRIVACY_URL = "https://rarime.com/privacy-notice.html"
    const val AIRDROP_TERMS_URL = "https://rarime.com/airdrop-terms.html"

    const val MAX_PASSCODE_ATTEMPTS = 5
    val PASSCODE_LOCK_PERIOD = 5.minutes


    const val SCAN_PASSPORT_REWARD = 10.0
    const val AIRDROP_REWARD = 10.0
    const val MAX_PASSPORT_IDENTIFIERS = 2

    val NOT_ALLOWED_COUNTRIES = listOf(
        "RUS",
        "USA",
        "CAN",
        "BLR",
        "CHN",
        "HKG",
        "MAC",
        "TWN",
        "PRK",
        "IRN",
        "CUB",
        "COG",
        "COD",
        "LBY",
        "SOM",
        "SSD",
        "SDN",
        "SYR",
        "YEM"
    )

    val RARIMO_CHAINS: Map<String, ChainInfo> = mapOf(
        RarimoChains.MainnetBeta.chainId to ChainInfo(
            chainId = "rarimo_42-1",
            chainName = "Rarimo Testnet",
            chainSymbolImageUrl = "https://raw.githubusercontent.com/rarimo/js-sdk/2.0.0-rc.14/assets/logos/ra-dark-logo.png",
            rpc = "core-api.node1.mainnet-beta.rarimo.com:443", // "https://rpc.node1.mainnet-beta.rarimo.com",
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
            rpc = "core-api.mainnet.rarimo.com:443",
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
                        average = 0.0,
                        high = 0.0
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
}