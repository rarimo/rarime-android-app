package com.distributedLab.rarime.data

import com.distributedLab.rarime.contracts.rarimo.PoseidonSMT
import com.distributedLab.rarime.util.toBase64
import java.io.Serializable

data class ProofTx(
    val root: String,
    val siblings: List<String>,
    val existence: Boolean,
) : Serializable

data class ProofTxFull(
    val root: String,
    val siblings: List<String>,
    val existence: Boolean,
    var key: String,
    var value: String,
    var auxExistence: Boolean? = null,
    var auxKey: String,
    var auxValue: String
) : Serializable {
    companion object {
        fun fromContractProof(proof: PoseidonSMT.Proof): ProofTxFull {
            return ProofTxFull(
                root = proof.root.toBase64(),
                siblings = proof.siblings.map { it.toBase64() },
                existence = proof.existence,
                key = proof.key.toBase64(),
                value = proof.pValue.toBase64(),
                auxExistence = proof.auxExistence,
                auxKey = proof.auxKey.toBase64(),
                auxValue = proof.auxValue.toBase64()
            )
        }
    }
}