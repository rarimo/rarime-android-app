package com.rarilabs.rarime.util

import org.bouncycastle.jcajce.provider.digest.Keccak
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.StandardCharsets

data class EIP712Domain(
    val name: String,
    val version: String,
    val chainId: Long,
    val verifyingContract: String
)

data class MessageType(
    val type: String,
    val name: String
)

data class EIP712TypedData(
    val types: Map<String, List<MessageType>>,
    val domain: EIP712Domain,
    val primaryType: String,
    val message: Map<String, Any>
)

object EIP712Utility {

    // Keccak-256 hashing function
    private fun keccak256(input: ByteArray): ByteArray {
        val digest = Keccak.Digest256()
        return digest.digest(input)
    }

    // Function to hash the domain separator
    private fun hashDomain(domain: EIP712Domain): ByteArray {
        val domainTypeHash = keccak256(serializeType("EIP712Domain", domain.toMap()))
        val domainDataHash = keccak256(serializeData("EIP712Domain", domain.toMap()))

        return keccak256(domainTypeHash + domainDataHash)
    }

    // Function to hash the message
    private fun hashMessage(typedData: EIP712TypedData): ByteArray {
        val messageTypeHash = keccak256(serializeType(typedData.primaryType, typedData.message))
        val messageDataHash = keccak256(serializeData(typedData.primaryType, typedData.message))

        return keccak256(messageTypeHash + messageDataHash)
    }

    // Function to sign the message
    fun signMessage(typedData: EIP712TypedData, privateKey: String): String {
        val domainHash = hashDomain(typedData.domain)
        val messageHash = hashMessage(typedData)

        val finalHash = keccak256(byteArrayOf(0x19.toByte(), 0x01) + domainHash + messageHash)
        val credentials = Credentials.create(privateKey)

        val signatureData = Sign.signMessage(finalHash, credentials.ecKeyPair, false)
        return Numeric.toHexString(signatureData.r + signatureData.s + signatureData.v)
    }

    // Serialize the data type to a byte array
    private fun serializeType(primaryType: String, data: Map<String, Any>): ByteArray {
        val typeDefinition = buildString {
            append(primaryType).append("(")
            data.forEach { (key, value) ->
                append(value.javaClass.simpleName).append(" ").append(key).append(",")
            }
            deleteCharAt(length - 1) // remove the trailing comma
            append(")")
        }

        return typeDefinition.toByteArray(StandardCharsets.UTF_8)
    }

    // Updated serializeData function
    private fun serializeData(primaryType: String, data: Map<String, Any>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        data.forEach { (_, value) ->
            outputStream.write(encodeValue(value))
        }
        return keccak256(outputStream.toByteArray())
    }

    // Helper function to encode individual values based on type
    private fun encodeValue(value: Any): ByteArray {
        return when (value) {
            is String -> keccak256(value.toByteArray(StandardCharsets.UTF_8))
            is Long -> keccak256(BigInteger.valueOf(value).toByteArray())
            is BigInteger -> keccak256(value.toByteArray())
            is ByteArray -> keccak256(value)
            else -> throw IllegalArgumentException("Unsupported value type: ${value.javaClass}")
        }
    }

    // Extension function to convert EIP712Domain to Map
    private fun EIP712Domain.toMap(): Map<String, Any> {
        return mapOf(
            "name" to this.name,
            "version" to this.version,
            "chainId" to this.chainId,
            "verifyingContract" to this.verifyingContract
        )
    }
}
