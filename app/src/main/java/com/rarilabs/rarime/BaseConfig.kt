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
    val EXPLORER_API_URL: String
    val VOTING_RELAYER_URL: String
    val RARIMO_EXPLORER: String
    val VOTING_REGISTRATION_SMT_CONTRACT_ADDRESS: String

    val VOTING_RPC_URL: String

    val PROPOSAL_CONTRACT_ADDRESS: String

    val MULTICALL_CONTRACT_ADDRRESS: String

    val VOTING_WEBSITE_URL: String

    val FACE_REGISTRY_ADDRESS: String

    val FACE_REGISTRY_ZKEY_URL: String


    val FACE_RECOGNITION_MODEL_URL: String

    val GUESS_CELEBRITY_CONTRACT_ADDRESS: String

    val NOIR_TRUSTED_SETUP_URL: String


    val registerIdentity_1_256_3_5_576_248_NA: String
    val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String
    val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String

    val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String
    val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String
    val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String

    val registerIdentity_11_256_3_3_576_248_1_1184_5_264: String
    val registerIdentity_12_256_3_3_336_232_NA: String
    val registerIdentity_1_256_3_4_600_248_1_1496_3_256: String

    val registerIdentity_1_160_3_4_576_200_NA: String
    val registerIdentity_21_256_3_3_336_232_NA: String
    val registerIdentity_24_256_3_4_336_232_NA: String


    val registerIdentity_20_256_3_3_336_224_NA: String


    val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String
    val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String
    val registerIdentity_21_256_3_3_576_232_NA: String



    val registerIdentity_21_256_3_4_576_232_NA: String
    val registerIdentity_11_256_3_3_576_240_1_864_5_264: String
    val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String


    val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String
    val registerIdentity_21_256_3_5_576_232_NA: String

    val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String
    val registerIdentity_20_256_3_5_336_72_NA: String

    val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String
    val registerIdentity_15_512_3_3_336_248_NA: String
    val registerIdentity_20_160_3_3_736_200_NA: String

    val registerIdentityLight160: String
    val registerIdentityLight224: String
    val registerIdentityLight256: String
    val registerIdentityLight384: String
    val registerIdentityLight512: String

    val registerIdentity_11_256_3_4_336_232_1_1480_4_256: String
    val registerIdentity_11_256_3_3_576_248_NA: String
    val registerIdentity_11_256_3_5_576_248_NA: String
    val registerIdentity_14_256_3_3_576_240_NA: String
    val registerIdentity_14_256_3_4_336_232_1_1480_5_296: String
    val registerIdentity_14_256_3_4_576_248_1_1496_3_256: String
    val registerIdentity_1_256_3_4_576_232_1_1480_3_256: String
    val registerIdentity_1_256_3_5_336_248_1_2120_4_256: String
    val registerIdentity_2_256_3_4_336_232_1_1480_4_256: String
    val registerIdentity_2_256_3_4_336_248_NA: String
    val registerIdentity_20_160_3_2_576_184_NA: String
    val registerIdentity_20_160_3_3_576_200_NA: String
    val registerIdentity_20_256_3_5_336_248_NA: String
    val registerIdentity_23_160_3_3_576_200_NA: String
    val registerIdentity_24_256_3_4_336_248_NA: String
    val registerIdentity_3_256_3_4_600_248_1_1496_3_256: String
    val registerIdentity_6_160_3_3_336_216_1_1080_3_256: String
    val registerIdentity_3_512_3_3_336_264_NA: String


    val registerIdentity_11_256_3_5_584_264_1_2136_4_256: String
    val registerIdentity_11_256_3_5_576_264_NA: String
    val registerIdentity_2_256_3_4_336_248_22_1496_7_2408: String
    val registerIdentity_1_256_3_4_336_232_NA: String

    val registerIdentity_25_384_3_3_336_232_NA: String
    val registerIdentity_25_384_3_4_336_264_1_2904_2_256: String
    val registerIdentity_26_512_3_3_336_248_NA: String

    val registerIdentity_26_512_3_3_336_264_1_1968_2_256: String
    val registerIdentity_27_512_3_4_336_248_NA: String

    val registerIdentity_1_256_3_5_336_248_1_2120_3_256: String
    val registerIdentity_7_160_3_3_336_216_1_1080_3_256: String

    val registerIdentity_8_160_3_3_336_216_1_1080_3_256: String

    val registerIdentity_3_256_3_3_576_248_NA: String

    val registerIdentity_25_384_3_3_336_264_1_2024_3_296: String

    val registerIdentity_28_384_3_3_576_264_24_2024_4_2792: String
    val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String
    val registerIdentity_25_384_3_3_336_248_NA: String

    val registerIdentity_1_160_3_3_576_200_NA: String
    val registerIdentity_1_256_3_3_576_248_NA: String
    val registerIdentity_1_256_3_4_336_232_1_1480_5_296: String
    val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String
    val registerIdentity_2_256_3_6_336_264_1_2448_3_256: String
    val registerIdentity_3_160_3_3_336_200_NA: String

    val registerIdentity_3_160_3_4_576_216_1_1512_3_256: String

    val registerIdentity_11_256_3_2_336_216_NA: String

    val registerIdentity_11_256_3_3_336_248_NA: String
}

