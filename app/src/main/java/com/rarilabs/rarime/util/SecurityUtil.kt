package com.rarilabs.rarime.util

import android.util.Log
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequence
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.jmrtd.Util
import java.io.IOException
import java.io.StringWriter
import java.security.MessageDigest
import java.security.Provider
import java.security.PublicKey
import java.security.Security
import java.security.Signature
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PSSParameterSpec
import javax.crypto.Cipher


/**
 * @author AliMertOzdemir
 * @class SecurityUtil
 * @created 01.12.2020
 */


object SecurityUtil {
    private val TAG = SecurityUtil::class.java.simpleName
    private val BC_PROVIDER: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)


    fun verifyAA(
        publicKey: PublicKey,
        digestAlgorithm: String?,
        signatureAlgorithm: String?,
        challenge: ByteArray?,
        response: ByteArray
    ): Boolean {
        var result = false
        val publicKeyAlgorithm = publicKey.algorithm
        if ("RSA" == publicKeyAlgorithm) {
            Log.d(
                TAG,
                "Unexpected algorithms for RSA AA: digest algorithm = $digestAlgorithm, signature algorithm = $signatureAlgorithm"
            )
            val rsaAADigest: MessageDigest
            val rsaAASignature: Signature
            val rsaAACipher: Cipher
            val rsaPublicKey: PublicKey
            try {
                rsaAADigest = MessageDigest.getInstance(digestAlgorithm)
                rsaAASignature = Signature.getInstance(signatureAlgorithm, BC_PROVIDER)
                rsaAACipher = Cipher.getInstance("RSA/NONE/NoPadding")
                rsaPublicKey = publicKey as RSAPublicKey
                rsaAACipher.init(Cipher.DECRYPT_MODE, rsaPublicKey)
                rsaAASignature.initVerify(rsaPublicKey)
                val digestLength = rsaAADigest.digestLength
                val decryptedResponse = rsaAACipher.doFinal(response)
                val m1 = Util.recoverMessage(digestLength, decryptedResponse)
                rsaAASignature.update(m1)
                rsaAASignature.update(challenge)
                result = rsaAASignature.verify(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if ("EC" == publicKeyAlgorithm || "ECDSA" == publicKeyAlgorithm) {
            var ecdsaAADigest: MessageDigest?
            var ecdsaAASignature: Signature?
            val ecdsaPublicKey: PublicKey
            try {
                ecdsaAADigest = MessageDigest.getInstance("SHA-256")
                ecdsaAASignature = Signature.getInstance("SHA256withECDSA", BC_PROVIDER)
                ecdsaPublicKey = publicKey as ECPublicKey
                if (ecdsaAASignature == null || signatureAlgorithm != null && signatureAlgorithm != ecdsaAASignature.algorithm) {
                    Log.d(
                        TAG,
                        "Re-initializing ecdsaAASignature with signature algorithm $signatureAlgorithm"
                    )
                    ecdsaAASignature = Signature.getInstance(signatureAlgorithm)
                }
                if (ecdsaAADigest == null || digestAlgorithm != null && digestAlgorithm != ecdsaAADigest.algorithm) {
                    Log.d(
                        TAG, "Re-initializing ecdsaAADigest with digest algorithm $digestAlgorithm"
                    )
                    ecdsaAADigest = MessageDigest.getInstance(digestAlgorithm)
                }
                ecdsaAASignature.initVerify(ecdsaPublicKey)
                if (response.size % 2 != 0) {
                    Log.d(TAG, "Active Authentication response is not of even length")
                }
                val length = response.size / 2
                val r = Util.os2i(response, 0, length)
                val s = Util.os2i(response, length, length)
                ecdsaAASignature.update(challenge)
                try {
                    val asn1R = ASN1Integer(r)
                    val asn1S = ASN1Integer(s)
                    val asn1Encodables = arrayOf<ASN1Encodable>(asn1R, asn1S)
                    val asn1Sequence = DERSequence(asn1Encodables)
                    result = ecdsaAASignature.verify(asn1Sequence.encoded)
                } catch (exp: IOException) {
                    Log.e(TAG, "Unexpected exception during AA signature verification with ECDSA")
                    exp.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun findSaltRSA_PSS(
        digestEncryptionAlgorithm: String,
        docSigningCert: Certificate?,
        eContent: ByteArray?,
        signature: ByteArray?
    ): Int {
        for (i in 0..512) {
            try {
                val sig = Signature.getInstance(digestEncryptionAlgorithm, BC_PROVIDER)
                if (digestEncryptionAlgorithm.endsWith("withRSA/PSS")) {
                    val mgf1ParameterSpec = MGF1ParameterSpec("SHA-256")
                    val pssParameterSpec =
                        PSSParameterSpec("SHA-256", "MGF1", mgf1ParameterSpec, i, 1)
                    sig.setParameter(pssParameterSpec)
                }
                sig.initVerify(docSigningCert)
                sig.update(eContent)
                val verify = sig.verify(signature)
                if (verify) {
                    return i
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return 0
    }

    fun convertToPEM(certificate: X509Certificate): String {
        val stringWriter = StringWriter()
        JcaPEMWriter(stringWriter).use { pemWriter ->
            pemWriter.writeObject(certificate)
        }
        return stringWriter.toString()
    }
}
