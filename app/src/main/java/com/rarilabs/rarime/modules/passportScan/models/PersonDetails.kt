package com.rarilabs.rarime.modules.passportScan.models

import android.graphics.Bitmap
import com.rarilabs.rarime.util.ImageUtil
import org.jmrtd.lds.iso19794.FaceImageInfo

data class PersonDetails(
    var name: String? = null,
    var surname: String? = null,
    var personalNumber: String? = null,
    var gender: String? = null,
    var birthDate: String? = null,
    var expiryDate: String? = null,
    var serialNumber: String? = null,
    var nationality: String? = null,
    var issuerAuthority: String? = null,
    var faceImageInfo: FaceImageInfo? = null,
    var portraitImageBase64: String? = null,
    var signature: Bitmap? = null,
    var signatureBase64: String? = null,
    var fingerprints: List<Bitmap>? = null
) {
    fun getPortraitImage(): Bitmap? {
        if (this.faceImageInfo == null) {
            if (this.portraitImageBase64 == null) {
                return null
            }

            return ImageUtil.getImageFromBase64(this.portraitImageBase64!!)
        }

        return ImageUtil.getImage(this.faceImageInfo!!).bitmapImage
    }
}