/* TESTNET */
object TestNetConfig : IConfig {
    override val RELAYER_URL = "https://api.orgs.app.stage.rarime.com"
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
    override val EXPLORER_API_URL = "https://api.evmscan.rarimo.com"
    override val RARIMO_EXPLORER = "https://api.evmscan.rarimo.com/tx"
    override val EVM_RPC_URL = "https://rpc.evm.mainnet.rarimo.com"
    override val REGISTER_CONTRACT_ADDRESS = "0x435E8833bC8c6F5Fdfc1cd7E45D5760b523f4020"
    override val REGISTRATION_SIMPLE_CONTRACT_ADRRESS = "0xd63782478CA40b587785700Ce49248775398b045"
    override val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0xc2974679359c756bf97ff6B698377E02c083F3D4"
    override val REGISTRATION_SMT_CONTRACT_ADDRESS = "0xF19a85B10d705Ed3bAF3c0eCe3E73d8077Bf6481"
    override val STATE_KEEPER_CONTRACT_ADDRESS = "0x9EDADB216C1971cf0343b8C687cF76E7102584DB"
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

    override val VOTING_RELAYER_URL: String = "https://api.stage.freedomtool.org"
    override val VOTING_REGISTRATION_SMT_CONTRACT_ADDRESS: String =
        "0xFbae44a113A6f07687b180605f425e43066a6179"
    override val VOTING_RPC_URL: String = "https://rpc.qtestnet.org"

    override val PROPOSAL_CONTRACT_ADDRESS: String = "0x4C61d7454653720DAb9e26Ca25dc7B8a5cf7065b"
    override val MULTICALL_CONTRACT_ADDRRESS: String = "0xcA11bde05977b3631167028862bE2a173976CA11"

    override val VOTING_WEBSITE_URL: String = "https://freedomtool.org"

    override val NOIR_TRUSTED_SETUP_URL: String =
        "https://storage.googleapis.com/rarimo-store/trusted-setups/ultraPlonkTrustedSetup.dat"


    override val FACE_REGISTRY_ADDRESS: String = "0x3C0f27AC1817820C1BA41337B53090652aE4F448"

    override val GUESS_CELEBRITY_CONTRACT_ADDRESS: String =
        "0x411AA3eF21AdC9e84c60e17451B0732119C8f0c7"


    override val FACE_REGISTRY_ZKEY_URL: String =
        "https://storage.googleapis.com/rarimo-store/zkey/circuit_final.zkey"
    override val FACE_RECOGNITION_MODEL_URL: String =
        "https://storage.googleapis.com/rarimo-store/face-recognition/face-recognition.tflite"

    override val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_21_256_3_7_336_264_21_3072_6_2008-download.zip"

    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_11_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_12_256_3_3_336_232_NA =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_12_256_3_3_336_232_NA-download.zip"

    override val registerIdentity_1_160_3_4_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_1_160_3_4_576_200_NA-download.zip"
    override val registerIdentity_21_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_21_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_24_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_24_256_3_4_336_232_NA-download.zip"

    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_11_256_3_5_576_248_1_1808_4_256-download.zip"



