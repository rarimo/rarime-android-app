package com.rarilabs.rarime.util

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

object FileIntegrityChecker {

    private const val TAG = "FileIntegrityChecker"

    /**
     * Computes the MD5 checksum for a given file.
     *
     * @param file The file to compute the checksum for.
     * @return The computed MD5 checksum as a hexadecimal string.
     * @throws IOException If an error occurs while reading the file.
     */
    fun computeMD5(file: File): String {
        require(file.exists()) { "File does not exist: ${file.absolutePath}" }

        val md = MessageDigest.getInstance("MD5")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)  // Read in chunks of 8 KB for efficiency
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }

        // Convert the byte array to a hexadecimal string
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies the MD5 checksum of a given file.
     *
     * @param file The file to verify.
     * @param expectedMD5 The expected MD5 checksum in hexadecimal string format.
     * @return `true` if the computed MD5 matches the expected one, `false` otherwise.
     */
    fun verifyFileMD5(file: File, expectedMD5: String): Boolean {
        return try {
            val computedMD5 = computeMD5(file)
            computedMD5.equals(expectedMD5, ignoreCase = true)
        } catch (e: IOException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}