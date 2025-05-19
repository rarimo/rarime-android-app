package com.rarilabs.rarime.util

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter

fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
    val hmac = HMac(SHA256Digest())
    hmac.init(KeyParameter(key))
    hmac.update(data, 0, data.size)

    val result = ByteArray(hmac.macSize)
    hmac.doFinal(result, 0)

    return result
}