    override val registerIdentity_11_256_3_3_576_240_1_864_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_11_256_3_3_576_240_1_864_5_264-download.zip"

    override val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.9/registerIdentity_11_256_3_5_576_248_1_1808_5_296-download.zip"


    override val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_14_256_3_4_336_64_1_1480_5_296-download.zip"
    override val registerIdentity_21_256_3_5_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_21_256_3_5_576_232_NA-download.zip"

    override val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_1_256_3_6_336_560_1_2744_4_256-download.zip"
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


    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_10_256_3_3_576_248_1_1184_5_264.json"
    override val registerIdentity_11_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.10-fix/registerIdentity_11_256_3_3_576_248_NA.json"
    override val registerIdentity_11_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.6-fix/registerIdentity_11_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_11_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.12-fix/registerIdentity_11_256_3_5_576_248_NA.json"
    override val registerIdentity_14_256_3_3_576_240_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.8-fix/registerIdentity_14_256_3_3_576_240_NA.json"
    override val registerIdentity_14_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.12-fix/registerIdentity_14_256_3_4_336_232_1_1480_5_296.json"
    override val registerIdentity_14_256_3_4_576_248_1_1496_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.8-fix/registerIdentity_14_256_3_4_576_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_4_576_232_1_1480_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.12-fix/registerIdentity_1_256_3_4_576_232_1_1480_3_256.json"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_1_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_5_336_248_1_2120_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.7-fix/registerIdentity_1_256_3_5_336_248_1_2120_4_256.json"
    override val registerIdentity_2_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.7-fix/registerIdentity_2_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_2_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.7-fix/registerIdentity_2_256_3_4_336_248_NA.json"
    override val registerIdentity_20_160_3_2_576_184_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.8-fix/registerIdentity_20_160_3_2_576_184_NA.json"
    override val registerIdentity_20_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.9-fix/registerIdentity_20_160_3_3_576_200_NA.json"
    override val registerIdentity_20_256_3_5_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.11-fix/registerIdentity_20_256_3_5_336_248_NA.json"
    override val registerIdentity_21_256_3_3_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_21_256_3_3_576_232_NA.json"
    override val registerIdentity_23_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.10-fix/registerIdentity_23_160_3_3_576_200_NA.json"
    override val registerIdentity_24_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.11-fix/registerIdentity_24_256_3_4_336_248_NA.json"
    override val registerIdentity_3_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.10-fix/registerIdentity_3_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_3_512_3_3_336_264_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.6-fix/registerIdentity_3_512_3_3_336_264_NA.json"

    override val registerIdentity_6_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.11-fix/registerIdentity_6_160_3_3_336_216_1_1080_3_256.json"
    override val registerIdentity_1_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.9-fix/registerIdentity_1_256_3_5_576_248_NA.json"
    override val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.9-fix/registerIdentity_1_256_3_6_576_264_1_2448_3_256.json"
    override val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.3/registerIdentity_2_256_3_6_336_264_21_2448_6_2008.json"
    override val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.3/registerIdentity_2_256_3_6_336_248_1_2432_3_256.json"
    override val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.6-fix/registerIdentity_2_256_3_6_576_248_1_2432_3_256.json"
    override val registerIdentity_20_256_3_3_336_224_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.3/registerIdentity_20_256_3_3_336_224_NA.json"
    override val registerIdentity_21_256_3_4_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.5-fix/registerIdentity_21_256_3_4_576_232_NA.json"


    override val registerIdentity_11_256_3_5_584_264_1_2136_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.13/registerIdentity_11_256_3_5_584_264_1_2136_4_256.json"

    override val registerIdentity_11_256_3_5_576_264_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.13/registerIdentity_11_256_3_5_576_264_NA.json"

    override val registerIdentity_2_256_3_4_336_248_22_1496_7_2408: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.14/registerIdentity_2_256_3_4_336_248_22_1496_7_2408.json"

    override val registerIdentity_1_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.14/registerIdentity_1_256_3_4_336_232_NA.json"

