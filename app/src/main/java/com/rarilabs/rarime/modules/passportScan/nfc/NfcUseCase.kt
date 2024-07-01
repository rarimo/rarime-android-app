package com.rarilabs.rarime.modules.passportScan.nfc

import android.nfc.tech.IsoDep
import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.modules.passportScan.models.AdditionalPersonDetails
import com.rarilabs.rarime.modules.passportScan.models.DocType
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.util.DateUtil
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
        try {
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
                val cardSecurityFile = CardSecurityFile(service.getInputStream(PassportService.EF_CARD_SECURITY))
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

            val dg15 = try {
                val dG15File = service.getInputStream(PassportService.EF_DG15)
                DG15File(dG15File)
            } catch (e: Exception) {
                Log.i("NFC Scan", "No Dg15")
                null
            }

            val digestAlgorithm = sodFile.digestAlgorithm
            Log.d("", "Digest Algorithm: $digestAlgorithm")
            val digest: MessageDigest = if (Security.getAlgorithms("MessageDigest").contains(digestAlgorithm)) {
                MessageDigest.getInstance(digestAlgorithm)
            } else {
                MessageDigest.getInstance(digestAlgorithm, BouncyCastleProvider())
            }

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
                encodedDg1File = StringUtil.fixPersonalNumberMrzData(encodedDg1File, mrzInfo.personalNumber)
            } else if ("P" == mrzInfo.documentCode) {
                docType = DocType.PASSPORT
            }
            val dg1StoredHash = sodFile.dataGroupHashes[1]
            val dg1ComputedHash = digest.digest(encodedDg1File.toByteArray())

            if (Arrays.equals(dg1StoredHash, dg1ComputedHash)) {
                Log.d("", "DG1 Hashes are matched")
            } else {
                hashesMatched = false
            }

            val dg2In = service.getInputStream(PassportService.EF_DG2)
            val dg2File = DG2File(dg2In)
            val dg2StoredHash = sodFile.dataGroupHashes[2]
            val dg2ComputedHash = digest.digest(dg2File.encoded)

            if (dg2StoredHash.contentEquals(dg2ComputedHash)) {
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
                personDetails.faceImageInfo = faceImageInfo
            }

            eDocument.docType = docType
            eDocument.personDetails = personDetails
            eDocument.additionalPersonDetails = additionalPersonDetails
            eDocument.isPassiveAuth = hashesMatched


            val profiler = Profile().newProfile(privateKey).registrationChallenge
            var response: AAResult? = null
            try {
                if (dg15 == null) {
                    throw IllegalStateException("No Dg15 for sign")
                }
                response = service.doAA(
                    dg15.publicKey, sodFile.digestAlgorithm, sodFile.signerInfoDigestAlgorithm, profiler
                )
                eDocument.aaSignature = response?.response
                eDocument.aaResponse = response?.toString()
                eDocument.isActiveAuth = true
            } catch (e: Exception) {
                eDocument.isActiveAuth = false
            }

            eDocument.aaSignature = response?.response
            eDocument.dg15Pem = dg15?.publicKey?.publicKeyToPem()
            eDocument.dg15 = dg15?.encoded?.toHexString()

        } catch (e: Exception) {
            Log.e("NfcUseCase", "Error in scanPassport: ${e.message}", e)
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

        val sodFile = SODFileOwn(eDocument.sod?.decodeHexString()?.inputStream())
        val dg15 = DG15File(eDocument.dg15?.decodeHexString()?.inputStream())

        try {
            val response = service.doAA(
                dg15.publicKey,
                sodFile.digestAlgorithm,
                sodFile.signerInfoDigestAlgorithm,
                challenge
            )

            try {
                Log.i("eDocument", Gson().toJson(eDocument))
                Log.i("response", Gson().toJson(response))
            } catch (e: Exception) {
            }

            return eDocument.copy(
                aaSignature = response.response,
                aaResponse = response.toString(),
                isActiveAuth = true
            )
        } catch (e: Exception) {
            Log.e("SignRevocationWithPassport", e.toString())

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
