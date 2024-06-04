package com.distributedLab.rarime.modules.passport.nfc

import android.nfc.tech.IsoDep
import android.util.Log
import com.distributedLab.rarime.modules.passport.models.AdditionalPersonDetails
import com.distributedLab.rarime.modules.passport.models.DocType
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
import com.distributedLab.rarime.util.DateUtil
import com.distributedLab.rarime.util.SecurityUtil
import com.distributedLab.rarime.util.StringUtil
import com.distributedLab.rarime.util.addCharAtIndex
import com.distributedLab.rarime.util.decodeHexString
import com.distributedLab.rarime.util.publicKeyToPem
import com.distributedLab.rarime.util.toBitArray
import identity.Profile
import net.sf.scuba.smartcards.CardService
import org.bouncycastle.asn1.cms.SignedData
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.ChipAuthenticationInfo
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.SODFile
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG15File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.protocol.AAResult
import org.jmrtd.protocol.EACCAResult
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
    fun revokePassport(challenge: ByteArray, eDocument: EDocument) {
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
            Log.w("Error", e)
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
            eDocument.aaSignature = response.response.toHexString()
            eDocument.aaResponse = response.toString()
            eDocument.isActiveAuth = true
        } catch (e: Exception) {
            eDocument.isActiveAuth = false
        }

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
        var chipAuth = true
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
            Log.w("Error", e)
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
            Log.d("", "Data group: $key hash value: ${StringUtil.byteArrayToHex(value)}")
        }


        val dG15File = service.getInputStream(PassportService.EF_DG15)


        var digestAlgorithm = sodFile.digestAlgorithm
        Log.d(
            "", "Digest Algorithm: $digestAlgorithm"
        )
        val docSigningCert = sodFile.docSigningCertificate
        val docSigningCerts = sodFile.docSigningCertificates
        val pemFile: String = SecurityUtil.convertToPEM(docSigningCert)
        Log.d(
            "", "Document Signer Certificate: $docSigningCert"
        )
        Log.d(
            "", "Document Signer Certificate Pem : $pemFile"
        )
        val digestEncryptionAlgorithm = sodFile.digestEncryptionAlgorithm
        val digest: MessageDigest
        //publishProgress("Loading digest algorithm")
        digest = if (Security.getAlgorithms("MessageDigest").contains(digestAlgorithm)) {
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
        Log.d(
            "", "DG1 Stored Hash: " + StringUtil.byteArrayToHex(dg1StoredHash!!)
        )
        Log.d(
            "", "DG1 Computed Hash: " + StringUtil.byteArrayToHex(dg1ComputedHash)
        )
        if (Arrays.equals(dg1StoredHash, dg1ComputedHash)) {
            Log.d("", "DG1 Hashes are matched")
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
        Log.d(
            "", "DG2 Stored Hash: " + StringUtil.byteArrayToHex(dg2StoredHash!!)
        )
        Log.d(
            "", "DG2 Computed Hash: " + StringUtil.byteArrayToHex(dg2ComputedHash)
        )
        if (Arrays.equals(dg2StoredHash, dg2ComputedHash)) {
            Log.d("", "DG2 Hashes are matched")
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

        try {
            val dg14In = service.getInputStream(PassportService.EF_DG14)
            val dg14File = DG14File(dg14In)
            val dg14StoredHash = sodFile.dataGroupHashes[14]
            val dg14ComputedHash = digest.digest(dg14File.encoded)
            Log.d(
                "", "DG14 Stored Hash: " + StringUtil.byteArrayToHex(dg14StoredHash!!)
            )
            Log.d(
                "", "DG14 Computed Hash: " + StringUtil.byteArrayToHex(dg14ComputedHash)
            )
            if (Arrays.equals(dg14StoredHash, dg14ComputedHash)) {
                Log.d(
                    "", "DG14 Hashes are matched"
                )
            } else {
                hashesMatched = false
            }

            // Chip Authentication
            val eaccaResults = ArrayList<EACCAResult>()
            val chipAuthenticationPublicKeyInfos: MutableList<ChipAuthenticationPublicKeyInfo> =
                ArrayList()
            var chipAuthenticationInfo: ChipAuthenticationInfo? = null
            if (!dg14File.securityInfos.isEmpty()) {
                for (securityInfo in dg14File.securityInfos) {
                    Log.d(
                        "", "DG14 Security Info Identifier: " + securityInfo.objectIdentifier
                    )
                    if (securityInfo is ChipAuthenticationInfo) {
                        chipAuthenticationInfo = securityInfo
                    } else if (securityInfo is ChipAuthenticationPublicKeyInfo) {
                        chipAuthenticationPublicKeyInfos.add(securityInfo)
                    }
                }
                for (chipAuthenticationPublicKeyInfo in chipAuthenticationPublicKeyInfos) {
                    if (chipAuthenticationInfo != null) {
                        val eaccaResult = service.doEACCA(
                            chipAuthenticationInfo.keyId,
                            chipAuthenticationInfo.objectIdentifier,
                            chipAuthenticationInfo.protocolOIDString,
                            chipAuthenticationPublicKeyInfo.subjectPublicKey
                        )
                        eaccaResults.add(eaccaResult)
                    } else {
                        Log.d(
                            "",
                            "Chip Authentication failed for key: $chipAuthenticationPublicKeyInfo"
                        )
                    }
                }
                if (eaccaResults.size == 0) chipAuth = false
            }
        } catch (e: Exception) {
            Log.w("", e)
        }

        eDocument.docType = docType
        eDocument.personDetails = personDetails
        eDocument.additionalPersonDetails = additionalPersonDetails
        eDocument.isPassiveAuth = hashesMatched
        eDocument.isChipAuth = chipAuth


        val dg15 = DG15File(dG15File)

        Log.e("PUB KEy", dg15.publicKey.encoded.toHexString())


        Log.e("Digest Algorithm", sodFile.digestAlgorithm)
        Log.e("signerInfoDigestAlgorithm", sodFile.signerInfoDigestAlgorithm)


        val profiler = Profile().newProfile(privateKey).registrationChallenge
        val response: AAResult
        try {
            response = service.doAA(
                dg15.publicKey, sodFile.digestAlgorithm, sodFile.signerInfoDigestAlgorithm, profiler
            )
            eDocument.aaSignature = response.response.toHexString()
            eDocument.aaResponse = response.toString()
            eDocument.isActiveAuth = true
        } catch (e: Exception) {
            eDocument.isActiveAuth = false
        }

        // sign -> contract

        val index = pemFile.indexOf("-----END CERTIFICATE-----")
        val pemFileEnded = pemFile.addCharAtIndex('\n', index)


        val encapsulaged_content = sodFile.readASN1Data()

        Log.d("Encapsulated Content", encapsulaged_content)
        val dg1B =
            String(dg1File.encoded).toBitArray().toCharArray().map { it1 -> it1.digitToInt() }
        val signedAtributes = sodFile.eContent
        val pubKey = dg15.publicKey.encoded

        Log.e("PUB key cert", sodFile.docSigningCertificate.publicKey.encoded.toHexString())

        val signature = sodFile.encryptedDigest

        eDocument.dg15Pem = dg15.publicKey.publicKeyToPem()


        Log.e("pemFile", "pemFile: $pemFileEnded")
        Log.e("encapsulated_content", "encapsulated_content: $encapsulaged_content")
        Log.e("dg1b", "dg1b: $dg1B")
        Log.e("signedAtributes", "signedAtributes: " + signedAtributes.toHexString())
        Log.e("pubKey", "pubKey: " + pubKey.toHexString())
        Log.e("signature", "signature: " + signature.toHexString())


        Log.e("PUBLIC KEY", sodFile.docSigningCertificate.publicKey.toString())

        eDocument.dg15 = dg15.encoded.toHexString()
        Log.e("DG15", dg15.encoded.toHexString())



        return eDocument
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