    override val registerIdentity_25_384_3_3_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.15/registerIdentity_25_384_3_3_336_232_NA.json"
    override val registerIdentity_25_384_3_4_336_264_1_2904_2_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.15/registerIdentity_25_384_3_4_336_264_1_2904_2_256.json"
    override val registerIdentity_26_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.15/registerIdentity_26_512_3_3_336_248_NA.json"
    override val registerIdentity_26_512_3_3_336_264_1_1968_2_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.16/registerIdentity_26_512_3_3_336_264_1_1968_2_256.json"
    override val registerIdentity_27_512_3_4_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.16/registerIdentity_27_512_3_4_336_248_NA.json"

    override val registerIdentity_1_256_3_5_336_248_1_2120_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.18/registerIdentity_1_256_3_5_336_248_1_2120_3_256.json"
    override val registerIdentity_7_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.18/registerIdentity_7_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_8_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.19/registerIdentity_8_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_3_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.20/registerIdentity_3_256_3_3_576_248_NA.json"

    override val registerIdentity_25_384_3_3_336_264_1_2024_3_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.21/registerIdentity_25_384_3_3_336_264_1_2024_3_296.json"

    override val registerIdentity_28_384_3_3_576_264_24_2024_4_2792: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.22/registerIdentity_28_384_3_3_576_264_24_2024_4_2792.json"
    override val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.22/registerIdentity_1_256_3_6_576_248_1_2432_5_296.json"
    override val registerIdentity_25_384_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.22/registerIdentity_25_384_3_3_336_248_NA.json"

    override val registerIdentity_1_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.23/registerIdentity_1_160_3_3_576_200_NA.json"
    override val registerIdentity_1_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.23/registerIdentity_1_256_3_3_576_248_NA.json"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.23/registerIdentity_1_256_3_4_336_232_1_1480_5_296.json"

    override val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.24/registerIdentity_1_256_3_6_336_248_1_2744_4_256.json"
    override val registerIdentity_2_256_3_6_336_264_1_2448_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.24/registerIdentity_2_256_3_6_336_264_1_2448_3_256.json"
    override val registerIdentity_3_160_3_3_336_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.24/registerIdentity_3_160_3_3_336_200_NA.json"

    override val registerIdentity_3_160_3_4_576_216_1_1512_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.25/registerIdentity_3_160_3_4_576_216_1_1512_3_256.json"
    override val registerIdentity_11_256_3_2_336_216_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.25/registerIdentity_11_256_3_2_336_216_NA.json"
    override val registerIdentity_11_256_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.25/registerIdentity_11_256_3_3_336_248_NA.json"

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
    override val EXPLORER_API_URL = "https://evmscan.l2.rarimo.com"
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
    override val RARIMO_EXPLORER = "https://scan.rarimo.com/tx"
    override val GLOBAL_NOTIFICATION_TOPIC = "rarime"
    override val REWARD_NOTIFICATION_TOPIC: String = "rarime-rewardable"
    override val APPSFLYER_DEV_KEY = Keys.APPSFLYER_DEV_KEY

    override val VOTING_WEBSITE_URL = "https://freedomtool.org"
    override val VOTING_RELAYER_URL: String = "https://api.freedomtool.org"
    override val VOTING_REGISTRATION_SMT_CONTRACT_ADDRESS: String =
        "0x479F84502Db545FA8d2275372E0582425204A879"
    override val VOTING_RPC_URL: String = "https://l2.rarimo.com"
    override val PROPOSAL_CONTRACT_ADDRESS: String = "0x9C4b84a940C9D3140a1F40859b3d4367DC8d099a"
    override val MULTICALL_CONTRACT_ADDRRESS: String = "0xb4EE49BDf7cf199081b2a286B2B9B5f87AE930b1"

    override val FACE_REGISTRY_ADDRESS: String = "0x15DCd57B70D97F1D1F220ccb4e6B8E886aF3e3B9"


    override val GUESS_CELEBRITY_CONTRACT_ADDRESS: String =
        "0x5283f7B6A011433A6631701875A6f147e5c17a96"


    override val FACE_REGISTRY_ZKEY_URL: String =
        "https://storage.googleapis.com/rarimo-store/zkey/circuit_final.zkey"

