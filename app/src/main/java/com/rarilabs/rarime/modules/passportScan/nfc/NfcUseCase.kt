package com.rarilabs.rarime.modules.passportScan.nfc

import android.nfc.tech.IsoDep
import com.google.gson.Gson
import com.rarilabs.rarime.modules.passportScan.models.AdditionalPersonDetails
import com.rarilabs.rarime.modules.passportScan.models.DocType
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.util.DateUtil
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SecurityUtil
import com.rarilabs.rarime.util.StringUtil
import com.rarilabs.rarime.util.addCharAtIndex
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.publicKeyToPem
import com.rarilabs.rarime.util.toBitArray
import identity.Profile
import net.sf.scuba.smartcards.CardService
import org.bouncycastle.asn1.cms.SignedData
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.SODFile
import org.jmrtd.lds.icao.DG15File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.protocol.AAResult
import java.io.InputStream
import java.security.MessageDigest
import java.security.Security
import java.util.Arrays

class NfcUseCase(
    private val isoDep: IsoDep, private val bacKey: BACKeySpec, private val privateKey: ByteArray
) {
    private var eDocument: EDocument = EDocument()
    private var docType: DocType = DocType.OTHER
    private var personDetails: PersonDetails = PersonDetails()
    private var additionalPersonDetails: AdditionalPersonDetails = AdditionalPersonDetails()

    private fun cropByteArray(inputByteArray: ByteArray, endNumber: Int): ByteArray {
        // Make sure endNumber is within bounds
        val endIndex = if (endNumber > inputByteArray.size) inputByteArray.size else endNumber

        // Use copyOfRange to crop the ByteArray
        return inputByteArray.copyOfRange(0, endIndex)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun scanPassport(): EDocument {
        val cardService = CardService.getInstance(isoDep)
        cardService.open()
        val service = PassportService(
            cardService,
            PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
            PassportService.DEFAULT_MAX_BLOCKSIZE,
            true,
            false
        )


        service.open()
        var paceSucceeded = false
        try {
            val cardSecurityFile =
                CardSecurityFile(service.getInputStream(PassportService.EF_CARD_SECURITY))
            val securityInfoCollection = cardSecurityFile.securityInfos
            for (securityInfo in securityInfoCollection) {

                if (securityInfo is PACEInfo) {
                    val paceInfo = securityInfo
                    service.doPACE(
                        bacKey,
                        paceInfo.objectIdentifier,
                        PACEInfo.toParameterSpec(paceInfo.parameterId),
                        null
                    )
                    paceSucceeded = true
                }
            }
        } catch (e: Exception) {
            ErrorHandler.logError("scanPassport error:", e.toString(), e)
            e.printStackTrace()
        }

        service.sendSelectApplet(paceSucceeded)
        if (!paceSucceeded) {
            try {
                service.getInputStream(PassportService.EF_COM).read()
            } catch (e: Exception) {
                e.printStackTrace()
                service.doBAC(bacKey)
            }
        }


        var hashesMatched = true
        ////publishProgress("Reading sod file")
        val sodIn1 = service.getInputStream(PassportService.EF_SOD)

        val byteArray = ByteArray(1024 * 1024)

        val byteLen = sodIn1.read(byteArray)


        val sod = cropByteArray(byteArray, byteLen).toHexString()
        eDocument.sod = sod


        val sodIn = service.getInputStream(PassportService.EF_SOD)

        val sodFile = SODFileOwn(sodIn)

        sodFile.dataGroupHashes.entries.forEach { (key, value) ->
            ErrorHandler.logDebug(
                "Nfc scan", "Data group: $key hash value: ${StringUtil.byteArrayToHex(value)}"
            )
        }


        val dG15File = service.getInputStream(PassportService.EF_DG15)


        var digestAlgorithm = sodFile.digestAlgorithm
        ErrorHandler.logDebug("Nfc scan", "Digest Algorithm: $digestAlgorithm")
        val docSigningCert = sodFile.docSigningCertificate
        val pemFile: String = SecurityUtil.convertToPEM(docSigningCert)
        ErrorHandler.logDebug(
            "Nfc scan", "Document Signer Certificate: $docSigningCert"
        )
        ErrorHandler.logDebug(
            "Nfc scan", "Document Signer Certificate Pem : $pemFile"
        )

        val digestEncryptionAlgorithm = sodFile.digestEncryptionAlgorithm

        ErrorHandler.logDebug("Nfc scan", "Digest Encryption Algorithm: $digestEncryptionAlgorithm")
        //publishProgress("Loading digest algorithm")
        val digest: MessageDigest = if (Security.getAlgorithms("MessageDigest").contains(digestAlgorithm)) {
            MessageDigest.getInstance(digestAlgorithm)
        } else {
            MessageDigest.getInstance(digestAlgorithm, BouncyCastleProvider())
        }
        //publishProgress("Reading Personal Details")

        // -- Personal Details -- //
        val dg1In = service.getInputStream(PassportService.EF_DG1)
        val dg1File = DG1File(dg1In)
        var encodedDg1File = dg1File.encoded.toHexString()
        val mrzInfo = dg1File.mrzInfo
        personDetails.name = mrzInfo.secondaryIdentifier.replace("<", " ").trim { it <= ' ' }
        personDetails.surname = mrzInfo.primaryIdentifier.replace("<", " ").trim { it <= ' ' }
        personDetails.personalNumber = mrzInfo.personalNumber
        personDetails.gender = mrzInfo.gender.toString()
        personDetails.birthDate = DateUtil.convertFromMrzDate(mrzInfo.dateOfBirth)
        personDetails.expiryDate = DateUtil.convertFromMrzDate(mrzInfo.dateOfExpiry)
        personDetails.serialNumber = mrzInfo.documentNumber
        personDetails.nationality = mrzInfo.nationality
        personDetails.issuerAuthority = mrzInfo.issuingState
        eDocument.dg1 = encodedDg1File

        if ("I" == mrzInfo.documentCode) {
            docType = DocType.ID_CARD
            encodedDg1File =
                StringUtil.fixPersonalNumberMrzData(encodedDg1File, mrzInfo.personalNumber)
        } else if ("P" == mrzInfo.documentCode) {
            docType = DocType.PASSPORT
        }
        val dg1StoredHash = sodFile.dataGroupHashes[1]
        val dg1ComputedHash = digest.digest(encodedDg1File.toByteArray())
        ErrorHandler.logDebug(
            "Nfc scan", "DG1 Stored Hash: " + StringUtil.byteArrayToHex(dg1StoredHash!!)
        )
        ErrorHandler.logDebug(
            "Nfc scan", "DG1 Computed Hash: " + StringUtil.byteArrayToHex(dg1ComputedHash)
        )
        if (Arrays.equals(dg1StoredHash, dg1ComputedHash)) {
            ErrorHandler.logDebug("Nfc scan", "DG1 Hashes are matched")
        } else {
            hashesMatched = false
        }
        //publishProgress("Reading Face Image")

        // -- Face Image -- //
        val dg2In = service.getInputStream(PassportService.EF_DG2)
        val dg2File = DG2File(dg2In)
        //publishProgress("Decoding Face Image")
        val dg2StoredHash = sodFile.dataGroupHashes[2]
        val dg2ComputedHash = digest.digest(dg2File.encoded)
        ErrorHandler.logDebug(
            "Nfc scan", "DG2 Stored Hash: " + StringUtil.byteArrayToHex(dg2StoredHash!!)
        )
        ErrorHandler.logDebug(
            "Nfc scan", "DG2 Computed Hash: " + StringUtil.byteArrayToHex(dg2ComputedHash)
        )
        if (Arrays.equals(dg2StoredHash, dg2ComputedHash)) {
            ErrorHandler.logDebug("Nfc scan", "DG2 Hashes are matched")
        } else {
            hashesMatched = false
        }
        val faceInfos = dg2File.faceInfos
        val allFaceImageInfos: MutableList<FaceImageInfo> = ArrayList()
        for (faceInfo in faceInfos) {
            allFaceImageInfos.addAll(faceInfo.faceImageInfos)
        }
        if (!allFaceImageInfos.isEmpty()) {
            val faceImageInfo = allFaceImageInfos.iterator().next()
            personDetails!!.faceImageInfo = faceImageInfo
        }



        eDocument.docType = docType
        eDocument.personDetails = personDetails
        eDocument.additionalPersonDetails = additionalPersonDetails
        eDocument.isPassiveAuth = hashesMatched


        val dg15 = try {
            DG15File(dG15File)
        } catch (
            e: Exception
        ) {
            ErrorHandler.logError("Nfc scan", "No DG15 file", e)
            null
        }

        //Arrays.copyOfRange(poseidonHash, poseidonHash.size - 8, poseidonHash.size).reversed().toByteArray()


        ErrorHandler.logDebug("PUB KEy", dg15?.publicKey?.encoded?.toHexString().toString())


        ErrorHandler.logDebug("Digest Algorithm", sodFile.digestAlgorithm)
        ErrorHandler.logDebug("signerInfoDigestAlgorithm", sodFile.signerInfoDigestAlgorithm)


        val profiler = Profile().newProfile(privateKey).registrationChallenge
        var response: AAResult? = null
        try {
            response = service.doAA(
                dg15?.publicKey,
                sodFile.digestAlgorithm,
                sodFile.signerInfoDigestAlgorithm,
                profiler
            )
            eDocument.aaSignature = response.response
            eDocument.aaResponse = response.toString()
            eDocument.isActiveAuth = true
            ErrorHandler.logDebug("Nfc scan", "AA is available")
        } catch (e: Exception) {
            eDocument.isActiveAuth = false
            ErrorHandler.logError("Nfc scan", "AA is NOT available")
        }


        eDocument.aaSignature = response?.response

        // sign -> contract

        val index = pemFile.indexOf("-----END CERTIFICATE-----")
        val pemFileEnded = pemFile.addCharAtIndex('\n', index)


        val encapsulaged_content = sodFile.readASN1Data()

        ErrorHandler.logDebug("Encapsulated Content", encapsulaged_content)

        val signedAtributes = sodFile.eContent
        val pubKey = dg15?.publicKey?.encoded

        ErrorHandler.logError(
            "PUB key cert", sodFile.docSigningCertificate.publicKey.encoded.toHexString()
        )

        val signature = sodFile.encryptedDigest

        eDocument.dg15Pem = dg15?.publicKey?.publicKeyToPem()


        try {
            ErrorHandler.logError("pemFile", "pemFile: $pemFileEnded")
            ErrorHandler.logError("encapsulated_content", "encapsulated_content: $encapsulaged_content")
            ErrorHandler.logError(
                "signedAtributes", "signedAtributes: " + signedAtributes.toHexString()
            )
            ErrorHandler.logError("pubKey", "pubKey: " + pubKey?.toHexString())
            ErrorHandler.logError("signature", "signature: " + signature.toHexString())


            ErrorHandler.logError("PUBLIC KEY", sodFile.docSigningCertificate.publicKey.toString())
            eDocument.dg15 = dg15?.encoded?.toHexString()
        } catch (e: Exception) {
            ErrorHandler.logError("NFC SCAN", "Smt wrong with Log data while scanning", e)
        }

        return eDocument
    }

    fun signRevocationWithPassport(challenge: ByteArray, eDocument: EDocument): EDocument {
        val cardService = CardService.getInstance(isoDep)
        cardService.open()

        val service = PassportService(
            cardService,
            PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
            PassportService.DEFAULT_MAX_BLOCKSIZE,
            true,
            false
        )
        service.open()

        var paceSucceeded = false
        try {
            val cardSecurityFile =
                CardSecurityFile(service.getInputStream(PassportService.EF_CARD_SECURITY))
            val securityInfoCollection = cardSecurityFile.securityInfos
            for (securityInfo in securityInfoCollection) {

                if (securityInfo is PACEInfo) {
                    val paceInfo = securityInfo
                    service.doPACE(
                        bacKey,
                        paceInfo.objectIdentifier,
                        PACEInfo.toParameterSpec(paceInfo.parameterId),
                        null
                    )
                    paceSucceeded = true
                }
            }
        } catch (e: Exception) {
            ErrorHandler.logError("Error", e.toString(), e)
        }

        service.sendSelectApplet(paceSucceeded)
        if (!paceSucceeded) {
            try {
                service.getInputStream(PassportService.EF_COM).read()
            } catch (e: Exception) {
                e.printStackTrace()
                service.doBAC(bacKey)
            }
        }

        val sodFile = SODFileOwn(eDocument.sod!!.decodeHexString().inputStream())
        val dg15 = DG15File(eDocument.dg15!!.decodeHexString().inputStream())

        try {
            val response = service.doAA(
                dg15.publicKey,
                sodFile.digestAlgorithm,
                sodFile.signerInfoDigestAlgorithm,
                challenge
            )

            try {
                ErrorHandler.logDebug("eDocument", Gson().toJson(eDocument))
                ErrorHandler.logDebug("response", Gson().toJson(response))
            } catch (e: Exception) {
            }

            return eDocument.copy(
                aaSignature = response.response,
                aaResponse = response.toString(),
                isActiveAuth = true
            )
        } catch (e: Exception) {
            ErrorHandler.logError("SignRevocationWithPassport", e.toString())

            return eDocument.copy(isActiveAuth = false)
        }

    }
}

class SODFileOwn(inputStream: InputStream?) : SODFile(inputStream) {
    @OptIn(ExperimentalStdlibApi::class)
    fun readASN1Data(): String {
        val a = SODFile::class.java.getDeclaredField("signedData");
        a.isAccessible = true

        val v: SignedData = a.get(this) as SignedData

        val encapsulatedContent =
            v.encapContentInfo.content.toASN1Primitive().encoded!!.toHexString()

        val target = "30"
        val startIndex = encapsulatedContent.indexOf(target)
        return encapsulatedContent.substring(startIndex)
    }
}
