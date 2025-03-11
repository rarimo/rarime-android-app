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


    val registerIdentity_3_160_3_3_336_200_NA: String
    val registerIdentity_3_160_3_4_576_216_1_1512_3_256: String
    val registerIdentity_2_256_3_6_336_264_1_2448_3_256: String


    val registerIdentity_21_256_3_4_576_232_NA: String
    val registerIdentity_11_256_3_3_576_240_1_864_5_264: String
    val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String


    val registerIdentity_11_256_3_3_336_248_NA: String
    val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String
    val registerIdentity_21_256_3_5_576_232_NA: String

    val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String
    val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String
    val registerIdentity_20_256_3_5_336_72_NA: String

    val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String
    val registerIdentity_15_512_3_3_336_248_NA: String
    val registerIdentity_20_160_3_3_736_200_NA: String


    val registerIdentityLight160: String
    val registerIdentityLight224: String
    val registerIdentityLight256: String
    val registerIdentityLight384: String
    val registerIdentityLight512: String

}

/* TESTNET */
object TestNetConfig : IConfig {
    override val RELAYER_URL = "https://api.orgs.app.stage.rarime.com"
    override val EVM_RPC_URL = "https://l2.rarimo.com"
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

    override val REGISTER_CONTRACT_ADDRESS = "0xd0cBA13E8B1cd45Fe6E80475ba61A15b1342c470"
    override val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0x52a254a983790B5Dc8c15C97F0211d85Bede601B"
    override val REGISTRATION_SMT_CONTRACT_ADDRESS = "0xA3cf09d3D1FD7b04769dCdfE09a7733c80186c29"
    override val STATE_KEEPER_CONTRACT_ADDRESS = "0xfb071F5f05caaB7bE2461A6ea661BC032716E192"
    override val REGISTRATION_SIMPLE_CONTRACT_ADRRESS = "0x5D7f2f6B720D0aEAd1cEf96c81dEE016FaDc5e08"
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

    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_11_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_12_256_3_3_336_232_NA =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_12_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_1_256_3_4_336_232_1_1480_5_296-download.zip"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_1_256_3_4_600_248_1_1496_3_256-download.zip"

    override val registerIdentity_1_160_3_4_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_1_160_3_4_576_200_NA-download.zip"
    override val registerIdentity_21_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_21_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_24_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_24_256_3_4_336_232_NA-download.zip"

    override val registerIdentity_20_256_3_3_336_224_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_20_256_3_3_336_224_NA-download.zip"
    override val registerIdentity_1_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_256_3_3_576_248_NA-download.zip"
    override val registerIdentity_1_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_160_3_3_576_200_NA-download.zip"
    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_10_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_11_256_3_5_576_248_1_1808_4_256-download.zip"
    override val registerIdentity_21_256_3_3_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_21_256_3_3_576_232_NA-download.zip"

    override val registerIdentity_3_160_3_3_336_200_NA =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.7-fix/registerIdentity_3_160_3_3_336_200_NA-download.zip"
    override val registerIdentity_3_160_3_4_576_216_1_1512_3_256 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.7-fix/registerIdentity_3_160_3_4_576_216_1_1512_3_256-download.zip"
    override val registerIdentity_2_256_3_6_336_264_1_2448_3_256 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.7-fix/registerIdentity_2_256_3_6_336_264_1_2448_3_256-download.zip"


    override val registerIdentity_21_256_3_4_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_21_256_3_4_576_232_NA-download.zip"
    override val registerIdentity_11_256_3_3_576_240_1_864_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_11_256_3_3_576_240_1_864_5_264-download.zip"

    override val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.9/registerIdentity_11_256_3_5_576_248_1_1808_5_296-download.zip"


    override val registerIdentity_11_256_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_11_256_3_3_336_248_NA-download.zip"
    override val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_14_256_3_4_336_64_1_1480_5_296-download.zip"
    override val registerIdentity_21_256_3_5_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_21_256_3_5_576_232_NA-download.zip"

