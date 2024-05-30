package com.distributedLab.rarime.util.data

import com.google.gson.Gson


data class Proof(
    val pi_a: List<String>,
    val pi_b: List<List<String>>,
    val pi_c: List<String>,
    val protocol: String,
) {
    companion object {
        fun fromJson(jsonString: String): Proof {
            val json = Gson().fromJson(jsonString, Proof::class.java)
            return json
        }
    }

}

data class ZkProof(
    val proof: Proof,
    val pub_signals: List<String>
)