package com.distributedLab.rarime.util

import kotlin.time.Duration.Companion.minutes

object Constants {
    const val TERMS_URL = "https://rarime.com/general-terms.html"
    const val PRIVACY_URL = "https://rarime.com/privacy-notice.html"
    const val AIRDROP_TERMS_URL = "https://rarime.com/airdrop-terms.html"

    const val MAX_PASSCODE_ATTEMPTS = 3
    val PASSCODE_LOCK_PERIOD = 5.minutes
}