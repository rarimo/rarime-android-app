package com.rarilabs.rarime.util.circuits

// RegisterIdentityCircuitType class in Kotlin
data class RegisterIdentityCircuitType(
    val signatureType: CircuitSignatureType,
    val passportHashType: CircuitPassportHashType,
    val ecChunkNumber: UInt,
    val ecDigestPosition: UInt,
    val dg1DigestPositionShift: UInt,
    val aaType: CircuitAAType? = null
) {
    fun buildName(): String? {
        var name = "registerIdentity"
        val signatureTypeId = signatureType.getId() ?: return null
        name += "_$signatureTypeId"
        name += "_${passportHashType.rawValue}"
        name += "_$ecChunkNumber"
        name += "_$ecDigestPosition"
        name += "_$dg1DigestPositionShift"
        if (aaType != null) {
            name += "_${aaType.dg15DigestPositionShift}"
            name += "_${aaType.dg15ChunkNumber}"
            name += "_${aaType.aaKeyPositionShift}"
        } else {
            name += "_NA"
        }
        return name
    }

    data class CircuitSignatureType(
        val staticId: UInt,
        val algorithm: CircuitSignatureAlgorithmType,
        val keySize: CircuitSignatureKeySizeType,
        val exponent: CircuitSignatureExponentType?,
        val salt: CircuitSignatureSaltType?,
        val curve: CircuitSignatureCurveType?,
        val hashAlgorithm: CircuitSignatureHashAlgorithmType?
    ) {
        fun getId(): String? {
            return SupportRegisterIdentityCircuitSignatureType.getSupportedSignatureTypeId(this)?.toString()
        }
    }

    data class CircuitAAType(
        val dg15DigestPositionShift: UInt,
        val dg15ChunkNumber: UInt,
        val aaKeyPositionShift: UInt
    )

    enum class CircuitPassportHashType(val rawValue: Int) {
        SHA1(160),
        SHA2_256(256),
        SHA2_384(384),
        SHA2_512(512)
    }

    enum class CircuitDocumentType(val rawValue: Int) {
        TD1(1),
        TD3(3)
    }
}

// Enums for CircuitSignatureType
enum class CircuitSignatureAlgorithmType {
    RSA, RSAPSS, ECDSA
}

enum class CircuitSignatureKeySizeType {
    B2048, B4096, B256, B320, B192
}

enum class CircuitSignatureExponentType {
    E3, E65537
}

enum class CircuitSignatureSaltType {
    S32, S64, S48
}

enum class CircuitSignatureCurveType {
    SECP256R1, BRAINPOOLP256, BRAINPOOL320R1, SECP192R1
}

enum class CircuitSignatureHashAlgorithmType {
    HA256, HA384, HA160
}

// Support class to provide valid types
object SupportRegisterIdentityCircuitSignatureType {
     private val supported: List<RegisterIdentityCircuitType.CircuitSignatureType> = listOf(
        // RSA
        RegisterIdentityCircuitType.CircuitSignatureType(1u, CircuitSignatureAlgorithmType.RSA, CircuitSignatureKeySizeType.B2048, CircuitSignatureExponentType.E65537, null, null, CircuitSignatureHashAlgorithmType.HA256),
        RegisterIdentityCircuitType.CircuitSignatureType(2u, CircuitSignatureAlgorithmType.RSA, CircuitSignatureKeySizeType.B4096, CircuitSignatureExponentType.E65537, null, null, CircuitSignatureHashAlgorithmType.HA256),
        // RSAPSS
        RegisterIdentityCircuitType.CircuitSignatureType(10u, CircuitSignatureAlgorithmType.RSAPSS, CircuitSignatureKeySizeType.B2048, CircuitSignatureExponentType.E3, CircuitSignatureSaltType.S32, null, CircuitSignatureHashAlgorithmType.HA256),
        RegisterIdentityCircuitType.CircuitSignatureType(11u, CircuitSignatureAlgorithmType.RSAPSS, CircuitSignatureKeySizeType.B2048, CircuitSignatureExponentType.E65537, CircuitSignatureSaltType.S32, null, CircuitSignatureHashAlgorithmType.HA256),
        RegisterIdentityCircuitType.CircuitSignatureType(12u, CircuitSignatureAlgorithmType.RSAPSS, CircuitSignatureKeySizeType.B2048, CircuitSignatureExponentType.E65537, CircuitSignatureSaltType.S64, null, CircuitSignatureHashAlgorithmType.HA256),
        RegisterIdentityCircuitType.CircuitSignatureType(13u, CircuitSignatureAlgorithmType.RSAPSS, CircuitSignatureKeySizeType.B2048, CircuitSignatureExponentType.E65537, CircuitSignatureSaltType.S48, null, CircuitSignatureHashAlgorithmType.HA384),
        // ECDSA
        RegisterIdentityCircuitType.CircuitSignatureType(20u, CircuitSignatureAlgorithmType.ECDSA, CircuitSignatureKeySizeType.B256, null, null, CircuitSignatureCurveType.SECP256R1, CircuitSignatureHashAlgorithmType.HA256),
        RegisterIdentityCircuitType.CircuitSignatureType(21u, CircuitSignatureAlgorithmType.ECDSA, CircuitSignatureKeySizeType.B256, null, null, CircuitSignatureCurveType.BRAINPOOLP256, CircuitSignatureHashAlgorithmType.HA256),
        RegisterIdentityCircuitType.CircuitSignatureType(22u, CircuitSignatureAlgorithmType.ECDSA, CircuitSignatureKeySizeType.B320, null, null, CircuitSignatureCurveType.BRAINPOOL320R1, CircuitSignatureHashAlgorithmType.HA256),
        RegisterIdentityCircuitType.CircuitSignatureType(23u, CircuitSignatureAlgorithmType.ECDSA, CircuitSignatureKeySizeType.B192, null, null, CircuitSignatureCurveType.SECP192R1, CircuitSignatureHashAlgorithmType.HA160)
    )


