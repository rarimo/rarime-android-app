package com.rarilabs.rarime.data

data class AppCurrency(
    val coinDenom: String,
    val coinMinimalDenom: String,
    val coinDecimals: Int,
    /**
     * This is used to fetch asset's fiat value from coingecko.
     * You can get id from https://api.coingecko.com/api/v3/coins/list.
     */
    val coinGeckoId: String? = null,
    val coinImageUrl: String? = null
)

data class Bech32Config(
    val bech32PrefixAccAddr: String,
    val bech32PrefixAccPub: String? = null,
    val bech32PrefixValAddr: String? = null,
    val bech32PrefixValPub: String? = null,
    val bech32PrefixConsAddr: String? = null,
    val bech32PrefixConsPub: String? = null
)

data class GasPriceStep(
    val low: Double,
    val average: Double,
    val high: Double
)

data class FeeCurrency(
    val coinDenom: String,
    val coinMinimalDenom: String,
    val coinDecimals: Int,
    /**
     * This is used to fetch asset's fiat value from coingecko.
     * You can get id from https://api.coingecko.com/api/v3/coins/list.
     */
    val coinGeckoId: String? = null,
    val coinImageUrl: String? = null,
    val gasPriceStep: GasPriceStep? = null
)

data class ChainInfo(
    val rpc: String,
    val rest: String,
    val chainId: String,
    val chainName: String,
    /**
     * This indicates the type of coin that can be used for stake.
     * You can get actual currency information from Currencies.
     */
    val stakeCurrency: AppCurrency? = null,
    val bip44: Bip44,
    val bech32Config: Bech32Config,
    val currencies: List<AppCurrency>,
    /**
     * This indicates which coin or token can be used for fee to send transaction.
     * You can get actual currency information from Currencies.
     */
    val feeCurrencies: List<FeeCurrency>,
    val chainSymbolImageUrl: String,
    val beta: Boolean? = null,
    /**
     * ZKP integration
     */
    val rpcEvm: String,
    val stateContractAddress: String
)

data class Bip44(
    val coinType: Int
)

data class ChainZkpInfo(
    val targetChainId: Int,
    val targetRpcUrl: String,
    val targetStateContractAddress: String,
    val coreChainId: String
)

enum class RarimoChains(val chainId: String) {
    Mainnet("rarimo_201411-1"),
    MainnetBeta("rarimo_42-1")
}
