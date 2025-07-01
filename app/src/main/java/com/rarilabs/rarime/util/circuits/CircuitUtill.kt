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

    fun splitEmptyData(data: ByteArray): List<BigInteger> {
        val nBits = data.size * 8
        var chunkNumber = nBits / 120
        if (nBits % 120 != 0) chunkNumber += 1

        // BN(0) in Swift → BigInteger.ZERO in Kotlin
        return smartBNToArray120(120, chunkNumber, BigInteger.ZERO)
    }

    fun smartChunking2(
        bytes: ByteArray,
        blockNumber: Long,
        smartChunking2BlockSize: Long
    ): List<Long> {
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
            input.size > 32 -> input.copyOfRange(
                input.size - 32,
                input.size
            ) // Truncate leading bytes
            input.size < 32 -> ByteArray(32 - input.size) + input // Pad with leading zeros
            else -> input // Already 32 bytes
        }
    }


    fun bigIntToChunkingArray(nBits: Int, numChunks: Int, x: BigInteger): List<BigInteger> {
        val mod = BigInteger.ONE.shiftLeft(nBits)
        val out = mutableListOf<BigInteger>()
        var value = x
        repeat(numChunks) {
            out.add(value.mod(mod))
            value = value.shiftRight(nBits)
        }
        return out
    }

    fun computeBarrettReduction(nBits: Int, modulus: BigInteger): BigInteger {
        return BigInteger.ONE.shiftLeft(2 * nBits).divide(modulus)
    }

    fun noirChunkNumber(dataBits: Int, chunkSize: Int): Int {
        val length = dataBits + 1 + 64
        return (length + chunkSize - 1) / chunkSize
    }

    fun splitBy120Bits(data: ByteArray): List<BigInteger> {
        val bitLength = data.size * 8
        var chunkNumber = bitLength / 120
        if (bitLength % 120 != 0) chunkNumber += 1

        return smartBNToArray120(120, chunkNumber, BigInteger(1, data))
    }

    fun rsaBarrettReductionParam(n: BigInteger, nBits: Int): List<BigInteger> {
        var chunkNumber = nBits / 120
        if (nBits % 120 != 0) chunkNumber += 1

        val exp = (nBits + 2) * 2
        val baseX = BigInteger.valueOf(2L).pow(exp)
        val result = baseX.divide(n)

        return smartBNToArray120(120, chunkNumber, result)
    }

    /**
     * Splits x into k values, each n bits wide, returned as BigIntegers.
     */
    fun smartBNToArray120(n: Int, k: Int, x: BigInteger): List<BigInteger> {
        val mod = BigInteger.ONE.shiftLeft(n) // 2^n
        val result = mutableListOf<BigInteger>()
        var mutX = x
        repeat(k) {
            result.add(mutX.mod(mod))
            mutX = mutX.divide(mod)
        }
        return result
    }

}