package com.rarilabs.rarime.util.data

import com.google.gson.Gson
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import org.web3j.utils.Numeric
import java.math.BigInteger

private val gson = Gson()


enum class RegistrationProofType {
    LIGHT_PROOF, GROTH_PROOF, PLONK_PROOF
}


class UniversalZkProof private constructor() {

    lateinit var rawData: String

    var type: RegistrationProofType = RegistrationProofType.LIGHT_PROOF


    fun getPubSignals(): List<String> {
        return when (type) {
            RegistrationProofType.LIGHT_PROOF -> {
                val res = gson.fromJson(rawData, LightRegistrationData::class.java)
                listOf(res.verifier, res.signature, res.public_key, res.passport_hash)
            }

            RegistrationProofType.GROTH_PROOF -> {
                val res = gson.fromJson(rawData, GrothProof::class.java)
                res.pub_signals
            }

            RegistrationProofType.PLONK_PROOF -> {
                val res = gson.fromJson(rawData, PlonkProof::class.java)
                res.pub_signals
            }
        }
    }

    fun getIdentityKey(): String {
        return when (type) {
            RegistrationProofType.LIGHT_PROOF -> {
                val res = gson.fromJson(rawData, LightRegistrationData::class.java)
                res.signature
            }

            RegistrationProofType.GROTH_PROOF -> {
                val res = gson.fromJson(rawData, GrothProof::class.java)
                res.pub_signals[0]
            }

            RegistrationProofType.PLONK_PROOF -> {
                val res = gson.fromJson(rawData, PlonkProof::class.java)
                res.pub_signals[0]
            }
        }
    }

    fun getPublicKey(): String {
        return when (type) {
            RegistrationProofType.LIGHT_PROOF -> {
                val res = gson.fromJson(rawData, LightRegistrationData::class.java)
                res.public_key
            }

            RegistrationProofType.GROTH_PROOF -> {
                val res = gson.fromJson(rawData, GrothProof::class.java)
                res.pub_signals[0]
            }

            RegistrationProofType.PLONK_PROOF -> {
                val res = gson.fromJson(rawData, PlonkProof::class.java)
                res.pub_signals[0]
            }
        }
    }


    fun getPassportHash(): String {
        return when (type) {
            RegistrationProofType.LIGHT_PROOF -> {
                val res = gson.fromJson(rawData, LightRegistrationData::class.java)
                res.passport_hash
            }

            RegistrationProofType.GROTH_PROOF -> {
                val res = gson.fromJson(rawData, GrothProof::class.java)
                res.pub_signals[1]
            }

            RegistrationProofType.PLONK_PROOF -> {
                val res = gson.fromJson(rawData, PlonkProof::class.java)
                res.pub_signals[1]
            }
        }
    }

    constructor(grothProof: GrothProof) : this() {
        val json = gson.toJson(grothProof)
        this.rawData = Numeric.toHexString(json.toByteArray())
        this.type = RegistrationProofType.GROTH_PROOF
    }

    //Only For plonkProofs
    constructor(rawPlonkData: ByteArray) : this() {
        val plonk = PlonkProof.fromByteArray(rawPlonkData)
        val json = gson.toJson(plonk)
        this.rawData = Numeric.toHexString(json.toByteArray())
        this.type = RegistrationProofType.PLONK_PROOF
    }

    //From shared Prefs
    constructor(rawDataHex: String) : this() {
        val rawData = Numeric.hexStringToByteArray(rawDataHex)

        val jsonString = rawData.decodeToString()

        val grothProof = tryDecodeToGroth(jsonString)

        this.rawData = jsonString

        if (grothProof != null) {
            type = RegistrationProofType.GROTH_PROOF
            return
        }

        val plonkProof = tryDecodeToPlonk(jsonString)

        if (plonkProof != null) {
            type = RegistrationProofType.PLONK_PROOF
            return
        }

        val lightProof = tryDecodeToLight(jsonString)

        if (lightProof != null) {
            type = RegistrationProofType.LIGHT_PROOF
            return
        }
    }


    private fun tryDecodeToGroth(rawJson: String): GrothProof? {
        return try {
            gson.fromJson(rawJson, GrothProof::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun tryDecodeToPlonk(rawJson: String): PlonkProof? {
        return try {
            gson.fromJson(rawJson, PlonkProof::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun tryDecodeToLight(rawJson: String): LightRegistrationData? {
        return try {
            gson.fromJson(rawJson, LightRegistrationData::class.java)
        } catch (e: Exception) {
            null
        }
    }

}


data class GrothProofData(
    val pi_a: List<String>,
    val pi_b: List<List<String>>,
    val pi_c: List<String>,
    val protocol: String,
) {
    companion object {
        fun fromJson(jsonString: String): GrothProofData {
            return gson.fromJson(jsonString, GrothProofData::class.java)
        }
    }
}

data class PlonkProof(
    val rawProof: ByteArray,
    val proof: String,
    val pub_signals: List<String>,
) {
    companion object {
        fun fromByteArray(data: ByteArray): PlonkProof {
            if (data.size != 2304)
                throw IllegalStateException("data.size != 2304, got ${data.size}")

            val pubSignalLen = 5
            val pubSignalData = 32
            val pubSignalSize = pubSignalLen * pubSignalData

            // Extract public signals
            val pubSignalsRaw = data.copyOfRange(0, pubSignalSize)
            val pubSignalsList = pubSignalsRaw
                .toList()
                .chunked(pubSignalData)
                .map { BigInteger(it.toByteArray()).toString() }

            // Extract proof bytes (after pub signals)
            val proofBytes = data.copyOfRange(pubSignalSize, data.size)
            val proofHex = Numeric.toHexString(proofBytes)

            return PlonkProof(
                rawProof = data,
                proof = proofHex,
                pub_signals = pubSignalsList
            )
        }
    }
}

data class GrothProof(
    val proof: GrothProofData, val pub_signals: List<String>
)