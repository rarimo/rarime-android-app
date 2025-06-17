package com.rarilabs.rarime.util

import java.math.BigInteger

enum class QueryProofField(val bitIndex: Int, val displayName: String) { //not sure is this good for app architecture
    Nullifier(0, "Incognito ID"),
    BirthDate(1, "Birth date"),
    ExpirationDate(2, "Expiration date"),
    Name(3, "Name"),
    Nationality(4, "Nationality"),
    Citizenship(5, "Citizenship"),
    Sex(6, "Sex"),
    DocumentNumber(7, "Document number"),
    TimestampLowerbound(8, "Registered after"),
    TimestampUpperbound(9, "Registered before"),
    IdentityCounterLowerbound(10, "Min registrations"),
    IdentityCounterUpperbound(11, "Max registrations"),
    PassportExpirationLowerbound(12, "Expiration after"),
    PassportExpirationUpperbound(13, "Expiration before"),
    BirthDateLowerbound(14, "Born after"),
    BirthDateUpperbound(15, "Born before"),
    VerifyCitizenshipWhitelist(16, "Citizenship whitelist"),
    VerifyCitizenshipBlacklist(17, "Citizenship blacklist");

    companion object {
        fun fromSelector(selector: String): List<QueryProofField> {
            val bitmask = selector.toBigInteger()


            return QueryProofField.entries.filter {
                (bitmask shr it.bitIndex) and BigInteger.ONE == BigInteger.ONE
            }
        }
    }
}