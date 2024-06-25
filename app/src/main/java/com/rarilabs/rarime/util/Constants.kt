package com.rarilabs.rarime.util

import kotlin.time.Duration.Companion.minutes

object Constants {
    const val TERMS_URL = "https://rarime.com/general-terms.html"
    const val PRIVACY_URL = "https://rarime.com/privacy-notice.html"
    const val AIRDROP_TERMS_URL = "https://rarime.com/airdrop-terms.html"

    const val MAX_PASSCODE_ATTEMPTS = 5
    val PASSCODE_LOCK_PERIOD = 5.minutes


    const val SCAN_PASSPORT_REWARD = 10.0
    const val AIRDROP_REWARD = 10.0
    const val MAX_PASSPORT_IDENTIFIERS = 2

    val NOT_ALLOWED_COUNTRIES = listOf(
        "RUS",
        "USA",
        "CAN",
        "BLR",
        "CHN",
        "HKG",
        "MAC",
        "TWN",
        "PRK",
        "IRN",
        "CUB",
        "COG",
        "COD",
        "LBY",
        "SOM",
        "SSD",
        "SDN",
        "SYR",
        "YEM"
    )
}