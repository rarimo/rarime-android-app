package com.rarilabs.rarime

import com.rarilabs.rarime.config.Keys
import com.rarilabs.rarime.data.RarimoChains

val BaseConfig: IConfig = if (BuildConfig.isTestnet) TestNetConfig else MainnetConfig


interface IConfig {
    val APPSFLYER_DEV_KEY: String
    val RELAYER_URL: String
    val EVM_RPC_URL: String
    val COSMOS_RPC_URL: String
    val EVM_SERVICE_URL: String
    val DISCORD_URL: String
    val TWITTER_URL: String
    val INVITATION_BASE_URL: String
    val POINTS_SVC_ID: String
    val AIRDROP_SVC_ID: String
    val ICAO_COSMOS_RPC: String
    val MASTER_CERTIFICATES_FILENAME: String
    val MASTER_CERTIFICATES_BUCKETNAME: String
    val EVM_STABLE_COIN_RPC: String
    val STABLE_COIN_ADDRESS: String
    val REGISTER_CONTRACT_ADDRESS: String
    val CERTIFICATES_SMT_CONTRACT_ADDRESS: String
    val REGISTRATION_SMT_CONTRACT_ADDRESS: String
    val STATE_KEEPER_CONTRACT_ADDRESS: String
    val REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048: String
    val REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096: String
    val POINTS_SVC_SELECTOR: String
    val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP: Long
    val FEEDBACK_EMAIL: String
    val CHAIN: RarimoChains
    val lightVerificationSKHex: String
    val GLOBAL_NOTIFICATION_TOPIC: String
    val REWARD_NOTIFICATION_TOPIC: String
    val GOOGLE_WEB_KEY: String
    val APP_ID_FIREBASE: String
}

/* TESTNET */
object TestNetConfig: IConfig {
    override val RELAYER_URL = "https://api.orgs.app.stage.rarime.com"
    override val EVM_RPC_URL = "https://rpc.evm.node1.mainnet-beta.rarimo.com"
    override val COSMOS_RPC_URL = "https://rpc-api.node1.mainnet-beta.rarimo.com"
    override val EVM_SERVICE_URL =
        "https://api.orgs.app.stage.rarime.com/integrations/evm-airdrop-svc-glo/"
    override val DISCORD_URL = "https://discord.gg/Bzjm5MDXrU"
    override val TWITTER_URL = "https://x.com/Rarimo_protocol"

    override val INVITATION_BASE_URL = "https://app.stage.rarime.com"

    override val POINTS_SVC_ID = "0x77fabbc6cb41a11d4fb6918696b3550d5d602f252436dd587f9065b7c4e62b"
    override val AIRDROP_SVC_ID = "0xac42d1a986804618c7a793fbe814d9b31e47be51e082806363dca6958f3062"

    override val ICAO_COSMOS_RPC = "core-api.node1.mainnet-beta.rarimo.com:443"
    override val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
    override val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"
    override val EVM_STABLE_COIN_RPC = "https://ethereum-sepolia-rpc.publicnode.com"
    override val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"

    override val REGISTER_CONTRACT_ADDRESS = "0x1b0F076c8800c457CE734BA85aC8569284DD640A"
    override val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0xb393B0F444fC849bC61E3285C6c38b1052520007"
    override val REGISTRATION_SMT_CONTRACT_ADDRESS = "0xc1534912902BBe8C54626e2D69288C76a843bc0E"
    override val STATE_KEEPER_CONTRACT_ADDRESS = "0x0Ca8340633e0D728db012D59bD543fD80dDabFC9"
    override val REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.1.0-alpha/registerIdentityUniversalRSA2048-download.zip"
    override val REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.1.0-alpha/registerIdentityUniversalRSA4096-download.zip"
    override val POINTS_SVC_SELECTOR = "23073"
    override val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP = 1715688000L

    override val FEEDBACK_EMAIL = "apereliez1@gmail.com"
    override val CHAIN = RarimoChains.MainnetBeta
    override val lightVerificationSKHex = Keys.lightVerificationSKHex
    override val GOOGLE_WEB_KEY = Keys.GOOGLE_WEB_KEY
    override val APP_ID_FIREBASE = Keys.APP_ID


    override val GLOBAL_NOTIFICATION_TOPIC = "rarime-stage"
    override val REWARD_NOTIFICATION_TOPIC: String = "rarime-rewardable-stage"
    override val APPSFLYER_DEV_KEY = Keys.APPSFLYER_DEV_KEY
}

// Mainnet
object MainnetConfig: IConfig {
    override val RELAYER_URL = "https://api.app.rarime.com"
    override val EVM_RPC_URL = "https://rpc.evm.mainnet.rarimo.com"
    override val COSMOS_RPC_URL = "https://rpc-api.mainnet.rarimo.com"
    override val EVM_SERVICE_URL = "https://api.orgs.app.rarime.com/integrations/evm-airdrop-svc-glo/"
    override val DISCORD_URL = "https://discord.gg/Bzjm5MDXrU"
    override val TWITTER_URL = "https://x.com/Rarimo_protocol"

    override val INVITATION_BASE_URL = "https://app.rarime.com"

    override val POINTS_SVC_ID = "0x77fabbc6cb41a11d4fb6918696b3550d5d602f252436dd587f9065b7c4e62b"
    override val AIRDROP_SVC_ID = "0xac42d1a986804618c7a793fbe814d9b31e47be51e082806363dca6958f3062"

    override val ICAO_COSMOS_RPC = "core-api.mainnet.rarimo.com:443"
    override val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
    override val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"
    override val EVM_STABLE_COIN_RPC = "https://ethereum-sepolia-rpc.publicnode.com"

    override val REGISTER_CONTRACT_ADDRESS = "0xC0B09085Fa2ad3A8BbF96494B8d5cd10702FE20d"
    override val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"
    override val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0xcc54b12a18f2C575CA97991046090f43C3070aA0"
    override val REGISTRATION_SMT_CONTRACT_ADDRESS = "0xA25a197d26Cad659A8fFf7F268cA4F9e0283de03"
    override val STATE_KEEPER_CONTRACT_ADDRESS = "0x7d4E8Da1d10f8Db46C52414175d4003ab0Aef506"

    override val REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.1.0-alpha/registerIdentityUniversalRSA2048-download.zip"
    override val REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.1.0-alpha/registerIdentityUniversalRSA4096-download.zip"
    override val POINTS_SVC_SELECTOR = "23073"
    override val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP = 1715688000L
    override val FEEDBACK_EMAIL = "info@rarilabs.com"
    override val CHAIN = RarimoChains.Mainnet
    override val lightVerificationSKHex = Keys.lightVerificationSKHex
    override val GOOGLE_WEB_KEY = Keys.GOOGLE_WEB_KEY
    override val APP_ID_FIREBASE = Keys.APP_ID

    override val GLOBAL_NOTIFICATION_TOPIC = "rarime"
    override val REWARD_NOTIFICATION_TOPIC: String = "rarime-rewardable"
    override val APPSFLYER_DEV_KEY = Keys.APPSFLYER_DEV_KEY

}