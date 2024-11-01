package com.rarilabs.rarime.util.circuits

import org.bouncycastle.asn1.x9.ECNamedCurveTable
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util
import org.bouncycastle.jce.spec.ECParameterSpec
import java.security.GeneralSecurityException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.Enumeration


@Throws(GeneralSecurityException::class)
fun deriveCurveName(ecParameterSpec: ECParameterSpec): String {
    val names: Enumeration<*> = ECNamedCurveTable.getNames()
    while (names.hasMoreElements()) {
        val name = names.nextElement() as String

        val params: X9ECParameters = ECNamedCurveTable.getByName(name)

        if (params.n == ecParameterSpec.n && params.h == ecParameterSpec.h && params.curve.equals(
                ecParameterSpec.curve
            )
            && params.g.equals(ecParameterSpec.g)
        ) {
            return name
        }
    }

    throw GeneralSecurityException("Could not find name for curve")
}

@Throws(GeneralSecurityException::class)
fun deriveCurveName(publicKey: PublicKey?): String {
    if (publicKey is ECPublicKey) {
        val params = publicKey.params
        return deriveCurveName(EC5Util.convertSpec(params))
    } else if (publicKey is org.bouncycastle.jce.interfaces.ECPublicKey) {
        return deriveCurveName(publicKey.parameters)
    } else throw IllegalArgumentException("Can only be used with instances of ECPublicKey (either jce or bc implementation)")
}

@Throws(GeneralSecurityException::class)
fun deriveCurveName(privateKey: PrivateKey?): String {
    if (privateKey is ECPrivateKey) {
        val params = privateKey.params
        return deriveCurveName(EC5Util.convertSpec(params))
    } else if (privateKey is org.bouncycastle.jce.interfaces.ECPrivateKey) {
        return deriveCurveName(privateKey.parameters)
    } else throw IllegalArgumentException("Can only be used with instances of ECPrivateKey (either jce or bc implementation)")
}