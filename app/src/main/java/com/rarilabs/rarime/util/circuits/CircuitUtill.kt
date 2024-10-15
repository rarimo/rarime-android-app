package com.rarilabs.rarime.util.circuits

import com.rarilabs.rarime.util.toBits
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

}