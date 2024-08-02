package com.rarilabs.rarime.util

import com.google.gson.Gson
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.util.data.IOSPassport
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.util.encoders.Base64
import org.jmrtd.lds.iso19794.FaceImageInfo
import java.io.File

class ParseEDocument {

    private fun readFile(fileName: String): String {
        val jsonRaw = File(fileName).inputStream().readBytes().toString(Charsets.UTF_8)
        val json = preprocessFile(jsonRaw)
        return json
    }

    fun parseEDocument(fileName: String): EDocument {
        val json = readFile(fileName)
        val struct = parseToStruct(json)
        val eDocument = convertToEDocument(struct)
        return eDocument
    }

    private fun parseToStruct(json: String): IOSPassport {
        val passport: IOSPassport = Gson().fromJson(json, IOSPassport::class.java)
        val passportHexed = convertFieldsToHex(passport)
        return passportHexed
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun convertFieldsToHex(passport: IOSPassport): IOSPassport {
        passport.sod = decodeBase64Asn1(passport.sod)?.encoded?.toHexString() ?: passport.sod
        passport.dg1 = decodeBase64Asn1(passport.dg1)?.encoded?.toHexString() ?: passport.dg1
        passport.dg15 = decodeBase64Asn1(passport.dg15)?.encoded?.toHexString() ?: passport.dg15

        return passport
    }

    private fun convertToEDocument(passport: IOSPassport): EDocument {
        val eDocument = EDocument()
        val personDetails = PersonDetails()
        personDetails.gender = personDetails.gender

        personDetails.faceImageInfo = FaceImageInfo(passport.passportImageRaw.byteInputStream())
        personDetails.birthDate = passport.dateOfBirth
        personDetails.name = passport.firstName
        personDetails.surname = passport.lastName
        personDetails.nationality = passport.nationality
        personDetails.expiryDate = passport.documentExpiryDate
        personDetails.issuerAuthority = passport.issuingAuthority

        eDocument.personDetails = personDetails
        eDocument.dg1 = passport.dg1
        eDocument.dg15 = passport.dg15
        eDocument.sod = passport.sod
        eDocument.isChipAuth = false

        return eDocument
    }


    private fun preprocessFile(rawJson: String): String {
        val res = rawJson.replace("\\" + "/", "/")
        return res
    }

    companion object {
        fun decodeBase64Asn1(base64String: String?): ASN1Primitive? {
            // Decode the Base64 string
            val decodedBytes = Base64.decode(base64String)

            // Create an ASN1InputStream to parse the ASN.1 structure
            ASN1InputStream(decodedBytes).use { asn1InputStream ->
                // Read the ASN.1 object
                return asn1InputStream.readObject()
            }
        }
    }
}