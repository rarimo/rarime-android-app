package com.rarilabs.rarime.util

import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec


fun parseASN1RsaManually(data: ByteArray): RSAPublicKey {
    var offset = 0

    // Parse the top-level SEQUENCE
    if (data[offset] != 0x30.toByte()) {
        throw IllegalArgumentException("Invalid data: expected SEQUENCE at offset $offset")
    }
    offset += 1
    val (seqLength, seqLengthOffset) = readAsn1Length(data, offset)
    offset = seqLengthOffset
    val seqEnd = offset + seqLength

    // Parse AlgorithmIdentifier SEQUENCE
    if (data[offset] != 0x30.toByte()) {
        throw IllegalArgumentException("Invalid data: expected SEQUENCE at offset $offset")
    }
    offset += 1
    val (algIdLength, algIdOffset) = readAsn1Length(data, offset)
    offset = algIdOffset + algIdLength

    // Parse BIT STRING
    if (data[offset] != 0x03.toByte()) {
        throw IllegalArgumentException("Invalid data: expected BIT STRING at offset $offset")
    }
    offset += 1
    val (bitStringLength, bitStringOffset) = readAsn1Length(data, offset)
    offset = bitStringOffset

    // Skip the unused bits byte
    val unusedBits = data[offset].toInt()
    if (unusedBits != 0) {
        throw IllegalArgumentException("Unsupported unused bits: $unusedBits")
    }
    offset += 1

    // Extract the public key bytes
    val pubKeyBytes = data.copyOfRange(offset, offset + (bitStringLength - 1))
    offset = 0 // Reset offset for public key bytes

    // Parse the public key SEQUENCE
    if (pubKeyBytes[offset] != 0x30.toByte()) {
        throw IllegalArgumentException("Invalid data: expected SEQUENCE at offset $offset")
    }
    offset += 1
    val (pubKeySeqLength, pubKeySeqOffset) = readAsn1Length(pubKeyBytes, offset)
    offset = pubKeySeqOffset

    // Parse modulus INTEGER
    if (pubKeyBytes[offset] != 0x02.toByte()) {
        throw IllegalArgumentException("Invalid data: expected INTEGER (modulus) at offset $offset")
    }
    offset += 1
    val (modulusLength, modulusOffset) = readAsn1Length(pubKeyBytes, offset)
    offset = modulusOffset
    val modulusBytes = pubKeyBytes.copyOfRange(offset, offset + modulusLength)
    offset += modulusLength

    // Parse exponent INTEGER
    if (pubKeyBytes[offset] != 0x02.toByte()) {
        throw IllegalArgumentException("Invalid data: expected INTEGER (exponent) at offset $offset")
    }
    offset += 1
    val (exponentLength, exponentOffset) = readAsn1Length(pubKeyBytes, offset)
    offset = exponentOffset
    val exponentBytes = pubKeyBytes.copyOfRange(offset, offset + exponentLength)

    // Construct BigIntegers
    val modulus = BigInteger(1, modulusBytes)
    val exponent = BigInteger(1, exponentBytes)

    // Output the modulus and exponent
    println("Modulus: $modulus")
    println("Exponent (decimal): $exponent")

    return createRSAPublicKey(modulus, exponent)


}


fun readAsn1Length(data: ByteArray, offset: Int): Pair<Int, Int> {
    var idx = offset
    var length = data[idx].toInt() and 0xFF
    idx += 1
    if (length > 127) {
        val numLengthBytes = length and 0x7F
        length = 0
        for (i in 0 until numLengthBytes) {
            length = (length shl 8) or (data[idx].toInt() and 0xFF)
            idx += 1
        }
    }
    return Pair(length, idx)
}

fun createRSAPublicKey(modulus: BigInteger, exponent: BigInteger): RSAPublicKey {
    // Create an RSAPublicKeySpec with the modulus and exponent
    val publicKeySpec = RSAPublicKeySpec(modulus, exponent)

    // Get a KeyFactory for RSA
    val keyFactory = KeyFactory.getInstance("RSA")

    // Generate the public key from the key specification
    val publicKey = keyFactory.generatePublic(publicKeySpec) as RSAPublicKey

    return publicKey
}