    override val FACE_RECOGNITION_MODEL_URL: String =
        "https://storage.googleapis.com/rarimo-store/face-recognition/face-recognition.tflite"

    override val NOIR_TRUSTED_SETUP_URL: String =
        "https://storage.googleapis.com/rarimo-store/trusted-setups/ultraPlonkTrustedSetup.dat"


    override val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.0/registerIdentity_21_256_3_7_336_264_21_3072_6_2008-download.zip"

    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264 =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_11_256_3_3_576_248_1_1184_5_264-download.zip"
    override val registerIdentity_12_256_3_3_336_232_NA =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.2/registerIdentity_12_256_3_3_336_232_NA-download.zip"

    override val registerIdentity_1_160_3_4_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_1_160_3_4_576_200_NA-download.zip"
    override val registerIdentity_21_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_21_256_3_3_336_232_NA-download.zip"
    override val registerIdentity_24_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.3/registerIdentity_24_256_3_4_336_232_NA-download.zip"

    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.5/registerIdentity_11_256_3_5_576_248_1_1808_4_256-download.zip"


    override val registerIdentity_11_256_3_3_576_240_1_864_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.8/registerIdentity_11_256_3_3_576_240_1_864_5_264-download.zip"


    override val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.9/registerIdentity_11_256_3_5_576_248_1_1808_5_296-download.zip"


    override val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_14_256_3_4_336_64_1_1480_5_296-download.zip"
    override val registerIdentity_21_256_3_5_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.10/registerIdentity_21_256_3_5_576_232_NA-download.zip"

    override val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_1_256_3_6_336_560_1_2744_4_256-download.zip"
    override val registerIdentity_20_256_3_5_336_72_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.11/registerIdentity_20_256_3_5_336_72_NA-download.zip"

    override val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_4_160_3_3_336_216_1_1296_3_256-download.zip"
    override val registerIdentity_15_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_15_512_3_3_336_248_NA-download.zip"
    override val registerIdentity_20_160_3_3_736_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits/v0.2.12/registerIdentity_20_160_3_3_736_200_NA-download.zip"


    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_10_256_3_3_576_248_1_1184_5_264.json"
    override val registerIdentity_11_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.10-fix/registerIdentity_11_256_3_3_576_248_NA.json"
    override val registerIdentity_11_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.6-fix/registerIdentity_11_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_11_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.12-fix/registerIdentity_11_256_3_5_576_248_NA.json"
    override val registerIdentity_14_256_3_3_576_240_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.8-fix/registerIdentity_14_256_3_3_576_240_NA.json"
    override val registerIdentity_14_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.12-fix/registerIdentity_14_256_3_4_336_232_1_1480_5_296.json"
    override val registerIdentity_14_256_3_4_576_248_1_1496_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.8-fix/registerIdentity_14_256_3_4_576_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_4_576_232_1_1480_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.12-fix/registerIdentity_1_256_3_4_576_232_1_1480_3_256.json"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_1_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_5_336_248_1_2120_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.7-fix/registerIdentity_1_256_3_5_336_248_1_2120_4_256.json"
    override val registerIdentity_2_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.7-fix/registerIdentity_2_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_2_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.7-fix/registerIdentity_2_256_3_4_336_248_NA.json"
    override val registerIdentity_20_160_3_2_576_184_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.8-fix/registerIdentity_20_160_3_2_576_184_NA.json"
    override val registerIdentity_20_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.9-fix/registerIdentity_20_160_3_3_576_200_NA.json"
    override val registerIdentity_20_256_3_5_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.11-fix/registerIdentity_20_256_3_5_336_248_NA.json"
    override val registerIdentity_21_256_3_3_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_21_256_3_3_576_232_NA.json"
    override val registerIdentity_23_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.10-fix/registerIdentity_23_160_3_3_576_200_NA.json"
    override val registerIdentity_24_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.11-fix/registerIdentity_24_256_3_4_336_248_NA.json"
    override val registerIdentity_3_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.10-fix/registerIdentity_3_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_3_512_3_3_336_264_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.6-fix/registerIdentity_3_512_3_3_336_264_NA.json"
    override val registerIdentity_6_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.11-fix/registerIdentity_6_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_1_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.9-fix/registerIdentity_1_256_3_5_576_248_NA.json"
    override val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.9-fix/registerIdentity_1_256_3_6_576_264_1_2448_3_256.json"
    override val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.3/registerIdentity_2_256_3_6_336_264_21_2448_6_2008.json"
    override val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.3/registerIdentity_2_256_3_6_336_248_1_2432_3_256.json"
    override val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.6-fix/registerIdentity_2_256_3_6_576_248_1_2432_3_256.json"
    override val registerIdentity_20_256_3_3_336_224_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.3/registerIdentity_20_256_3_3_336_224_NA.json"
    override val registerIdentity_21_256_3_4_576_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.5-fix/registerIdentity_21_256_3_4_576_232_NA.json"


