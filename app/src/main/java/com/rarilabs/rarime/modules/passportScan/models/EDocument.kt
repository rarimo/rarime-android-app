package com.rarilabs.rarime.modules.passportScan.models

import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.circuits.CircuitSignatureCurveType
import com.rarilabs.rarime.util.circuits.CircuitSignatureExponentType
import com.rarilabs.rarime.util.circuits.CircuitSignatureKeySizeType
import com.rarilabs.rarime.util.circuits.RegisterIdentityCircuitType
import com.rarilabs.rarime.util.decodeHexString
import org.bouncycastle.asn1.eac.ECDSAPublicKey
import java.security.interfaces.ECKey
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.ECParameterSpec


data class EDocument(
    var docType: DocType? = null,
    var personDetails: PersonDetails? = null,
    var additionalPersonDetails: AdditionalPersonDetails? = null,
    var isPassiveAuth: Boolean = false,
    var isActiveAuth: Boolean = false,
    var isChipAuth: Boolean = false,
    var sod: String? = null,
    var dg1: String? = null,
    var dg15: String? = null,
    var dg15Pem: String? = null,
    var aaSignature: ByteArray? = null,
    var aaResponse: String? = null,
) {

    fun getSodFile(): SODFileOwn {
        val sodStream = this.sod!!.decodeHexString().inputStream()
        return SODFileOwn(sodStream)
    }

    fun getRegisterIdentityCircuitType(): RegisteredCircuitData {
        val sod = getSodFile()
        val sodSignatureAlgorithmName = sod.docSigningCertificate.sigAlgName
    }


    private fun getSodPublicKeySupportedSize(size: Int): CircuitSignatureKeySizeType? {
        return when (size) {
            2048 -> CircuitSignatureKeySizeType.B2048
            4096 -> CircuitSignatureKeySizeType.B4096
            256 -> CircuitSignatureKeySizeType.B256
            320 -> CircuitSignatureKeySizeType.B320
            192 -> CircuitSignatureKeySizeType.B192
            else -> null
        }
    }

    private fun getSodPublicKeyExponent(publicKey: RSAPublicKey): CircuitSignatureExponentType? {
        val exponent = publicKey.publicExponent.toLong()
        return when (exponent.toUInt()) {
            3u -> CircuitSignatureExponentType.E3
            65537u -> CircuitSignatureExponentType.E65537
            else -> null
        }
    }

    private fun getSodPublicKeyCurve(publicKey: ECPublicKey?): CircuitSignatureCurveType? {
        val params: ECParameterSpec = publicKey!!.params
        params.getCurveName()
//        return when (curve) {
//            "secp256r1" -> RegisterIdentityCircuitType.CircuitSignatureType.CircuitSignatureCurveType.SECP256R1
//            "brainpoolP256r1" -> RegisterIdentityCircuitType.CircuitSignatureType.CircuitSignatureCurveType.BRAINPOOLP256
//            "brainpoolP320r1" -> RegisterIdentityCircuitType.CircuitSignatureType.CircuitSignatureCurveType.BRAINPOOL320R1
//            "secp192r1" -> RegisterIdentityCircuitType.CircuitSignatureType.CircuitSignatureCurveType.SECP192R1
//            else -> null
//        }
    }
}