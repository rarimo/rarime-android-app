package com.rarilabs.rarime.util.circuits

enum class RegisteredCircuitData(val value: String) {
    REGISTER_IDENTITY_1_256_3_5_576_248_NA("registerIdentity_1_256_3_5_576_248_NA"),
    REGISTER_IDENTITY_1_256_3_6_576_248_1_2432_5_296("registerIdentity_1_256_3_6_576_248_1_2432_5_296"),
    REGISTER_IDENTITY_2_256_3_6_336_264_21_2448_6_2008("registerIdentity_2_256_3_6_336_264_21_2448_6_2008"),
    REGISTER_IDENTITY_21_256_3_7_336_264_21_3072_6_2008("registerIdentity_21_256_3_7_336_264_21_3072_6_2008"),
    REGISTER_IDENTITY_1_256_3_6_576_264_1_2448_3_256("registerIdentity_1_256_3_6_576_264_1_2448_3_256"),
    REGISTER_IDENTITY_2_256_3_6_336_248_1_2432_3_256("registerIdentity_2_256_3_6_336_248_1_2432_3_256"),
    REGISTER_IDENTITY_2_256_3_6_576_248_1_2432_3_256("registerIdentity_2_256_3_6_576_248_1_2432_3_256"),
    REGISTER_IDENTITY_11_256_3_3_576_248_1_1184_5_264("registerIdentity_11_256_3_3_576_248_1_1184_5_264"),
    REGISTER_IDENTITY_12_256_3_3_336_232_NA("registerIdentity_12_256_3_3_336_232_NA"),
    REGISTER_IDENTITY_1_256_3_4_336_232_1_1480_5_296("registerIdentity_1_256_3_4_336_232_1_1480_5_296"),
    REGISTER_IDENTITY_1_256_3_4_600_248_1_1496_3_256("registerIdentity_1_256_3_4_600_248_1_1496_3_256");
    companion object {
        private val map = entries.associateBy(RegisteredCircuitData::value)
        fun fromValue(value: String) = map[value]
    }
}