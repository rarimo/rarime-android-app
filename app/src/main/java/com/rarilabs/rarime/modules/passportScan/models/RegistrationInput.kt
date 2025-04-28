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

data class PlonkRegistrationInputs(
    val dg1: List<BigInteger>,
    val dg15: List<BigInteger>,
    val ec: List<BigInteger>,
    val sa: List<BigInteger>,
    val pk: List<BigInteger>,
    val reduction: BigInteger,
    val sig: List<BigInteger>,
    val sk_identity: String,
    val icao_root: String,
    val inclusion_branches: List<String>
)