package com.rarilabs.rarime.api.registration.models

import com.rarilabs.rarime.util.data.ZkProof


data class VerifySodRequest(
    val data: VerifySodRequestData
)

data class VerifySodRequestData(
    val id: String,
    val type: String,
    val attributes: VerifySodRequestAttributes
)

data class VerifySodRequestAttributes(
    val zk_proof: ZkProof, // Adjust the type as per the expected structure of zk_proof
    val document_sod: DocumentSodAttributes
)

data class DocumentSodAttributes(
    val hash_algorithm: String,
    val signature_algorithm: String,
    val signed_attributes: String,
    val signature: String,
    val aa_signature: String,
    val encapsulated_content: String,
    val pem_file: String,
    val dg15: String
)


data class VerifySodResponse(
    val data: VerifySodResponseData
)

data class VerifySodResponseData(
    val id: String,
    val type: String,
    val attributes: VerifySodResponseAttributes
)

data class VerifySodResponseAttributes(
    val signature: String,
    val document_hash: String
)