package com.rarilabs.rarime.util

import org.jmrtd.lds.icao.DG1File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.PublicKey
import java.util.Base64

fun String.decodeHexString(): ByteArray {
    check(length % 2 == 0) {
        "Must have an even length"
    }

    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

fun String.reversedInt(): String {
    var result = ""
    for (byte in this.toByteArray()) {
        val bitsStr = byte.toString(2) // Convert byte to binary string
        val bitsStrReversed = bitsStr.reversed().drop(1) // Reverse and remove leading "0"
        result = bitsStrReversed + result
    }
    return (result.toInt(2)).toString() // Convert binary string to decimal and convert to string
}

fun String.reversedIntPreImage(): String {
    val rawInt = this.toIntOrNull(10) ?: 0
    var result = ""
    for (byte in rawInt.toByteArr().dropLast(1)) {
        val bitsStr = byte.toString(2)
        val bitsStrReversed = bitsStr.reversed().drop(1)
        result = bitsStrReversed + result
    }
    val intPreImageRepr = result.toIntOrNull(2) ?: 0
    return intPreImageRepr.toByteArr().toString(Charsets.UTF_8) ?: ""
}

fun Byte.toString(radix: Int): String = Integer.toString(this.toInt(), radix)

fun Int.toByteArr(): ByteArray = ByteArray(4) { (this shr (it * 8) and 0xFF).toByte() }


fun String.toBitArray(): String {
    val stringBuilder = StringBuilder()

    for (char in this) {
        val binaryRepresentation = char.toInt().toString(2)
        // Ensure each character is represented by 8 bits by padding zeros if necessary
        val paddedBinary = binaryRepresentation.padStart(8, '0')
        stringBuilder.append(paddedBinary)
    }

    return stringBuilder.toString()
}

fun PublicKey.publicKeyToPem(): String {
    val base64PubKey = Base64.getEncoder().encodeToString(this.encoded)

    return "-----BEGIN PUBLIC KEY-----\n" +
            base64PubKey.replace("(.{64})".toRegex(), "$1\n") +
            "\n-----END PUBLIC KEY-----\n"
}


fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

fun ByteArray.toBase64(): String =
    String(Base64.getEncoder().encode(this))

fun String.fromBase64ToByteArray(): ByteArray {
    return Base64.getDecoder().decode(this)
}

fun DG1File.encodedHash(algorithm: String): ByteArray {
    val messageDigest = MessageDigest.getInstance(algorithm, "BC")
    return messageDigest.digest(this.encoded)
}

fun Dg15FileOwn.encodedHash(algorithm: String): ByteArray {
    val messageDigest = MessageDigest.getInstance(algorithm, "BC")
    return messageDigest.digest(this.encoded)
}

fun ByteArray.toUInt(): UInt {
    return java.nio.ByteBuffer.wrap(this).int.toUInt()
}

fun ByteArray.toBits(): List<Long> {
    val bits = mutableListOf<Long>()
    for (byte in this) {
        for (i in 0 until 8) {
            bits.add(((byte.toInt() shr (7 - i)) and 1).toLong())
        }
    }
    return bits
}


fun BigInteger.toNormalizedByteArray(capacity: Int = 0): ByteArray {
    // Convert the BigInteger to a byte array
    var result = this.toByteArray()

    // Remove leading zero byte if present (caused by sign bit in two's complement representation)
    if (result.size > 1 && result[0] == 0.toByte()) {
        result = result.copyOfRange(1, result.size)
    }

    // Pad with leading zeros to match the desired capacity
    if (capacity != 0 && capacity > result.size) {
        val padding = ByteArray(capacity - result.size) { 0 }
        result = padding + result
    }

    return result
}