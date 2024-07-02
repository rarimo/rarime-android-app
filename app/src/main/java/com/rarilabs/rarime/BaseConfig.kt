package com.rarilabs.rarime

import com.rarilabs.rarime.data.RarimoChains

/* TESTNET */
object BaseConfig {
    const val RELAYER_URL = "https://api.orgs.app.stage.rarime.com"
    const val EVM_RPC_URL = "https://rpc.evm.node1.mainnet-beta.rarimo.com"
    const val COSMOS_RPC_URL = "https://rpc-api.node1.mainnet-beta.rarimo.com"
    const val EVM_SERVICE_URL = "https://api.orgs.app.stage.rarime.com/integrations/evm-airdrop-svc-glo"
    const val DISCORD_URL = "https://discord.gg/Bzjm5MDXrU"
    const val TWITTER_URL = "https://x.com/Rarimo_protocol"

    const val INVITATION_BASE_URL = "https://app.stage.rarime.com"

    const val POINTS_SVC_ID = "0x77fabbc6cb41a11d4fb6918696b3550d5d602f252436dd587f9065b7c4e62b"
    const val AIRDROP_SVC_ID = "0xac42d1a986804618c7a793fbe814d9b31e47be51e082806363dca6958f3062"

    const val ICAO_COSMOS_RPC = "core-api.node1.mainnet-beta.rarimo.com:443"
    const val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
    const val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"
    const val EVM_STABLE_COIN_RPC = "https://ethereum-sepolia-rpc.publicnode.com"
    const val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"

    const val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0x4EECcb639990917fA43f7cbf23b5350e419DC129"
    const val REGISTRATION_SMT_CONTRACT_ADDRESS = "0x6FD2568A0a3692E2B0d6d4258845E57bcAc53Db2"
    const val STATE_KEEPER_CONTRACT_ADDRESS = "0x3CfB92B692045C73888904e6e81F9B78fcFC6FE4"
    const val REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048 = "https://storage.googleapis.com/rarimo-store/rdi2/registerIdentityUniversalRSA2048-download.zip"
    const val REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096 = "https://storage.googleapis.com/rarimo-store/rdi2/registerIdentityUniversalRSA4096-download.zip"
    const val POINTS_SVC_SELECTOR = "23073"
    const val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP = 1715688000L

    const val FEEDBACK_EMAIL = "apereliez1@gmail.com"
    val CHAIN = RarimoChains.MainnetBeta
}

/* MAINNET */
//object BaseConfig {
//    const val RELAYER_URL = "https://api.app.rarime.com"
//    const val EVM_RPC_URL = "https://rpc.evm.mainnet.rarimo.com"
//    const val COSMOS_RPC_URL = "https://rpc-api.mainnet.rarimo.com"
//    const val EVM_SERVICE_URL = "https://api.orgs.app.rarime.com/integrations/evm-airdrop-svc-glo"
//    const val DISCORD_URL = "https://discord.gg/Bzjm5MDXrU"
//    const val TWITTER_URL = "https://x.com/Rarimo_protocol"
//
//    const val INVITATION_BASE_URL = "https://app.rarime.com"
//
//    const val POINTS_SVC_ID = "0x77fabbc6cb41a11d4fb6918696b3550d5d602f252436dd587f9065b7c4e62b"
//    const val AIRDROP_SVC_ID = "0xac42d1a986804618c7a793fbe814d9b31e47be51e082806363dca6958f3062"
//
//    const val ICAO_COSMOS_RPC = "core-api.mainnet.rarimo.com:443"
//    const val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
//    const val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"
//    const val EVM_STABLE_COIN_RPC = "https://ethereum-sepolia-rpc.publicnode.com"
//    const val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"
//    const val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0xcc54b12a18f2C575CA97991046090f43C3070aA0"
//    const val REGISTRATION_SMT_CONTRACT_ADDRESS = "0xA25a197d26Cad659A8fFf7F268cA4F9e0283de03"
//
//    const val STATE_KEEPER_CONTRACT_ADDRESS = "0x7d4E8Da1d10f8Db46C52414175d4003ab0Aef506"
//    const val REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048 = "https://storage.googleapis.com/rarimo-store/rdi2/registerIdentityUniversalRSA2048-download.zip"
//    const val REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096 = "https://storage.googleapis.com/rarimo-store/rdi2/registerIdentityUniversalRSA4096-download.zip"
//    const val POINTS_SVC_SELECTOR = "23073"
//    const val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP = 1715688000L
//
//    const val FEEDBACK_EMAIL = "apereliez1@gmail.com"
//    val CHAIN = RarimoChains.Mainnet
//}