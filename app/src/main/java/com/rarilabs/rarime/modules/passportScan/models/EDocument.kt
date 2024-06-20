package com.rarilabs.rarime.modules.passportScan.models


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


}
