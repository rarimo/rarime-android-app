import com.rarilabs.rarime.util.circuits.SupportRegisterIdentityCircuitAAType
import com.rarilabs.rarime.util.circuits.SupportRegisterIdentityCircuitSignatureType

data class RegisterIdentityCircuitType(
    val signatureType: CircuitSignatureType,
    val passportHashType: CircuitPassportHashType,
    val documentType: CircuitDocumentType,
    val ecChunkNumber: UInt,
    val ecDigestPosition: UInt,
    val dg1DigestPositionShift: UInt,
    var aaType: CircuitAAType? = null
) {
    fun buildName(): String {
        var name = "registerIdentity"
        val signatureTypeId = signatureType.getId() ?: throw IllegalArgumentException("No signatureType")

        name += "_$signatureTypeId"
        name += "_${passportHashType.getId()}"
        name += "_${documentType.getId()}"
        name += "_$ecChunkNumber"
        name += "_$ecDigestPosition"
        name += "_$dg1DigestPositionShift"

        aaType?.let { aa ->
            val aaTypeId = aa.aaAlgorithm.getId() ?: throw IllegalStateException("aa.aaAlgorithm.getId not found")

            name += "_$aaTypeId"
            name += "_${aa.dg15DigestPositionShift}"
            name += "_${aa.dg15ChunkNumber}"
            name += "_${aa.aaKeyPositionShift}"
        } ?: run {
            name += "_NA"
        }

        return name
    }
}

data class CircuitSignatureType(
    val staticId: UInt,
    val algorithm: CircuitAlgorithmType,
    val keySize: CircuitKeySizeType,
    val exponent: CircuitExponentType?,
    val salt: CircuitSaltType?,
    val curve: CircuitCurveType?,
    val hashAlgorithm: CircuitHashAlgorithmType
) {
    fun getId(): String? {
        return SupportRegisterIdentityCircuitSignatureType.getSupportedSignatureTypeId(this)?.toString()
    }
}

enum class CircuitPassportHashType(val value: String) {
    SHA1("sha-1"),
    SHA256("sha-256"),
    SHA384("sha-384"),
    SHA512("sha-512");

    fun getId(): UInt {
        return when (this) {
            SHA1 -> 160u
            SHA256 -> 256u
            SHA384 -> 384u
            SHA512 -> 512u
        }
    }

    fun getChunkSize(): UInt {
        return when (this) {
            SHA1, SHA256 -> 512u
            SHA384, SHA512 -> 1024u
        }
    }

    companion object {
        fun fromValue(value: String): CircuitPassportHashType? {
            return values().firstOrNull { it.value.equals(value, ignoreCase = true) }
        }
    }
}

enum class CircuitDocumentType(val value: String) {
    TD1("TD1"),
    TD3("TD3");

    fun getId(): UInt {
        return when (this) {
            TD1 -> 1u
            TD3 -> 3u
        }
    }

    companion object {
        fun fromValue(value: String): CircuitDocumentType? {
            return entries.firstOrNull { it.value.equals(value, ignoreCase = true) }
        }
    }
}

data class CircuitAAType(
    val aaAlgorithm: CircuitAAAlgorithm,
    val dg15DigestPositionShift: UInt,
    val dg15ChunkNumber: UInt,
    val aaKeyPositionShift: UInt
)

data class CircuitAAAlgorithm(
    val staticId: UInt,
    val algorithm: CircuitAlgorithmType,
    val keySize: CircuitKeySizeType?,
    val exponent: CircuitExponentType?,
    val salt: CircuitSaltType?,
    val curve: CircuitCurveType?,
    val hashAlgorithm: CircuitHashAlgorithmType
) {
    fun getId(): String? {
        return SupportRegisterIdentityCircuitAAType.getSupportedSignatureTypeId(this)?.toString()
    }
}

enum class CircuitAlgorithmType {
    RSA, RSAPSS, ECDSA
}

enum class CircuitKeySizeType {
    B1024, B2048, B4096, B256, B320, B192
}

enum class CircuitExponentType {
    E3, E65537
}

enum class CircuitSaltType {
    S32, S64, S48
}

enum class CircuitCurveType {
    SECP256R1, BRAINPOOLP256, BRAINPOOL320R1, SECP192R1
}

enum class CircuitHashAlgorithmType {
    HA256, HA384, HA160
}
fun ByteArray.findSubarrayIndex(subarray: ByteArray): UInt? {
    for (i in indices) {
        if (i + subarray.size <= size && copyOfRange(i, i + subarray.size).contentEquals(subarray)) {
            return i.toUInt()
        }
    }
    return null
}