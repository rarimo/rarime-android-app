package com.rarilabs.rarime

import com.rarilabs.rarime.data.AppCurrency
import com.rarilabs.rarime.data.Bech32Config
import com.rarilabs.rarime.data.Bip44
import com.rarilabs.rarime.data.ChainInfo
import com.rarilabs.rarime.data.FeeCurrency
import com.rarilabs.rarime.data.GasPriceStep
import com.rarilabs.rarime.data.RarimoChains

object BaseConfig {
    const val RELAYER_URL = "https://api.orgs.app.stage.rarime.com"
    const val EVM_RPC_URL = "https://rpc.evm.node1.mainnet-beta.rarimo.com"
    const val REGISTER_CONTRACT_ADDRESS = "0x6FD2568A0a3692E2B0d6d4258845E57bcAc53Db2"
    const val COSMOS_RPC_URL = "https://rpc-api.node1.mainnet-beta.rarimo.com"
    const val EVM_SERVICE_URL = "https://api.orgs.app.stage.rarime.com/integrations/evm-airdrop-svc-glo"
    const val DISCORD_URL = "https://discord.gg/Bzjm5MDXrU"
    const val TWITTER_URL = "https://x.com/Rarimo_protocol"

    const val POINTS_SVC_ID = "0x77fabbc6cb41a11d4fb6918696b3550d5d602f252436dd587f9065b7c4e62b"
    const val AIRDROP_SVC_ID = "0xac42d1a986804618c7a793fbe814d9b31e47be51e082806363dca6958f3062"

    const val INVITATION_BASE_URL = "https://app.rarime.com/r"

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
    const val EVM_STABLE_COIN_RPC = "https://ethereum-sepolia-rpc.publicnode.com"
    const val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"

    const val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0x4EECcb639990917fA43f7cbf23b5350e419DC129"

    const val STATE_KEEPER_CONTRACT_ADDRESS = "0x3CfB92B692045C73888904e6e81F9B78fcFC6FE4"
    const val REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048 = "https://storage.googleapis.com/rarimo-store/rdi2/registerIdentityUniversalRSA2048-download.zip"
    const val REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096 = "https://storage.googleapis.com/rarimo-store/rdi2/registerIdentityUniversalRSA4096-download.zip"
}