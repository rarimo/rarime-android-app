package com.distributedLab.rarime.domain.data

import com.distributedLab.rarime.contracts.PoseidonSMT
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
        @OptIn(ExperimentalStdlibApi::class)
        fun fromContractProof(proof: PoseidonSMT.Proof): ProofTxFull {
            return ProofTxFull(
                root = proof.root.toHexString(),
                siblings = proof.siblings.map { it.toHexString() },
                existence = proof.existence,
                key = proof.key.toHexString(),
                value = proof.pValue.toHexString(),
                auxExistence = proof.auxExistence,
                auxKey = proof.auxKey.toHexString(),
                auxValue = proof.auxValue.toHexString()
            )
        }
    }
}