package com.rarilabs.rarime.data

import java.math.BigInteger


data class SMTProof(
    val root: ByteArray, // You might need a custom type adapter for this
    val siblings: List<ByteArray> // List of ByteArray
)


data class PassportInfo(
    val activeIdentity: ByteArray,
    val identityReissueCounter: BigInteger
)

data class IdentityInfo(
    val activePassport: ByteArray,
    val issueTimestamp: BigInteger
)