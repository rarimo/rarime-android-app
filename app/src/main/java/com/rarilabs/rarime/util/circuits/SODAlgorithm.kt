package com.rarilabs.rarime.util.circuits

import CircuitAlgorithmType
import CircuitHashAlgorithmType

enum class SODAlgorithm(val value: String) {
    SHA256WithRSAEncryption("sha256WithRSAEncryption"),
    RSASSAPSS("rsassaPss"),
    ECDSA_WITH_SHA1("ecdsa_with_SHA1"),
    ECDSA_WITH_SHA256("ecdsa_with_SHA256");

    fun getCircuitSignatureAlgorithm(): CircuitAlgorithmType {
        return when (this) {
            SHA256WithRSAEncryption -> CircuitAlgorithmType.RSA
            RSASSAPSS -> CircuitAlgorithmType.RSAPSS
            ECDSA_WITH_SHA1, ECDSA_WITH_SHA256 -> CircuitAlgorithmType.ECDSA
        }
    }

    fun getCircuitSignatureHashAlgorithm(): CircuitHashAlgorithmType {
        return when (this) {
            SHA256WithRSAEncryption, RSASSAPSS, ECDSA_WITH_SHA256 -> CircuitHashAlgorithmType.HA256
            ECDSA_WITH_SHA1 -> CircuitHashAlgorithmType.HA160
        }
    }

    companion object {
        fun fromValue(value: String): SODAlgorithm? {
            return values().firstOrNull { it.value.equals(value, ignoreCase = true) }
        }
    }
}