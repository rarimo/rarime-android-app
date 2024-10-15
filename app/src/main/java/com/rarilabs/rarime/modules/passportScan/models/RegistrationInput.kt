package com.rarilabs.rarime.modules.passportScan.models

import java.math.BigInteger

data class RegisterIdentityInputs(
    val skIdentity: String,
    val encapsulatedContent: List<Long>,
    val signedAttributes: List<Long>,
    val pubkey: List<BigInteger>,
    val signature: List<BigInteger>,
    val dg1: List<Long>,
    val dg15: List<Long>,
    val slaveMerkleRoot: String,
    val slaveMerkleInclusionBranches: List<String>
)