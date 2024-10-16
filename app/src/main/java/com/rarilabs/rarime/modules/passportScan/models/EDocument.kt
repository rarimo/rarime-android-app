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
import CircuitSignatureType
import RegisterIdentityCircuitType
import android.util.Log
import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.Dg15FileOwn
import com.rarilabs.rarime.util.circuits.SODAlgorithm
import com.rarilabs.rarime.util.decodeHexString
import findSubarrayIndex
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.cms.Attribute
import org.bouncycastle.asn1.cms.CMSAttributes
import org.bouncycastle.cms.CMSSignedData
import org.bouncycastle.cms.SignerInformation
import org.jmrtd.lds.icao.DG1File
import org.web3j.utils.Numeric
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

    fun getDg15File(): Dg15FileOwn {
        val dG15File = this.dg15!!.decodeHexString().inputStream()
        return Dg15FileOwn(dG15File)
    }

    fun getPublicKeySize(publicKey: PublicKey?): Int {
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


    fun getMessageDigestFromSignedAttributes(cmsSignedData: CMSSignedData): ByteArray {
        val signerInfos = cmsSignedData.signerInfos
        val signerInfo = signerInfos.signers.iterator().next() as SignerInformation
        val signedAttributes = signerInfo.signedAttributes
            ?: throw Exception("Signed attributes not found")

        val messageDigestAttr: Attribute = signedAttributes.get(CMSAttributes.messageDigest)
            ?: throw Exception("MessageDigest attribute not found")

        val digestValue = messageDigestAttr.attributeValues[0] as ASN1OctetString
        return digestValue.octets
    }

    fun getRegisterIdentityCircuitType(): RegisterIdentityCircuitType {
        try {
            // Initialize DataGroup1 and SOD using jMRTD
            val dg1Group = getDg1File()
            val sodFile = getSodFile()

            // Get the signature algorithm from SOD
            val sodSignatureAlgorithmName = sodFile.digestEncryptionAlgorithm
            val sodSignatureAlgorithm = SODAlgorithm.fromValue(sodSignatureAlgorithmName)
                ?: throw IllegalStateException("SOD algorithm not found")

            // Get the public key from SOD and determine its size
            val sodPublicKey = sodFile.docSigningCertificate.publicKey
            val publicKeySize = getPublicKeySupportedSize(getPublicKeySize(sodPublicKey))
                ?: throw IllegalStateException("Public key size not found")

            // Create the CircuitSignatureType
            val signatureType = CircuitSignatureType(
                staticId = 0u,
                algorithm = sodSignatureAlgorithm.getCircuitSignatureAlgorithm(),
                keySize = publicKeySize,
                exponent = getPublicKeyExponent(sodPublicKey),
                salt = null,
                curve = getPublicKeyCurve(sodPublicKey),
                hashAlgorithm = sodSignatureAlgorithm.getCircuitSignatureHashAlgorithm()
            )

            // Get the passport hash type
            val digestAlgorithm = sodFile.digestAlgorithm
            val passportHashType = CircuitPassportHashType.fromValue(digestAlgorithm)
                ?: throw IllegalArgumentException("Invalid digest algorithm")

            // Get the document type
            val documentTypeString = getStandardizedDocumentType(dg1Group.mrzInfo.documentCode)
            val documentType = CircuitDocumentType.fromValue(documentTypeString)
                ?: throw IllegalArgumentException("Invalid document type")

            // **Extract CMS Signed Data from SOD**
            //val cmsSignedData = extractCMSData(sodFile.encoded)

            // **Extract encapsulated content and signed attributes**
            val signedAttributes = sodFile.eContent
            val encapsulatedContent = sodFile.readASN1Data().decodeHexString()

            val ecHash = MessageDigest.getInstance(digestAlgorithm).digest(encapsulatedContent)

            // Calculate chunk numbers
            val ecChunkNumber = getChunkNumber(encapsulatedContent, passportHashType.getChunkSize())

            // Find digest positions
            val ecDigestPosition = signedAttributes.findSubarrayIndex(ecHash)
                ?: throw Exception("Unable to find EC digest position")

            val dg1Hash = dg1Group.encodedHash(passportHashType.value.uppercase())
            val dg1DigestPositionShift = encapsulatedContent.findSubarrayIndex(dg1Hash)
                ?: throw Exception("Unable to find DG1 digest position")

            // Initialize the circuit type
            val circuitType = RegisterIdentityCircuitType(
                signatureType = signatureType,
                passportHashType = passportHashType,
                documentType = documentType,
                ecChunkNumber = ecChunkNumber,
                ecDigestPosition = ecDigestPosition.toUInt() * 8u,
                dg1DigestPositionShift = dg1DigestPositionShift.toUInt() * 8u,
                aaType = null
            )

            // Process DG15 if available
            if (!dg15.isNullOrEmpty()) {
                val dg15Wrapper = getDg15File()

                val dg15Hash = dg15Wrapper.encodedHash(passportHashType.value.uppercase())

                val dg15DigestPositionShift = encapsulatedContent.findSubarrayIndex(dg15Hash)
                    ?: throw Exception("Unable to find DG15 digest position")

                val dg15ChunkNumber =
                    getChunkNumber(dg15!!.decodeHexString(), passportHashType.getChunkSize())

                val pubkeyData: ByteArray
                val aaAlgorithm: CircuitAlgorithmType
                var aaKeySize: CircuitKeySizeType? = null
                var aaExponent: CircuitExponentType? = null
                var aaCurve: CircuitCurveType? = null

                val publicKey = dg15Wrapper.publicKey

                if (publicKey.algorithm.equals("RSA", ignoreCase = true)) {
                    pubkeyData =
                        CryptoUtilsPassport.getModulusFromRSAPublicKey(publicKey) ?: ByteArray(0)
                    aaAlgorithm = CircuitAlgorithmType.RSA

                    aaKeySize =
                        getPublicKeySupportedSize(CryptoUtilsPassport.getPublicKeySize(publicKey))
                    aaExponent = getPublicKeyExponent(publicKey)
                } else if (publicKey.algorithm.equals("EC", ignoreCase = true)) {
                    pubkeyData =
                        CryptoUtilsPassport.getXYFromECDSAPublicKey(publicKey) ?: ByteArray(0)
                    aaAlgorithm = CircuitAlgorithmType.ECDSA

                    aaCurve = getPublicKeyCurve(publicKey)
                } else {
                    throw Exception("Unable to find public key")
                }

                Log.i("PubKeyData", Numeric.toHexStringNoPrefix(pubkeyData))
                Log.i("dg15", Numeric.toHexStringNoPrefix(dg15Wrapper.encoded))

                val aaKeyPositionShift = dg15Wrapper.encoded.findSubarrayIndex(pubkeyData)
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
            1024 -> CircuitKeySizeType.B1024
            2048 -> CircuitKeySizeType.B2048
            4096 -> CircuitKeySizeType.B4096
            256 -> CircuitKeySizeType.B256
            320 -> CircuitKeySizeType.B320
            192 -> CircuitKeySizeType.B192
            else -> null
        }
    }

    private fun getPublicKeyExponent(publicKey: PublicKey?): CircuitExponentType? {
        val exponent = CryptoUtilsPassport.getExponentFromPublicKey(publicKey) ?: return null
        val exponentBN = BigInteger(exponent)
        return when {
            exponentBN == BigInteger.valueOf(3L) -> CircuitExponentType.E3
            exponentBN == BigInteger.valueOf(65537L) -> CircuitExponentType.E65537
            else -> null
        }
    }

    private fun getPublicKeyCurve(publicKey: PublicKey?): CircuitCurveType? {
        val curve = CryptoUtilsPassport.getCurveFromECDSAPublicKey(publicKey) ?: return null
        return when (curve.lowercase()) {
            "secp256r1" -> CircuitCurveType.SECP256R1
            "brainpoolp256r1" -> CircuitCurveType.BRAINPOOLP256
            "brainpoolp320r1" -> CircuitCurveType.BRAINPOOL320R1
            "secp192r1" -> CircuitCurveType.SECP192R1
            else -> null
        }
    }

    private fun getChunkNumber(data: ByteArray, chunkSize: UInt): UInt {
        val length = data.size.toUInt() * 8u + 1u + 64u
        return length / chunkSize + if (length % chunkSize == 0u) 0u else 1u
    }

    private fun getStandardizedDocumentType(documentCode: String): String {
        return when (documentCode) {
            "P" -> "TD3" // Passport
            "ID" -> "TD1" // Identity Card
            else -> "TD1" // Default to TD1
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
            is java.security.interfaces.RSAPublicKey -> publicKey.modulus.bitLength()
            is org.bouncycastle.jce.interfaces.ECPublicKey -> publicKey.q.curve.fieldSize
            else -> 0
        }
    }

    fun getModulusFromRSAPublicKey(publicKey: PublicKey?): ByteArray? {
        return if (publicKey is java.security.interfaces.RSAPublicKey) {

            val pubKeyHex = Numeric.toHexStringNoPrefix(publicKey.modulus.toByteArray())

            if (pubKeyHex.startsWith("00")) {
                return Numeric.hexStringToByteArray(pubKeyHex.split("00")[1])
            } else {
                return Numeric.hexStringToByteArray(pubKeyHex)
            }

        } else {
            null
        }
    }

    fun getExponentFromPublicKey(publicKey: PublicKey?): ByteArray? {
        return if (publicKey is java.security.interfaces.RSAPublicKey) {
            publicKey.publicExponent.toByteArray()
        } else {
            null
        }
    }

    fun getXYFromECDSAPublicKey(publicKey: PublicKey?): ByteArray? {
        return if (publicKey is org.bouncycastle.jce.interfaces.ECPublicKey) {
            val q = publicKey.q.normalize()
            val x = q.affineXCoord.encoded
            val y = q.affineYCoord.encoded
            x + y
        } else {
            null
        }
    }

    fun getCurveFromECDSAPublicKey(publicKey: PublicKey?): String? {
        return if (publicKey is org.bouncycastle.jce.interfaces.ECPublicKey) {
            val curveName = publicKey.parameters.toString()
            curveName
        } else {
            null
        }
    }
}

// Extension function to compute hash
fun DG1File.encodedHash(algorithm: String): ByteArray {
    val messageDigest = MessageDigest.getInstance(algorithm, "BC")
    return messageDigest.digest(this.encoded)
}

fun Dg15FileOwn.encodedHash(algorithm: String): ByteArray {
    val messageDigest = MessageDigest.getInstance(algorithm, "BC")
    return messageDigest.digest(this.encoded)
}

// Extension function to find subarray index
fun ByteArray.findSubarrayIndex(subarray: ByteArray): Int? {
    for (i in indices) {
        if (i + subarray.size <= size && copyOfRange(
                i,
                i + subarray.size
            ).contentEquals(subarray)
        ) {
            return i
        }
    }
    return null
}
