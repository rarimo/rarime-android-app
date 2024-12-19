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
    val REGISTRATION_SIMPLE_CONTRACT_ADRRESS: String
    val POINTS_SVC_SELECTOR: String
    val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP: Long
    val FEEDBACK_EMAIL: String
    val CHAIN: RarimoChains
    val lightVerificationSKHex: String
    val GLOBAL_NOTIFICATION_TOPIC: String
    val REWARD_NOTIFICATION_TOPIC: String
    val GOOGLE_WEB_KEY: String
    val APP_ID_FIREBASE: String

    val registerIdentity_1_256_3_5_576_248_NA: String
    val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String
    val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String
    val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String

    val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String
    val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String
    val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String

    val registerIdentity_11_256_3_3_576_248_1_1184_5_264: String
    val registerIdentity_12_256_3_3_336_232_NA: String
    val registerIdentity_1_256_3_4_336_232_1_1480_5_296: String
    val registerIdentity_1_256_3_4_600_248_1_1496_3_256: String

    val registerIdentity_1_160_3_4_576_200_NA: String
    val registerIdentity_21_256_3_3_336_232_NA: String
    val registerIdentity_24_256_3_4_336_232_NA: String


    val registerIdentity_20_256_3_3_336_224_NA: String
    val registerIdentity_1_256_3_3_576_248_NA: String
    val registerIdentity_1_160_3_3_576_200_NA: String


    val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String
    val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String
    val registerIdentity_21_256_3_3_576_232_NA: String


    val registerIdentityLight160: String
    val registerIdentityLight224: String
    val registerIdentityLight256: String
    val registerIdentityLight384: String
    val registerIdentityLight512: String
}

/* TESTNET */
object TestNetConfig : IConfig {
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
    override val REGISTRATION_SIMPLE_CONTRACT_ADRRESS = "0xa2e2Cbbd0BAa74FE1bb4b61B12D420072806FFe0"
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

    override val registerIdentity_1_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_1_256_3_5_576_248_NA-download.zip"
    override val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_1_256_3_6_576_248_1_2432_5_296-download.zip"
    override val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_2_256_3_6_336_264_21_2448_6_2008-download.zip"
    override val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_21_256_3_7_336_264_21_3072_6_2008-download.zip"
    override val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.1/registerIdentity_1_256_3_6_576_264_1_2448_3_256-download.zip"
    override val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.1/registerIdentity_2_256_3_6_336_248_1_2432_3_256-download.zip"
    override val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.1/registerIdentity_2_256_3_6_576_248_1_2432_3_256-download.zip"

    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264 = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_11_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_12_256_3_3_336_232_NA = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_12_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296 = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_1_256_3_4_336_232_1_1480_5_296-download.zip"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256 = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_1_256_3_4_600_248_1_1496_3_256-download.zip"

    override val registerIdentity_1_160_3_4_576_200_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_1_160_3_4_576_200_NA-download.zip"
    override val registerIdentity_21_256_3_3_336_232_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_21_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_24_256_3_4_336_232_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_24_256_3_4_336_232_NA-download.zip"

    override val registerIdentity_20_256_3_3_336_224_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_20_256_3_3_336_224_NA-download.zip"
    override val registerIdentity_1_256_3_3_576_248_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_256_3_3_576_248_NA-download.zip"
    override val registerIdentity_1_160_3_3_576_200_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_160_3_3_576_200_NA-download.zip"
    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_10_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_11_256_3_5_576_248_1_1808_4_256-download.zip"
    override val registerIdentity_21_256_3_3_576_232_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_21_256_3_3_576_232_NA-download.zip"

    override val registerIdentityLight160: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight160-download.zip"
    override val registerIdentityLight224: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight224-download.zip"
    override val registerIdentityLight256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight256-download.zip"
    override val registerIdentityLight384: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight384-download.zip"
    override val registerIdentityLight512: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight512-download.zip"
}

// Mainnet
object MainnetConfig : IConfig {
    override val RELAYER_URL = "https://api.app.rarime.com"
    override val EVM_RPC_URL = "https://rpc.evm.mainnet.rarimo.com"
    override val COSMOS_RPC_URL = "https://rpc-api.mainnet.rarimo.com"
    override val EVM_SERVICE_URL =
        "https://api.orgs.app.rarime.com/integrations/evm-airdrop-svc-glo/"
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
    override val REGISTRATION_SIMPLE_CONTRACT_ADRRESS = "0xf1E98bCFdC29EbEccE46dd8cb599c5bd4A7ED75c"

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

    override val registerIdentity_1_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_1_256_3_5_576_248_NA-download.zip"
    override val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_1_256_3_6_576_248_1_2432_5_296-download.zip"
    override val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_2_256_3_6_336_264_21_2448_6_2008-download.zip"
    override val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_21_256_3_7_336_264_21_3072_6_2008-download.zip"
    override val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.1/registerIdentity_1_256_3_6_576_264_1_2448_3_256-download.zip"
    override val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.1/registerIdentity_2_256_3_6_336_248_1_2432_3_256-download.zip"
    override val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.1/registerIdentity_2_256_3_6_576_248_1_2432_3_256-download.zip"

    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264 = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_11_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_12_256_3_3_336_232_NA = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_12_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296 = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_1_256_3_4_336_232_1_1480_5_296-download.zip"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256 = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_1_256_3_4_600_248_1_1496_3_256-download.zip"

    override val registerIdentity_1_160_3_4_576_200_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_1_160_3_4_576_200_NA-download.zip"
    override val registerIdentity_21_256_3_3_336_232_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_21_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_24_256_3_4_336_232_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_24_256_3_4_336_232_NA-download.zip"

    override val registerIdentity_20_256_3_3_336_224_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_20_256_3_3_336_224_NA-download.zip"
    override val registerIdentity_1_256_3_3_576_248_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_256_3_3_576_248_NA-download.zip"

    override val registerIdentity_1_160_3_3_576_200_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_160_3_3_576_200_NA-download.zip"
    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_10_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_11_256_3_5_576_248_1_1808_4_256-download.zip"
    override val registerIdentity_21_256_3_3_576_232_NA: String = "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_21_256_3_3_576_232_NA-download.zip"

    override val registerIdentityLight160: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight160-download.zip"
    override val registerIdentityLight224: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight224-download.zip"
    override val registerIdentityLight256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight256-download.zip"
    override val registerIdentityLight384: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight384-download.zip"
    override val registerIdentityLight512: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.6-light/registerIdentityLight512-download.zip"
}
