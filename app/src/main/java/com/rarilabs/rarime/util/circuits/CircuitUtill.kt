package com.rarilabs.rarime.util.circuits

import com.rarilabs.rarime.util.toBits
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1Sequence
import java.math.BigInteger
import java.nio.ByteBuffer

object CircuitUtil {

    const val smartChunking2BlockSize: Long = 512

    fun smartChunking(x: BigInteger, chunksNumber: Int): List<BigInteger> {
        var value = x
        val result = mutableListOf<BigInteger>()
        val mod = BigInteger.ONE.shiftLeft(64)

        repeat(chunksNumber) {
            val chunk = value.mod(mod)
            result.add(chunk)
            value = value.divide(mod)
        }
        return result
    }

    fun smartChunking2(bytes: ByteArray, blockNumber: Long, smartChunking2BlockSize: Long): List<Long> {
        val bits = bytes.toBits()

        val dataBitsNumber = (bits.size + 1 + 64).toLong()
        val dataBlockNumber = (dataBitsNumber / smartChunking2BlockSize) + 1
        val zeroDataBitsNumber = dataBlockNumber * smartChunking2BlockSize - dataBitsNumber

        val result = mutableListOf<Long>()
        result.addAll(bits.map { it })
        result.add(1)

        repeat(zeroDataBitsNumber.toInt()) {
            result.add(0)
        }

        val bitsCount = bits.size.toLong()
        val bitsNumberBytes = ByteBuffer.allocate(8).putLong(bitsCount).array()
        val bitsNumber = bitsNumberBytes.toBits()
        result.addAll(bitsNumber.map { it })

        if (dataBlockNumber >= blockNumber) {
            return result
        }

        val restBlocksNumber = blockNumber - dataBlockNumber
        repeat((restBlocksNumber * smartChunking2BlockSize).toInt()) {
            result.add(0)
        }

        return result
    }

    fun calculateSmartChunkingNumber(bytesNumber: Int): Int {
        return if (bytesNumber == 2048) 32 else 64
    }

    fun parseECDSASignature(signatureEcdsa: ByteArray): ByteArray? {
        try {
            val asn1InputStream = ASN1InputStream(signatureEcdsa)
            val asn1Sequence = asn1InputStream.readObject() as ASN1Sequence

            val r = (asn1Sequence.getObjectAt(0) as ASN1Integer).positiveValue.toByteArray()
            val s = (asn1Sequence.getObjectAt(1) as ASN1Integer).positiveValue.toByteArray()

            // Normalize r and s to 32 bytes
            val normalizedR = normalizeTo32Bytes(r)
            val normalizedS = normalizeTo32Bytes(s)

            return normalizedR + normalizedS
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // Helper function to normalize a byte array to exactly 32 bytes
    private fun normalizeTo32Bytes(input: ByteArray): ByteArray {
        return when {
            input.size > 32 -> input.copyOfRange(input.size - 32, input.size) // Truncate leading bytes
            input.size < 32 -> ByteArray(32 - input.size) + input // Pad with leading zeros
            else -> input // Already 32 bytes
        }
    }

}