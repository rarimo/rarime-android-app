package com.rarilabs.rarime.modules.home.v3.model


fun getClaimWidgetTitle(isClaimed: Boolean) =
    if (isClaimed) "Reserved" else "Upcoming"

fun getClaimWidgetAccentTitle(currentPointsBalance: Long?) =
    if (currentPointsBalance != null && currentPointsBalance != 0L) "$currentPointsBalance RMO" else "RMO"
