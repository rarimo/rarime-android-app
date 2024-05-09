package com.distributedLab.rarime.contracts

import com.distributedLab.rarime.util.decodeHexString
import java.math.BigInteger
import java.nio.ByteBuffer

data class JsonRpcResponse(
    val jsonrpc: String,
    val id: Int,
    val result: String
)
data class ProofTX(
    val root: ByteArray,
    val siblings: List<ByteArray>,
    val existence: Boolean,
    val key: ByteArray,
    val value: ByteArray,
    val auxExistence: Boolean,
    val auxKey: ByteArray,
    val auxValue: ByteArray
)

fun decodeTXResp(hex: String): ByteArray {
    val hexTrimmed = hex.removePrefix("0x")
    return hexTrimmed.decodeHexString()
}

fun parseProof(data: ByteArray): ProofTX {
    val byteBuffer = ByteBuffer.wrap(data)

    // Read root
    val root = ByteArray(32)
    byteBuffer.get(root)

    // Read siblings offset
    val siblingsOffset = BigInteger(1, ByteArray(32).apply { byteBuffer.get(this) }).toInt()

    // Read siblings count
    byteBuffer.position(siblingsOffset)
    val siblingsCount = BigInteger(1, ByteArray(32).apply { byteBuffer.get(this) }).toInt()

    // Read siblings
    val siblings = mutableListOf<ByteArray>()
    repeat(siblingsCount) {
        val sibling = ByteArray(32)
        byteBuffer.get(sibling)
        siblings.add(sibling)
    }

    // Read existence flag
    val existence = BigInteger(1, ByteArray(32).apply { byteBuffer.get(this) }).toInt() != 0

    // Read key
    val key = ByteArray(32)
    byteBuffer.get(key)

    // Read value
    val value = ByteArray(32)
    byteBuffer.get(value)

    // Read auxiliary existence flag
    val auxExistence = BigInteger(1, ByteArray(32).apply { byteBuffer.get(this) }).toInt() != 0

    // Read auxiliary key
    val auxKey = ByteArray(32)
    byteBuffer.get(auxKey)

    // Read auxiliary value
    val auxValue = ByteArray(32)
    byteBuffer.get(auxValue)

    return ProofTX(root, siblings, existence, key, value, auxExistence, auxKey, auxValue)
}