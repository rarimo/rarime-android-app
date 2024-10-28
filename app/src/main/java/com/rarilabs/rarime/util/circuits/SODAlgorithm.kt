package com.rarilabs.rarime.util.circuits

import CircuitAlgorithmType
import CircuitHashAlgorithmType

enum class SODAlgorithm(val value: String) {
    RSA("RSA"),
    SHA256WithRSAEncryption("sha256WithRSA"),
    RSASSAPSS("SSAwithRSA/PSS"),
    ECDSA_WITH_SHA1("SHA1withECDSA"),
    ECDSA_WITH_SHA256("SHA256withECDSA"),
    ECDSA_WITH_SHA384("SHA384withECDSA");

    fun getCircuitSignatureAlgorithm(): CircuitAlgorithmType {
        return when (this) {
            SHA256WithRSAEncryption, RSA -> CircuitAlgorithmType.RSA
            RSASSAPSS -> CircuitAlgorithmType.RSAPSS
            ECDSA_WITH_SHA1, ECDSA_WITH_SHA256, ECDSA_WITH_SHA384 -> CircuitAlgorithmType.ECDSA
        }
    }

    fun getCircuitSignatureHashAlgorithm(): CircuitHashAlgorithmType {
        return when (this) {
            ECDSA_WITH_SHA384 -> CircuitHashAlgorithmType.HA384
            SHA256WithRSAEncryption, RSASSAPSS, ECDSA_WITH_SHA256, RSA -> CircuitHashAlgorithmType.HA256
            ECDSA_WITH_SHA1 -> CircuitHashAlgorithmType.HA160
        }
    }

    companion object {
        fun fromValue(value: String): SODAlgorithm? {
            return values().firstOrNull { it.value.equals(value, ignoreCase = true) }
        }
    }
}