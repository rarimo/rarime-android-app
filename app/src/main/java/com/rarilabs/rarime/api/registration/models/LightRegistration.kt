package com.rarilabs.rarime.api.registration.models

import com.rarilabs.rarime.util.data.GrothProof

data class VerifySodRequest(
    val data: VerifySodRequestData
)

data class VerifySodRequestData(
    val id: String,
    val type: String,
    val attributes: VerifySodRequestAttributes
)

data class VerifySodRequestAttributes(
    val zk_proof: GrothProof, // Adjust the type as per the expected structure of zk_proof
    val document_sod: VerifySodRequestDocumentSod
)

data class VerifySodRequestDocumentSod(
    val hash_algorithm: String,
    val signature_algorithm: String,
    val signed_attributes: String,
    val signature: String,
    val aa_signature: String,
    val encapsulated_content: String,
    val pem_file: String,
    val dg15: String,
    val sod: String
)

data class VerifySodResponse(
    val data: VerifySodResponseData
)

data class VerifySodResponseData(
    val id: String,
    val type: String,
    val attributes: LightRegistrationData
)

data class LightRegistrationData(
    val passport_hash: String,
    val public_key: String,
    val signature: String,
    val verifier: String
)