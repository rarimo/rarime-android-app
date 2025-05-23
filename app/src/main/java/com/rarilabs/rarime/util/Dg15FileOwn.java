package com.rarilabs.rarime.util;


import static com.rarilabs.rarime.util.ParseASN1RsaKt.parseASN1RsaManually;

import org.jmrtd.Util;
import org.jmrtd.lds.DataGroup;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File structure for the EF_DG15 file.
 * Datagroup 15 contains the public key used in Active Authentication.
 *
 * @author The JMRTD team (info@jmrtd.org)
 * @version $Revision: 1808 $
 */
public class Dg15FileOwn extends DataGroup {

    private static final long serialVersionUID = 3834304239673755744L;

    private static final Logger LOGGER = Logger.getLogger("org.jmrtd");

    private static final String[] PUBLIC_KEY_ALGORITHMS = {"EC", "DSA", "DH", "EdDSA", "RSA"};

    private PublicKey publicKey;

    /**
     * Constructs a new file.
     *
     * @param publicKey the key to store in this file
     */
    public Dg15FileOwn(PublicKey publicKey) {
        super(EF_DG15_TAG);
        this.publicKey = publicKey;
    }

    /**
     * Constructs a new file from binary representation.
     *
     * @param inputStream an input stream
     * @throws IOException on error reading from input stream
     */
    public Dg15FileOwn(InputStream inputStream) throws IOException {
        super(EF_DG15_TAG, inputStream);
    }

    /**
     * Constructs a public key from the given key bytes.
     * Public keys of type {@code "RSA"} and {@code "EC"}
     * in X509 encoding are supported.
     *
     * @param keyBytes an X509 encoded public key
     * @return a public object
     * @throws GeneralSecurityException when the bytes cannot be interpreted as a public key
     */
    private static PublicKey getPublicKey(byte[] keyBytes) throws GeneralSecurityException {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyBytes);

        for (String algorithm : PUBLIC_KEY_ALGORITHMS) {
            try {
                if (algorithm.equals("RSA")) {
                    try {
                        return parseASN1RsaManually(keyBytes);
                    } catch (Exception e) {
                        return getPublicKeyInternal(algorithm, pubKeySpec);
                    }
                }
                return getPublicKeyInternal(algorithm, pubKeySpec);
            } catch (InvalidKeySpecException ikse) {
                LOGGER.log(Level.FINE, "Ignore, try next algorithm", ikse);
            }
        }

        throw new InvalidAlgorithmParameterException();
    }

    private static PublicKey getPublicKeyInternal(String algorithm, X509EncodedKeySpec keySpec) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
        return keyFactory.generatePublic(keySpec);
    }

    @Override
    protected void readContent(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = inputStream instanceof DataInputStream ? (DataInputStream) inputStream : new DataInputStream(inputStream);
        try {
            byte[] value = new byte[getLength()];
            dataInputStream.readFully(value);

            publicKey = getPublicKey(value);
        } catch (GeneralSecurityException e) {
            LOGGER.log(Level.WARNING, "Unexpected exception while reading DG15 content", e);
        }
    }

    @Override
    protected void writeContent(OutputStream out) throws IOException {
        out.write(publicKey.getEncoded());
    }

    /**
     * Returns the public key stored in this file.
     *
     * @return the public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Dg15FileOwn other = (Dg15FileOwn) obj;
        return publicKey.equals(other.publicKey);
    }

    @Override
    public int hashCode() {
        return 5 * publicKey.hashCode() + 61;
    }

    @Override
    public String toString() {
        return "Dg15FileOwn [" + Util.getDetailedPublicKeyAlgorithm(publicKey) + "]";
    }
}
