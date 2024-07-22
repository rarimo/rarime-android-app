package com.rarilabs.rarime.util.data

data class IOSPassport(
    var dateOfBirth: String,
    var dg1: String,
    var dg15: String,
    var documentExpiryDate: String,
    var documentNumber: String,
    var documentType: String,
    var firstName: String,
    var gender: String,
    var issuingAuthority: String,
    var lastName: String,
    var nationality: String,
    var passportImageRaw: String,
    var signature: String,
    var sod: String
)
