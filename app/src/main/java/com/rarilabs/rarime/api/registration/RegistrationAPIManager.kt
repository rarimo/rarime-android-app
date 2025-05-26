package com.rarilabs.rarime.api.registration


import CircuitAlgorithmType
import CircuitPassportHashType
import com.rarilabs.rarime.api.registration.models.RegisterBody
import com.rarilabs.rarime.api.registration.models.RegisterData
import com.rarilabs.rarime.api.registration.models.RegisterResponseBody
import com.rarilabs.rarime.api.registration.models.VerifySodRequest
import com.rarilabs.rarime.api.registration.models.VerifySodRequestAttributes
import com.rarilabs.rarime.api.registration.models.VerifySodRequestData
import com.rarilabs.rarime.api.registration.models.VerifySodRequestDocumentSod
import com.rarilabs.rarime.api.registration.models.VerifySodResponse
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SecurityUtil
import com.rarilabs.rarime.util.circuits.SODAlgorithm
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.decodeHexString
import org.web3j.utils.Numeric
import javax.inject.Inject

class UserAlreadyRevoked : Exception()
class PassportAlreadyRegisteredByOtherPK : Exception()

class RegistrationAPIManager @Inject constructor(
    private val registrationAPI: RegistrationAPI
) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun register(callData: ByteArray, destination: String): RegisterResponseBody {
        val response = registrationAPI.register(
            RegisterBody(
                data = RegisterData(
                    tx_data = "0x" + callData.toHexString(),
                    destination = destination,
                    no_send = false
                )
            )
        )

        if (response.isSuccessful) {
            return response.body()!!
        }

        val errorBody = response.errorBody()?.string()
        val errorCode = response.code()

        ErrorHandler.logError("RegistrationAPIManager", errorBody.toString())

        if (errorCode == 400 && errorBody?.contains("already registered") == true) {
            throw PassportAlreadyRegisteredByOtherPK()
        } else if (errorBody?.contains("already revoked") == true || errorBody?.contains("the leaf does not match") == true) {
            throw UserAlreadyRevoked()
        } else if (errorBody?.contains("proof") == true) {
            ErrorHandler.logError("Registration failed with proof", errorBody.toString())
            throw Exception("Registration failed")
        } else {
            throw Exception("Registration failed")
        }
    }

    suspend fun lightRegistration(eDocument: EDocument, zkProof: GrothProof): VerifySodResponse {
        val sodFile = eDocument.getSodFile()

        val signedAttributes = sodFile.eContent
        val encapsulatedContent = Numeric.hexStringToByteArray(sodFile.readASN1Data())
        val signature = sodFile.encryptedDigest

        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)

        val digestAlgorithm = sodFile.digestAlgorithm
        val encapsulatedContentDigestAlgorithm = CircuitPassportHashType.fromValue(digestAlgorithm)
            ?: throw IllegalArgumentException("Invalid digest algorithm")

        val sodSignatureAlgorithmName = sodFile.digestEncryptionAlgorithm

        val sodSignatureAlgorithm =
            SODAlgorithm.fromValue(sodSignatureAlgorithmName)?.getCircuitSignatureAlgorithm()
                ?: throw IllegalStateException("SOD algorithm not found: $sodSignatureAlgorithmName")

        val sodSignatureAlgorithmNameText = when (sodSignatureAlgorithm) {
            CircuitAlgorithmType.RSA -> "RSA"
            CircuitAlgorithmType.RSAPSS -> "RSA-PSS"
            CircuitAlgorithmType.ECDSA -> "ECDSA"
        }

        val zkProof = zkProof.getPublicKey()

        val request = VerifySodRequest(

            data = VerifySodRequestData(
                id = "",
                type = "register",
                attributes = VerifySodRequestAttributes(
                    zk_proof = zkProof,
                    document_sod = VerifySodRequestDocumentSod(
                        hash_algorithm = encapsulatedContentDigestAlgorithm.value.uppercase()
                            .replace("-", ""),
                        signature_algorithm = sodSignatureAlgorithmNameText,
                        signed_attributes = Numeric.toHexString(signedAttributes),
                        signature = Numeric.toHexString(signature),
                        encapsulated_content = Numeric.toHexString(encapsulatedContent),
                        pem_file = certPem,
                        dg15 = if (eDocument.dg15 != null) eDocument.dg15!! else "",
                        sod = Numeric.toHexString(eDocument.sod!!.decodeHexString()),
                        aa_signature = if (eDocument.aaSignature?.isEmpty() != false) "" else Numeric.toHexString(
                            eDocument.aaSignature
                        )
                    )
                )
            )
        )

        val response = registrationAPI.incognitoLightRegistrator(request)

        if (response.isSuccessful) {
            return response.body()!!
        }

        val errorBody = response.errorBody()?.string()
        ErrorHandler.logError("RegistrationAPIManager", errorBody.toString())

        throw Exception("Failed to register via light registration: $errorBody")
    }
}