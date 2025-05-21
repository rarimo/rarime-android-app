package com.rarilabs.rarime.util.circuits

import CircuitAlgorithmType
import CircuitHashAlgorithmType

enum class SODAlgorithm(val value: String) {
    RSA("RSA"),
    SHA256WithRSAEncryption("sha256WithRSA"),
    SHA1WithRSAEncryption("SHA1withRSA"),
    SHA512withECDSA("SHA512withECDSA"),
    RSASSAPSS("SSAwithRSA/PSS"),
    ECDSA_WITH_SHA1("SHA1withECDSA"),
    ECDSA_WITH_SHA256("SHA256withECDSA"),
    ECDSA_WITH_SHA384("SHA384withECDSA"),
    ECDSA_WITH_SHA224("SHA224withECDSA");

    fun getCircuitSignatureAlgorithm(): CircuitAlgorithmType {
        return when (this) {
            SHA256WithRSAEncryption, SHA1WithRSAEncryption, RSA -> CircuitAlgorithmType.RSA
            RSASSAPSS -> CircuitAlgorithmType.RSAPSS
            ECDSA_WITH_SHA1, ECDSA_WITH_SHA224, ECDSA_WITH_SHA256, ECDSA_WITH_SHA384, SHA512withECDSA -> CircuitAlgorithmType.ECDSA
        }
    }

    fun getCircuitSignatureHashAlgorithm(): CircuitHashAlgorithmType {
        return when (this) {
            ECDSA_WITH_SHA224 -> CircuitHashAlgorithmType.HA224
            ECDSA_WITH_SHA384 -> CircuitHashAlgorithmType.HA384
            SHA512withECDSA -> CircuitHashAlgorithmType.HA512
            SHA256WithRSAEncryption, RSASSAPSS, ECDSA_WITH_SHA256, RSA -> CircuitHashAlgorithmType.HA256
            ECDSA_WITH_SHA1, SHA1WithRSAEncryption -> CircuitHashAlgorithmType.HA160
        }
    }

    companion object {
        fun fromValue(value: String): SODAlgorithm? {
            return values().firstOrNull { it.value.equals(value, ignoreCase = true) }
        }
    }
}