package com.rarilabs.rarime.util

import java.math.BigInteger


private const val fieldModulusStr =
    "21888242871839275222246405745257275088548364400416034343698204186575808495617"

fun isKeyValid(key: BigInteger, fieldModulus: BigInteger = BigInteger(fieldModulusStr)): Boolean {
    return key >= BigInteger.ZERO && key < fieldModulus
}