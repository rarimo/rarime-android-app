package com.rarilabs.rarime.util.data

import com.google.gson.Gson
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import com.rarilabs.rarime.util.data.UniversalProofFactory.fromLight
import org.web3j.utils.Numeric
import java.math.BigInteger


sealed class UniversalProof {
    abstract fun getPubSignals(): List<String>
    abstract fun getIdentityKey(): String
    abstract fun getPublicKey(): String
    abstract fun getPassportHash(): String
    abstract fun getProofJson(): String

    data class Groth internal constructor(val proof: GrothProof) : UniversalProof() {
        override fun getPubSignals() = proof.pub_signals
        override fun getIdentityKey() = proof.pub_signals[3]
        override fun getPublicKey() = proof.pub_signals[0]
        override fun getPassportHash() = proof.pub_signals[1]
        override fun getProofJson(): String {
            return Gson().toJson(proof)
        }
    }

    data class Light internal constructor(
        val proof: LightRegistrationData, val grothProof: GrothProof
    ) : UniversalProof() {
        override fun getPubSignals() = grothProof.pub_signals

        override fun getIdentityKey() = grothProof.pub_signals[2]
        override fun getPublicKey() = proof.public_key
        override fun getPassportHash() = proof.passport_hash

        override fun getProofJson(): String {
            return Gson().toJson(grothProof)
        }
    }

    data class Plonk internal constructor(val proof: PlonkProof) : UniversalProof() {
        override fun getPubSignals() = proof.pub_signals
        override fun getIdentityKey() = proof.pub_signals[0]
        override fun getPublicKey() = proof.pub_signals[0]
        override fun getPassportHash() = proof.pub_signals[1]
        override fun getProofJson(): String {
            return Gson().toJson(proof)
        }
    }

    companion object {
        fun fromGroth(grothProof: GrothProof): Groth = Groth(grothProof)
        fun fromPlonk(plonkProof: PlonkProof): Plonk = Plonk(plonkProof)
        fun fromLight(light: LightRegistrationData, groth: GrothProof): Light = Light(light, groth)


    }
}


object UniversalProofFactory {
    /**
     * Try to parse Groth or Plonk from raw JSON.
     * (LightProofs must be constructed via [fromLight] explicitly.)
     */

    private val gson = Gson()
    fun fromRaw(raw: String): UniversalProof? {
        return try {
            val groth = gson.fromJson(raw, GrothProof::class.java)
            UniversalProof.fromGroth(groth)
        } catch (_: Exception) {
            try {
                val plonk = gson.fromJson(raw, PlonkProof::class.java)
                UniversalProof.fromPlonk(plonk)
            } catch (_: Exception) {
                null
            }
        }
    }

    fun fromGroth(groth: GrothProof): UniversalProof.Groth = UniversalProof.fromGroth(groth)

    fun fromPlonkBytes(plonkRaw: ByteArray): UniversalProof.Plonk =
        UniversalProof.fromPlonk(PlonkProof.fromByteArray(plonkRaw))

    fun fromLight(light: LightRegistrationData, groth: GrothProof): UniversalProof.Light =
        UniversalProof.fromLight(light, groth)
}

// --- Proof data classes ---

data class GrothProof(
    val proof: GrothProofData, val pub_signals: List<String>
)

data class GrothProofData(
    val pi_a: List<String>,
    val pi_b: List<List<String>>,
    val pi_c: List<String>,
    val protocol: String,
) {
    companion object {
        fun fromJson(jsonString: String): GrothProofData =
            Gson().fromJson(jsonString, GrothProofData::class.java)
    }
}

data class PlonkProof(
    val rawProof: ByteArray,
    val proof: String,
    val pub_signals: List<String>,
) {
    companion object {
        fun fromByteArray(data: ByteArray): PlonkProof {
            require(data.size == 2304) { "data.size != 2304, got ${data.size}" }

            val pubSignalLen = 5
            val pubSignalData = 32
            val pubSignalSize = pubSignalLen * pubSignalData

            // Extract public signals
            val pubSignalsRaw = data.copyOfRange(0, pubSignalSize)
            val pubSignalsList = pubSignalsRaw.toList().chunked(pubSignalData)
                .map { BigInteger(it.toByteArray()).toString() }

            // Extract proof bytes (after pub signals)
            val proofBytes = data.copyOfRange(pubSignalSize, data.size)
            val proofHex = Numeric.toHexString(proofBytes)

            return PlonkProof(
                rawProof = data, proof = proofHex, pub_signals = pubSignalsList
            )
        }
    }
}


data class UniversalProofWrapper(
    val type: String,
    val groth: GrothProof? = null,
    val plonk: PlonkProof? = null,
    val light: LightProofDto? = null
)

data class LightProofDto(
    val proof: LightRegistrationData,
    val grothProof: GrothProof
)


fun UniversalProof.toWrapper(): UniversalProofWrapper = when (this) {
    is UniversalProof.Groth -> UniversalProofWrapper("Groth", groth = this.proof)
    is UniversalProof.Plonk -> UniversalProofWrapper("Plonk", plonk = this.proof)
    is UniversalProof.Light -> UniversalProofWrapper(
        "Light",
        light = LightProofDto(this.proof, this.grothProof)
    )
}

fun UniversalProofWrapper.toProof(): UniversalProof = when (type) {
    "Groth" -> UniversalProof.fromGroth(groth!!)
    "Plonk" -> UniversalProof.fromPlonk(plonk!!)
    "Light" -> UniversalProof.fromLight(light!!.proof, light.grothProof)
    else -> throw IllegalStateException("Unknown proof type: $type")
}