    override val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_1_256_3_6_336_560_1_2744_4_256-download.zip"
    override val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_1_256_3_6_336_248_1_2744_4_256-download.zip"
    override val registerIdentity_20_256_3_5_336_72_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_20_256_3_5_336_72_NA-download.zip"

    override val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_4_160_3_3_336_216_1_1296_3_256-download.zip"
    override val registerIdentity_15_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_15_512_3_3_336_248_NA-download.zip"
    override val registerIdentity_20_160_3_3_736_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_20_160_3_3_736_200_NA-download.zip"



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
    override val EVM_RPC_URL = "https://l2.rarimo.com"
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

    override val REGISTER_CONTRACT_ADDRESS = "0x11BB4B14AA6e4b836580F3DBBa741dD89423B971"
    override val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"
    override val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0xA8b350d699632569D5351B20ffC1b31202AcEDD8"
    override val REGISTRATION_SMT_CONTRACT_ADDRESS = "0x479F84502Db545FA8d2275372E0582425204A879"
    override val STATE_KEEPER_CONTRACT_ADDRESS = "0x61aa5b68D811884dA4FEC2De4a7AA0464df166E1"
    override val REGISTRATION_SIMPLE_CONTRACT_ADRRESS = "0x497D6957729d3a39D43843BD27E6cbD12310F273"

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

    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_11_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_12_256_3_3_336_232_NA =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_12_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_1_256_3_4_336_232_1_1480_5_296-download.zip"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_1_256_3_4_600_248_1_1496_3_256-download.zip"

    override val registerIdentity_1_160_3_4_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_1_160_3_4_576_200_NA-download.zip"
    override val registerIdentity_21_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_21_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_24_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_24_256_3_4_336_232_NA-download.zip"

    override val registerIdentity_20_256_3_3_336_224_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_20_256_3_3_336_224_NA-download.zip"
    override val registerIdentity_1_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_256_3_3_576_248_NA-download.zip"

    override val registerIdentity_1_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.4/registerIdentity_1_160_3_3_576_200_NA-download.zip"
    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_10_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_11_256_3_5_576_248_1_1808_4_256-download.zip"
    override val registerIdentity_21_256_3_3_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_21_256_3_3_576_232_NA-download.zip"

    override val registerIdentity_3_160_3_3_336_200_NA =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.7-fix/registerIdentity_3_160_3_3_336_200_NA-download.zip"
    override val registerIdentity_3_160_3_4_576_216_1_1512_3_256 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.7-fix/registerIdentity_3_160_3_4_576_216_1_1512_3_256-download.zip"
    override val registerIdentity_2_256_3_6_336_264_1_2448_3_256 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.7-fix/registerIdentity_2_256_3_6_336_264_1_2448_3_256-download.zip"

    override val registerIdentity_21_256_3_4_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_21_256_3_4_576_232_NA-download.zip"
    override val registerIdentity_11_256_3_3_576_240_1_864_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_11_256_3_3_576_240_1_864_5_264-download.zip"


    override val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.9/registerIdentity_11_256_3_5_576_248_1_1808_5_296-download.zip"


    override val registerIdentity_11_256_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_11_256_3_3_336_248_NA-download.zip"
    override val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_14_256_3_4_336_64_1_1480_5_296-download.zip"
    override val registerIdentity_21_256_3_5_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_21_256_3_5_576_232_NA-download.zip"

    override val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_1_256_3_6_336_560_1_2744_4_256-download.zip"
    override val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_1_256_3_6_336_248_1_2744_4_256-download.zip"
    override val registerIdentity_20_256_3_5_336_72_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_20_256_3_5_336_72_NA-download.zip"

    override val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_4_160_3_3_336_216_1_1296_3_256-download.zip"
    override val registerIdentity_15_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_15_512_3_3_336_248_NA-download.zip"
    override val registerIdentity_20_160_3_3_736_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_20_160_3_3_736_200_NA-download.zip"



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