    fun parseBuildName(buildName: String) {
        val parts = buildName.split("_")

        if (parts.size < 6) {
            println("Invalid buildName format")
            return
        }

        val signatureTypeId = parts[1].toIntOrNull()
        val passportHashTypeId = parts[2].toIntOrNull()
        val ecChunkNumber = parts[3].toIntOrNull()
        val ecDigestPosition = parts[4].toIntOrNull()
        val dg1DigestPositionShift = parts[5].toIntOrNull()

        if (signatureTypeId == null || passportHashTypeId == null || ecChunkNumber == null || ecDigestPosition == null || dg1DigestPositionShift == null) {
            println("Error parsing buildName")
            return
        }

        val signatureType = supported.firstOrNull { it.staticId.toInt() == signatureTypeId }
        val passportHashType = RegisterIdentityCircuitType.CircuitPassportHashType.values().firstOrNull { it.rawValue == passportHashTypeId }

        println("Parsed Build Name: $buildName")
        println("Signature Type:")
        if (signatureType != null) {
            println("  Algorithm: ${signatureType.algorithm}")
            println("  Key Size: ${signatureType.keySize}")
            println("  Exponent: ${signatureType.exponent}")
            println("  Salt: ${signatureType.salt}")
            println("  Curve: ${signatureType.curve}")
            println("  Hash Algorithm: ${signatureType.hashAlgorithm}")
        } else {
            println("  Unknown signature type")
        }

        println("Passport Hash Type: ${passportHashType ?: "Unknown"}")
        println("EC Chunk Number: $ecChunkNumber")
        println("EC Digest Position: $ecDigestPosition")
        println("DG1 Digest Position Shift: $dg1DigestPositionShift")

        if (parts.size > 6) {
            val aaTypePart = parts[6]
            if (aaTypePart == "NA") {
                println("AA Type: No Active Authentication")
            } else if (parts.size >= 9) {
                val dg15DigestPositionShift = parts[6].toIntOrNull()
                val dg15ChunkNumber = parts[7].toIntOrNull()
                val aaKeyPositionShift = parts[8].toIntOrNull()

                if (dg15DigestPositionShift != null && dg15ChunkNumber != null && aaKeyPositionShift != null) {
                    println("AA Type:")
                    println("  DG15 Digest Position Shift: $dg15DigestPositionShift")
                    println("  DG15 Chunk Number: $dg15ChunkNumber")
                    println("  AA Key Position Shift: $aaKeyPositionShift")
                } else {
                    println("Error parsing AA Type details")
                }
            } else {
                println("Invalid AA Type format")
            }
        }
    }

    fun getSupportedSignatureTypeId(type: RegisterIdentityCircuitType.CircuitSignatureType): UInt? {
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
