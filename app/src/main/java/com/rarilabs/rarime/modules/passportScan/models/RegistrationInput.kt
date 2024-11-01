package com.rarilabs.rarime.modules.passportScan.models

import java.math.BigInteger

data class RegisterIdentityInputs(
    val signature: List<String>,
    val signedAttributes: List<Long>,
    val dg1: List<Long>,
    val pubkey: List<String>,
    val slaveMerkleRoot: String,
    val skIdentity: String,
    val dg15: List<Long>,
    val encapsulatedContent: List<Long>,
    val slaveMerkleInclusionBranches: List<String>
)