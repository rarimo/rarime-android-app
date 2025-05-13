package com.rarilabs.rarime.util.data

import com.google.gson.Gson

private val gson = Gson() // 1 раз на весь файл

interface ZkProof {
    fun getProofString(): String
    fun getPubSignals(): List<String>
    fun preparingForSaving(): String

    companion object {
        fun getProofFromSavedData(data: String): ZkProof {
            return runCatching { gson.fromJson(data, GrothProof::class.java) }
                .getOrNull()
                ?.takeIf { it.proof != null && it.pub_signals != null }
                ?: runCatching { gson.fromJson(data, PlonkProof::class.java) }
                    .getOrNull()
                    ?.takeIf { it.proof != null && it.pub_signals != null }
                ?: throw IllegalArgumentException("Unknown proof format")
        }
    }
}

data class Proof(
    val pi_a: List<String>,
    val pi_b: List<List<String>>,
    val pi_c: List<String>,
    val protocol: String,
) {
    companion object {
        fun fromJson(jsonString: String): Proof {
            return gson.fromJson(jsonString, Proof::class.java)
        }
    }
}

data class PlonkProof(
    val proof: String,
    val pub_signals: List<String>,
) : ZkProof {
    override fun getProofString(): String = proof

    override fun getPubSignals(): List<String> = pub_signals

    override fun preparingForSaving(): String = gson.toJson(this)
}

data class GrothProof(
    val proof: Proof,
    val pub_signals: List<String>
) : ZkProof {
    override fun getProofString(): String = gson.toJson(proof)

    override fun getPubSignals(): List<String> = pub_signals

    override fun preparingForSaving(): String = gson.toJson(this)
}