    override val registerIdentity_11_256_3_5_584_264_1_2136_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.13/registerIdentity_11_256_3_5_584_264_1_2136_4_256.json"

    override val registerIdentity_11_256_3_5_576_264_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.13/registerIdentity_11_256_3_5_576_264_NA.json"

    override val registerIdentity_2_256_3_4_336_248_22_1496_7_2408: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.14/registerIdentity_2_256_3_4_336_248_22_1496_7_2408.json"

    override val registerIdentity_1_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.14/registerIdentity_1_256_3_4_336_232_NA.json"

    override val registerIdentity_25_384_3_3_336_232_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.15/registerIdentity_25_384_3_3_336_232_NA.json"
    override val registerIdentity_25_384_3_4_336_264_1_2904_2_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.15/registerIdentity_25_384_3_4_336_264_1_2904_2_256.json"
    override val registerIdentity_26_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.15/registerIdentity_26_512_3_3_336_248_NA.json"


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


    override val registerIdentity_26_512_3_3_336_264_1_1968_2_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.16/registerIdentity_26_512_3_3_336_264_1_1968_2_256.json"
    override val registerIdentity_27_512_3_4_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.16/registerIdentity_27_512_3_4_336_248_NA.json"

    override val registerIdentity_1_256_3_5_336_248_1_2120_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.18/registerIdentity_1_256_3_5_336_248_1_2120_3_256.json"
    override val registerIdentity_7_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.18/registerIdentity_7_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_8_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.19/registerIdentity_8_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_3_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.20/registerIdentity_3_256_3_3_576_248_NA.json"

    override val registerIdentity_25_384_3_3_336_264_1_2024_3_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.21/registerIdentity_25_384_3_3_336_264_1_2024_3_296.json"

    override val registerIdentity_28_384_3_3_576_264_24_2024_4_2792: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.22/registerIdentity_28_384_3_3_576_264_24_2024_4_2792.json"
    override val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.22/registerIdentity_1_256_3_6_576_248_1_2432_5_296.json"
    override val registerIdentity_25_384_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.22/registerIdentity_25_384_3_3_336_248_NA.json"

    override val registerIdentity_1_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.23/registerIdentity_1_160_3_3_576_200_NA.json"
    override val registerIdentity_1_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.23/registerIdentity_1_256_3_3_576_248_NA.json"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.23/registerIdentity_1_256_3_4_336_232_1_1480_5_296.json"

    override val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.24/registerIdentity_1_256_3_6_336_248_1_2744_4_256.json"
    override val registerIdentity_2_256_3_6_336_264_1_2448_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.24/registerIdentity_2_256_3_6_336_264_1_2448_3_256.json"
    override val registerIdentity_3_160_3_3_336_200_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.24/registerIdentity_3_160_3_3_336_200_NA.json"

    override val registerIdentity_3_160_3_4_576_216_1_1512_3_256: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.25/registerIdentity_3_160_3_4_576_216_1_1512_3_256.json"
    override val registerIdentity_11_256_3_2_336_216_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.25/registerIdentity_11_256_3_2_336_216_NA.json"
    override val registerIdentity_11_256_3_3_336_248_NA: String =
        "https://storage.googleapis.com/rarimo-store/passport-zk-circuits-noir/v0.1.25/registerIdentity_11_256_3_3_336_248_NA.json"

}
