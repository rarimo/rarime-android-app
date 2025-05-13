package com.rarilabs.rarime.modules.home.v3.model


fun getClaimCardTitle(isClaimed: Boolean) =
    if (isClaimed) "Reserved" else "Upcoming"

fun getClaimCardAccentTitle(currentPointsBalance: Long?) =
    if (currentPointsBalance != null && currentPointsBalance != 0L) "$currentPointsBalance RMO" else "RMO"
