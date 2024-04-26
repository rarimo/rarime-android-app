package com.distributedLab.rarime.util

object IdentityUtil {
    fun formatDid(did: String): String {
        val mainDidPart = did.replace("did:iden3:readonly:", "")
        return mainDidPart.substring(0, 12) + "..." + mainDidPart.substring(mainDidPart.length - 12)
    }
}
