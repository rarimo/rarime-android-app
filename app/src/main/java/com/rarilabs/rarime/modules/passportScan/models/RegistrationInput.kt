package com.rarilabs.rarime.modules.passportScan.models

import com.google.gson.Gson
import org.web3j.utils.Numeric
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
    val dg1: List<String>,
    val dg15: List<String>,
    val ec: List<String>,
    val icao_root: String,
    val inclusion_branches: List<String>,
    val pk: List<String>,
    val reduction_pk: List<String>,
    val sa: List<String>,
    val sig: List<String>,
    val sk_identity: String,
) {
    companion object {
        fun getMocked(): PlonkRegistrationInputs {

            val rawInputs = """
                
            """.trimIndent()

            val a = Gson().fromJson(rawInputs, PlonkRegistrationInputs::class.java)

            val inputs = PlonkRegistrationInputs(
                a.dg1.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.dg15.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.ec.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.icao_root.let { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.inclusion_branches.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.sa.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.pk.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.reduction_pk.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.sig.map { Numeric.toHexString(BigInteger(it).toByteArray()) },
                a.sk_identity.let { Numeric.toHexString(BigInteger(it).toByteArray()) },
            )

            return inputs
        }
    }
}

data class TestProofInputs(
    val proof: List<String>,
    val verification_key: List<String>,
    val key_hash: String,
    val public_inputs: List<String>
) {
    companion object {
        fun getMockedTestProof(): TestProofInputs {
            val json = """""".trimIndent()

            return Gson().fromJson(json, TestProofInputs::class.java)
        }
    }
}