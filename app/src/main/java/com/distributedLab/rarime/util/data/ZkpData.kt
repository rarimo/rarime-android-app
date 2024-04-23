package com.distributedLab.rarime.util.data

import android.os.Parcelable
import com.google.gson.Gson


data class Proof(
    val pi_a: List<String>,
    val pi_b: List<List<String>>,
    val pi_c: List<String>,
    val protocol: String,
    var curve: String = "bn128"
) {
    companion object {
        fun fromJson(jsonString: String): Proof {
            val json = Gson().fromJson(jsonString, Proof::class.java)
            return json
        }

        fun getDefaultCurve(): String {
            return "bn128"
        }
    }

}
data class ZkProof(
    val proof: Proof,
    val pub_signals: List<String>
)