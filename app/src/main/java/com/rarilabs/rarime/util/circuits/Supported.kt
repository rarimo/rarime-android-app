package com.rarilabs.rarime.util.circuits

import CircuitAAAlgorithm
import CircuitAlgorithmType
import CircuitCurveType
import CircuitExponentType
import CircuitHashAlgorithmType
import CircuitKeySizeType
import CircuitSaltType
import CircuitSignatureType

object SupportRegisterIdentityCircuitSignatureType {
    val supported: List<CircuitSignatureType> = listOf(
        // RSA
        CircuitSignatureType(
            staticId = 1u,
            algorithm = CircuitAlgorithmType.RSA,
            keySize = CircuitKeySizeType.B2048,
            exponent = CircuitExponentType.E65537,
            salt = null,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),
        CircuitSignatureType(
            staticId = 2u,
            algorithm = CircuitAlgorithmType.RSA,
            keySize = CircuitKeySizeType.B4096,
            exponent = CircuitExponentType.E65537,
            salt = null,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),

        CircuitSignatureType(
            staticId = 3u, // TODO: check id
            algorithm = CircuitAlgorithmType.RSA,
            keySize = CircuitKeySizeType.B2048,
            exponent = CircuitExponentType.E65537,
            salt = null,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA160
        ),

        CircuitSignatureType(
            staticId = 3u, // TODO: check id
            algorithm = CircuitAlgorithmType.RSA,
            keySize = CircuitKeySizeType.B3072,
            exponent = CircuitExponentType.E3,
            salt = null,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA160
        ),


        // RSAPSS
        CircuitSignatureType(
            staticId = 10u,
            algorithm = CircuitAlgorithmType.RSAPSS,
            keySize = CircuitKeySizeType.B2048,
            exponent = CircuitExponentType.E3,
            salt = CircuitSaltType.S32,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),
        CircuitSignatureType(
            staticId = 11u,
            algorithm = CircuitAlgorithmType.RSAPSS,
            keySize = CircuitKeySizeType.B2048,
            exponent = CircuitExponentType.E65537,
            salt = CircuitSaltType.S32,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),
        CircuitSignatureType(
            staticId = 12u,
            algorithm = CircuitAlgorithmType.RSAPSS,
            keySize = CircuitKeySizeType.B2048,
            exponent = CircuitExponentType.E65537,
            salt = CircuitSaltType.S64,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),
        CircuitSignatureType(
            staticId = 13u,
            algorithm = CircuitAlgorithmType.RSAPSS,
            keySize = CircuitKeySizeType.B2048,
            exponent = CircuitExponentType.E65537,
            salt = CircuitSaltType.S48,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA384
        ),

        CircuitSignatureType(
            staticId = 11u,
            algorithm = CircuitAlgorithmType.RSAPSS,
            keySize = CircuitKeySizeType.B3072,
            exponent = CircuitExponentType.E65537,
            salt = CircuitSaltType.S32,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),

        // ECDSA
        CircuitSignatureType(
            staticId = 20u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B256,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.SECP256R1,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),

        CircuitSignatureType(
            staticId = 24u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B224,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.SECP224R1,
            hashAlgorithm = CircuitHashAlgorithmType.HA224
        ),

        CircuitSignatureType(
            staticId = 21u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B256,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.BRAINPOOLP256,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),
        CircuitSignatureType(
            staticId = 22u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B320,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.BRAINPOOL320R1,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),
        CircuitSignatureType(
            staticId = 23u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B192,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.SECP192R1,
            hashAlgorithm = CircuitHashAlgorithmType.HA160
        ),
        CircuitSignatureType(
            staticId = 20u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B256,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.PRIME256V1,
            hashAlgorithm = CircuitHashAlgorithmType.HA256
        ),
        CircuitSignatureType(
            staticId = 20u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B384,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.BRAINPOOLP384R1,
            hashAlgorithm = CircuitHashAlgorithmType.HA384
        ),
        CircuitSignatureType(
            staticId = 20u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = CircuitKeySizeType.B512,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.BRAINPOOLP512R1,
            hashAlgorithm = CircuitHashAlgorithmType.HA512
        )

    )

    fun getSupportedSignatureTypeId(type: CircuitSignatureType): UInt? {
        return supported.firstOrNull {
            it.algorithm == type.algorithm &&
                    it.keySize == type.keySize &&
                    it.exponent == type.exponent &&
                    it.salt == type.salt &&
                    it.curve == type.curve &&
                    it.hashAlgorithm == type.hashAlgorithm
        }?.staticId
    }
}


object SupportRegisterIdentityCircuitAAType {
    val supported: List<CircuitAAAlgorithm> = listOf(
        // RSA
        CircuitAAAlgorithm(
            staticId = 1u,
            algorithm = CircuitAlgorithmType.RSA,
            keySize = CircuitKeySizeType.B1024,
            exponent = CircuitExponentType.E65537,
            salt = null,
            curve = null,
            hashAlgorithm = CircuitHashAlgorithmType.HA160
        ),

        // ECDSA
        CircuitAAAlgorithm(
            staticId = 21u,
            algorithm = CircuitAlgorithmType.ECDSA,
            keySize = null,
            exponent = null,
            salt = null,
            curve = CircuitCurveType.BRAINPOOLP256,
            hashAlgorithm = CircuitHashAlgorithmType.HA160
        )
    )

    fun getSupportedSignatureTypeId(type: CircuitAAAlgorithm): UInt? {
        return supported.firstOrNull {
            it.algorithm == type.algorithm &&
                    it.hashAlgorithm == type.hashAlgorithm
        }?.staticId
    }
}

