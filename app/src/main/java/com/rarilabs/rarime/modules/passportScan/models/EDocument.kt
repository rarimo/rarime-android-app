package com.rarilabs.rarime.modules.passportScan.models

import CircuitAAAlgorithm
import CircuitAAType
import CircuitAlgorithmType
import CircuitCurveType
import CircuitDocumentType
import CircuitExponentType
import CircuitHashAlgorithmType
import CircuitKeySizeType
import CircuitPassportHashType
import CircuitSaltType
import CircuitSignatureType
import RegisterIdentityCircuitType
import android.util.Log
import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.Dg15FileOwn
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.circuits.SODAlgorithm
import com.rarilabs.rarime.util.circuits.deriveCurveName
import com.rarilabs.rarime.util.decodeHexString
import findSubarrayIndex
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1Set
import org.bouncycastle.asn1.ASN1TaggedObject
import org.bouncycastle.asn1.DLApplicationSpecific
import org.bouncycastle.math.ec.ECPoint
import org.jmrtd.lds.icao.DG1File
import org.web3j.utils.Numeric
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.ECFieldF2m
import java.security.spec.ECFieldFp


data class EDocument(
    var docType: DocType? = null,
    var personDetails: PersonDetails? = null,
    var additionalPersonDetails: AdditionalPersonDetails? = null,
    var isPassiveAuth: Boolean = false,
    var isActiveAuth: Boolean = false,
    var isChipAuth: Boolean = false,
    var sod: String? = null,
    var dg1: String? = null,
    var dg15: String? = null,
    var dg15Pem: String? = null,
    var aaSignature: ByteArray? = null,
    var aaResponse: String? = null,
) {

    fun getSodFile(): SODFileOwn {
        val sodStream = this.sod!!.decodeHexString().inputStream()
        return SODFileOwn(sodStream)
    }

    fun getDg1File(): DG1File {
        val dg1File = this.dg1!!.decodeHexString().inputStream()
        return DG1File(dg1File)
    }

    fun getDg15File(): Dg15FileOwn? {
        if (this.dg15.isNullOrEmpty()) return null
        val dG15File = this.dg15!!.decodeHexString().inputStream()
        return Dg15FileOwn(dG15File)
    }

    private fun getPublicKeySize(publicKey: PublicKey?): Int {
        return when (publicKey) {
            is RSAPublicKey -> publicKey.modulus.bitLength()
            is ECPublicKey -> {
                val params = publicKey.params
                val curve = params.curve
                when (val field = curve.field) {
                    is ECFieldFp -> field.p.bitLength() // For prime fields
                    is ECFieldF2m -> field.m // For binary fields
                    else -> throw IllegalArgumentException("Unsupported ECField type")
                }
            }

            else -> throw IllegalArgumentException("Unsupported key type or null key")
        }
    }

    fun getRegisterIdentityCircuitType(): RegisterIdentityCircuitType {
        try {
            // Initialize DataGroup1 and SOD using jMRTD
            val dg1Group = getDg1File()
            val sodFile = getSodFile()

            // Get the signature algorithm from SOD
            val sodSignatureAlgorithmName = sodFile.digestEncryptionAlgorithm
            val sodSignatureAlgorithm = SODAlgorithm.fromValue(sodSignatureAlgorithmName)
                ?: throw IllegalStateException("SOD algorithm not found: $sodSignatureAlgorithmName")

            // Get the public key from SOD and determine its size
            val sodPublicKey = sodFile.docSigningCertificate.publicKey
            val publicKeySize = getPublicKeySupportedSize(getPublicKeySize(sodPublicKey))
                ?: throw IllegalStateException(
                    "Public key size not found: " + getPublicKeySize(
                        sodPublicKey
                    )
                )

            // Create the CircuitSignatureType
            val signatureType = CircuitSignatureType(
                staticId = 0u,
                algorithm = sodSignatureAlgorithm.getCircuitSignatureAlgorithm(),
                keySize = publicKeySize,
                exponent = getPublicKeyExponent(sodPublicKey),
                salt = getSaltSize(),
                curve = if (sodPublicKey is ECPublicKey) getPublicKeyCurve(sodPublicKey) else null,
                hashAlgorithm = sodSignatureAlgorithm.getCircuitSignatureHashAlgorithm()
            )

            // Get the passport hash type
            val digestAlgorithm = sodFile.digestAlgorithm
            val digestEncryptionAlgorithm = sodFile.signerInfoDigestAlgorithm
            val passportHashType = CircuitPassportHashType.fromValue(digestAlgorithm)
                ?: throw IllegalArgumentException("Invalid digest algorithm")

            // Get the document type
            val documentTypeString = getStandardizedDocumentType(dg1Group.mrzInfo.documentCode)
            val documentType =
                CircuitDocumentType.fromValue(documentTypeString) ?: throw IllegalArgumentException(
                    "Invalid document type"
                )


            val signedAttributes = sodFile.eContent
            val encapsulatedContent = Numeric.hexStringToByteArray(sodFile.readASN1Data())

            val ecHash = MessageDigest.getInstance(digestEncryptionAlgorithm, "BC")
                .digest(encapsulatedContent)

            val ecChunkNumber = getChunkNumber(encapsulatedContent, passportHashType.getChunkSize())

            // Find digest positions
            val ecDigestPosition = signedAttributes.findSubarrayIndex(ecHash)
                ?: throw Exception("Unable to find EC digest position")


            val messageDigestDG1 =
                MessageDigest.getInstance(passportHashType.value.uppercase(), "BC")

            val dg1Hash = messageDigestDG1.digest(dg1!!.decodeHexString())

            val dg1DigestPositionShift = encapsulatedContent.findSubarrayIndex(dg1Hash)
                ?: throw Exception("Unable to find DG1 digest position")

            // Initialize the circuit type
            val circuitType = RegisterIdentityCircuitType(
                signatureType = signatureType,
                passportHashType = passportHashType,
                documentType = documentType,
                ecChunkNumber = ecChunkNumber,
                ecDigestPosition = ecDigestPosition * 8u,
                dg1DigestPositionShift = dg1DigestPositionShift * 8u,
                aaType = null
            )

            // Process DG15 if available
            if (!dg15.isNullOrEmpty()) {
                val dg15Wrapper = getDg15File()

                val dg15Raw = dg15!!.decodeHexString() // Due to strange behaviour

                val messageDigest =
                    MessageDigest.getInstance(passportHashType.value.uppercase(), "BC")
                val dg15Hash = messageDigest.digest(dg15!!.decodeHexString())

                val dg15DigestPositionShift =
                    encapsulatedContent.findSubarrayIndex(dg15Hash) ?: throw Exception(
                        "Unable to find DG15 digest position: dg15Hash: ${
                            Numeric.toHexStringNoPrefix(
                                dg15Hash
                            )
                        } \n ${Numeric.toHexStringNoPrefix(encapsulatedContent)}"
                    )

                val dg15ChunkNumber = getChunkNumber(dg15Raw, passportHashType.getChunkSize())

                val pubkeyData: ByteArray
                val aaAlgorithm: CircuitAlgorithmType
                var aaKeySize: CircuitKeySizeType? = null
                var aaExponent: CircuitExponentType? = null
                var aaCurve: CircuitCurveType? = null

                val publicKey = dg15Wrapper!!.publicKey

                if (publicKey.algorithm.equals("RSA", ignoreCase = true)) {
                    pubkeyData =
                        CryptoUtilsPassport.getModulusFromRSAPublicKey(publicKey) ?: ByteArray(0)
                    aaAlgorithm = CircuitAlgorithmType.RSA

                    aaKeySize =
                        getPublicKeySupportedSize(CryptoUtilsPassport.getPublicKeySize(publicKey))
                    aaExponent = getPublicKeyExponent(publicKey)
                } else if (publicKey.algorithm.equals("EC", ignoreCase = true)) {
                    val pubKey = publicKey as ECPublicKey
                    pubkeyData = CryptoUtilsPassport.getXYFromECDSAPublicKey(pubKey) ?: ByteArray(0)
                    aaAlgorithm = CircuitAlgorithmType.ECDSA

                    aaCurve = getPublicKeyCurve(pubKey)
                    Log.i("aaCurve", aaCurve?.name.toString())
                } else {
                    throw Exception("Unable to find public key")
                }


                val aaKeyPositionShift = dg15Raw.findSubarrayIndex(pubkeyData)
                    ?: throw Exception("Unable to find AA key position")

                circuitType.aaType = CircuitAAType(
                    aaAlgorithm = CircuitAAAlgorithm(
                        staticId = 0u,
                        algorithm = aaAlgorithm,
                        keySize = aaKeySize,
                        exponent = aaExponent,
                        salt = null,
                        curve = aaCurve,
                        hashAlgorithm = CircuitHashAlgorithmType.HA160
                    ),
                    dg15DigestPositionShift = dg15DigestPositionShift * 8u,
                    dg15ChunkNumber = dg15ChunkNumber,
                    aaKeyPositionShift = aaKeyPositionShift * 8u
                )
            }

            return circuitType
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun getPublicKeySupportedSize(size: Int): CircuitKeySizeType? {
        return when (size) {
            3072 -> CircuitKeySizeType.B3072
            1024 -> CircuitKeySizeType.B1024
            2048 -> CircuitKeySizeType.B2048
            4096 -> CircuitKeySizeType.B4096
            521 -> CircuitKeySizeType.B521
            512 -> CircuitKeySizeType.B512
            256 -> CircuitKeySizeType.B256
            320 -> CircuitKeySizeType.B320
            384 -> CircuitKeySizeType.B384
            224 -> CircuitKeySizeType.B224
            192 -> CircuitKeySizeType.B192
            else -> null
        }
    }

    private fun getSaltSize(): CircuitSaltType? {
        val asn1Data = Numeric.hexStringToByteArray(sod)
        ASN1InputStream(ByteArrayInputStream(asn1Data)).use { asn1InputStream ->
            val asn1Object = asn1InputStream.readObject()

            if (asn1Object !is DLApplicationSpecific) {
                return null
            }

            val content = ASN1Sequence.getInstance(asn1Object.getObject())

            // Navigate to the issuer
            val issuer = (content.getObjectAt(1) as? ASN1TaggedObject)?.let {
                ASN1Sequence.getInstance(it.getObject())
            } ?: run {
                return null
            }

            // Find the RDNSequence
            val rdnSequence = ASN1Sequence.getInstance(issuer)

            // Find the last element in RDNSequence, which should be a SET
            val lastSet =
                (rdnSequence.getObjectAt(rdnSequence.size() - 1) as? ASN1TaggedObject)?.let {
                    ASN1Set.getInstance(it.getObject())
                } ?: rdnSequence.getObjectAt(rdnSequence.size() - 1) as? ASN1Set ?: run {
                    return null
                }

            // Get the first element in the SET, which should be a SEQUENCE
            val firstElement = lastSet.getObjectAt(0) as? ASN1Sequence ?: run {
                ErrorHandler.logError("Failed to retrieve First SEQUENCE in SET", "")
                return null
            }

            // Find the pre-last item in the SEQUENCE
            val preLastItem =
                firstElement.getObjectAt(firstElement.size() - 2) as? ASN1Sequence ?: run {
                    return null
                }

            // Find the SEQUENCE with 3 elements and get the last element, which should be an INTEGER
            val targetSequence =
                preLastItem.getObjectAt(preLastItem.size() - 1) as? ASN1Sequence ?: run {
                    return null
                }

            val targetInteger =
                (targetSequence.getObjectAt(targetSequence.size() - 1) as? ASN1TaggedObject)?.let {
                    ASN1Integer.getInstance(it.getObject())
                } ?: ASN1Integer.getInstance(targetSequence.getObjectAt(targetSequence.size() - 1))

            return when (targetInteger.value.toInt()) {
                64 -> CircuitSaltType.S64
                48 -> CircuitSaltType.S48
                32 -> CircuitSaltType.S32
                else -> null
            }
        }
    }

    fun getRegisterIdentityLightCircuitName(): String {
        val circuitName = "registerIdentityLight"

        val sod = getSodFile()


        val digestAlgorithm = sod.digestAlgorithm
        val passportHashType = CircuitPassportHashType.fromValue(digestAlgorithm)
            ?: throw IllegalArgumentException("Invalid digest algorithm")

        return circuitName + passportHashType.getId()
    }

    private fun getPublicKeyExponent(publicKey: PublicKey?): CircuitExponentType? {
        val exponent = CryptoUtilsPassport.getExponentFromPublicKey(publicKey) ?: return null
        val exponentBN = BigInteger(exponent)
        return when (exponentBN) {
            BigInteger.valueOf(3L) -> CircuitExponentType.E3
            BigInteger.valueOf(65537L) -> CircuitExponentType.E65537
            else -> null
        }
    }

    private fun getPublicKeyCurve(publicKey: ECPublicKey?): CircuitCurveType? {
        if (publicKey == null) return null
        val curve = CryptoUtilsPassport.getCurveOidFromPublicKey(publicKey)
        val res = when (curve.lowercase()) {
            "secp256r1" -> CircuitCurveType.SECP256R1       // secp256r1
            "brainpoolp256r1" -> CircuitCurveType.BRAINPOOLP256  // brainpoolP256r1
            "brainpoolp320r1" -> CircuitCurveType.BRAINPOOL320R1 // brainpoolP320r1
            "secp192r1" -> CircuitCurveType.SECP192R1       // secp192r1
            "brainpoolp384r1" -> CircuitCurveType.BRAINPOOLP384R1
            "secp224r1" -> CircuitCurveType.SECP224R1 // secp224r
            "prime256v1" -> CircuitCurveType.PRIME256V1
            "prime256v2" -> CircuitCurveType.PRIME256V1 // prime256v2 --> prime256v1 with seed
            "brainpoolp512r1" -> CircuitCurveType.BRAINPOOLP512R1
            else -> throw IllegalArgumentException("Unsupported curve: " + curve.lowercase())
        }

        return res
    }

    private fun getChunkNumber(data: ByteArray, chunkSize: UInt): UInt {
        val length = data.size.toUInt() * 8u + 1u + 64u
        return length / chunkSize + if (length % chunkSize == 0u) 0u else 1u
    }

    private fun getStandardizedDocumentType(documentCode: String): String {

        val normalizedDocumentType = documentCode
            .replace("<", "")
            .replace("O", "")

        return when (normalizedDocumentType) {
            "P" -> "TD3" // Passport
            "ID" -> "TD1" // Identity Card
            else -> "TD3" // Default to TD3
        }
    }

}

object CryptoUtilsPassport {

    fun getDataFromPublicKey(publicKey: PublicKey?): ByteArray? {
        return when (publicKey) {
            is RSAPublicKey -> getModulusFromRSAPublicKey(publicKey)
            is ECPublicKey -> getXYFromECDSAPublicKey(publicKey)
            else -> null
        }
    }

    fun getPublicKeySize(publicKey: PublicKey?): Int {
        return when (publicKey) {
            is RSAPublicKey -> publicKey.modulus.bitLength()
            is org.bouncycastle.jce.interfaces.ECPublicKey -> publicKey.q.curve.fieldSize
            else -> 0
        }
    }

    fun getModulusFromRSAPublicKey(publicKey: PublicKey?): ByteArray? {
        return if (publicKey is RSAPublicKey) {
            val pubKeyModulus = publicKey.modulus.toByteArray()
            return if (pubKeyModulus.isNotEmpty() && pubKeyModulus[0] == 0x00.toByte()) {
                // Return a new array without the first byte
                pubKeyModulus.copyOfRange(1, pubKeyModulus.size)
            } else {
                pubKeyModulus
            }

        } else {
            null
        }
    }

    fun getExponentFromPublicKey(publicKey: PublicKey?): ByteArray? {
        return if (publicKey is RSAPublicKey) {
            publicKey.publicExponent.toByteArray()
        } else {
            null
        }
    }

    /**
     * Extracts the X and Y coordinates from an ECDSA public key and concatenates them.
     *
     * @param publicKey The ECDSA public key.
     * @return A ByteArray containing the concatenated X and Y coordinates, or null if extraction fails.
     */
    fun getXYFromECDSAPublicKey(publicKey: PublicKey?): ByteArray? {

        if (publicKey == null) return null

        try {

            // Cast the PublicKey to BouncyCastle's ECPublicKey interface
            val ecPublicKey =
                publicKey as? org.bouncycastle.jce.interfaces.ECPublicKey ?: return null

            // Get the EC point (Q) and normalize it
            val q: ECPoint = ecPublicKey.q.normalize()

            // Extract the affine X and Y coordinates as BigIntegers
            val x = q.affineXCoord.toBigInteger()
            val y = q.affineYCoord.toBigInteger()

            // Determine the byte length based on the curve's field size
            val curve = ecPublicKey.parameters.curve
            val fieldSize = (curve.fieldSize + 7) / 8 // Corrected line

            // Convert BigIntegers to byte arrays
            var xBytes = x.toByteArray()
            var yBytes = y.toByteArray()

            // Ensure the byte arrays are exactly fieldSize bytes
            xBytes = adjustToFixedLength(xBytes, fieldSize)
            yBytes = adjustToFixedLength(yBytes, fieldSize)

            // Concatenate X and Y bytes
            return xBytes + yBytes
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Adjusts a byte array to a fixed length by removing a leading zero byte if present
     * or padding with leading zeros if necessary.
     *
     * @param bytes The original byte array.
     * @param length The desired fixed length.
     * @return A byte array of the specified fixed length.
     */
    private fun adjustToFixedLength(bytes: ByteArray, length: Int): ByteArray {
        return when {
            bytes.size == length -> bytes
            bytes.size == length + 1 && bytes[0] == 0.toByte() -> bytes.copyOfRange(1, bytes.size)
            bytes.size < length -> ByteArray(length - bytes.size) + bytes
            else -> bytes.takeLast(length).toByteArray() // Truncate if longer
        }
    }

    fun ByteArray.padLeft(targetSize: Int): ByteArray {
        if (this.size >= targetSize) return this
        val padding = ByteArray(targetSize - this.size) { 0x00 }
        return padding + this
    }

    fun getCurveOidFromPublicKey(publicKey: ECPublicKey): String {
        return deriveCurveName(publicKey)
    }
}