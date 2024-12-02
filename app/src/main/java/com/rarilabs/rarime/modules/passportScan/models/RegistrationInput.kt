package com.rarilabs.rarime.modules.passportScan.models

data class RegisterIdentityInputs(
    val signature: List<Long>,
    val signedAttributes: List<Long>,
    val dg1: List<Long>,
    val pubkey: List<Long>,
    val slaveMerkleRoot: String,
    val skIdentity: String,
    val dg15: List<Long>,
    val encapsulatedContent: List<Long>,
    val slaveMerkleInclusionBranches: List<String>
)