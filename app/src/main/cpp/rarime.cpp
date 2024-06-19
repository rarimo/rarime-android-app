#include "include/prover.h"
#include "witnesscalc_registerIdentityUniversalRSA4096.h"
#include "witnesscalc_registerIdentityUniversalRSA2048.h"
#include "witnesscalc_queryIdentity.h"
#include "witnesscalc_auth.h"

#include <jni.h>
#include <iostream>
#include <string>
#include <fstream>

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <vector>

using namespace std;


#include <jni.h>
#include <string>
#include <fstream>
#include <vector>
#include <android/log.h>

#define LOG_TAG "native-lib"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


extern "C"
JNIEXPORT jint JNICALL
Java_com_distributedLab_rarime_util_ZkpUtil_groth16InternalStorage(JNIEnv *env, jobject thiz,
                                                                   jstring filePath, jlong fileSizeJ,
                                                                   jbyteArray wtns_buffer,
                                                                   jlong wtns_size,
                                                                   jbyteArray proof_bufferJ,
                                                                   jlongArray proof_size,
                                                                   jbyteArray public_buffer,
                                                                   jlongArray public_size,
                                                                   jbyteArray error_msg,
                                                                   jlong error_msg_max_size) {

    const char *nativeFilePath = env->GetStringUTFChars(filePath, nullptr);
    unsigned long fileSize = static_cast<unsigned long>(fileSizeJ);

    // Read the file from the internal storage
    std::ifstream file(nativeFilePath, std::ios::binary);
    if (!file.is_open()) {
        LOGE("Failed to open file: %s", nativeFilePath);
        env->ReleaseStringUTFChars(filePath, nativeFilePath);
        return -1; // Error code for file opening failure
    }

    // Allocate buffer for the file content
    std::vector<char> circuitBuffer(fileSize);

    // Read the file content into a buffer
    file.read(circuitBuffer.data(), fileSize);
    if (!file) {
        LOGE("Failed to read file: %s", nativeFilePath);
        env->ReleaseStringUTFChars(filePath, nativeFilePath);
        return -2; // Error code for file reading failure
    }
    file.close();

    env->ReleaseStringUTFChars(filePath, nativeFilePath);

    // Get the buffers from Java
    const void *wtnsBuffer = env->GetByteArrayElements(wtns_buffer, nullptr);
    char *proofBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(proof_bufferJ, nullptr));
    char *publicBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(public_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    // Get the sizes from Java
    jlong *proofSizePtr = env->GetLongArrayElements(proof_size, nullptr);
    unsigned long proofSize = static_cast<unsigned long>(proofSizePtr[0]);

    jlong *publicSizePtr = env->GetLongArrayElements(public_size, nullptr);
    unsigned long publicSize = static_cast<unsigned long>(publicSizePtr[0]);

    // Call the groth16 prover function
    int result = groth16_prover(circuitBuffer.data(), fileSize,
                                wtnsBuffer, static_cast<unsigned long>(wtns_size),
                                proofBuffer, &proofSize,
                                publicBuffer, &publicSize,
                                errorMsg, static_cast<unsigned long>(error_msg_max_size));

    // Set the result and release the resources
    env->SetLongArrayRegion(proof_size, 0, 1, reinterpret_cast<const jlong *>(&proofSize));
    env->SetLongArrayRegion(public_size, 0, 1, reinterpret_cast<const jlong *>(&publicSize));

    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(const_cast<void *>(wtnsBuffer)), 0);
    env->ReleaseByteArrayElements(proof_bufferJ, reinterpret_cast<jbyte *>(proofBuffer), 0);
    env->ReleaseByteArrayElements(public_buffer, reinterpret_cast<jbyte *>(publicBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    env->ReleaseLongArrayElements(proof_size, proofSizePtr, 0);
    env->ReleaseLongArrayElements(public_size, publicSizePtr, 0);

    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_distributedLab_rarime_util_ZkpUtil_groth16ProverBig(JNIEnv *env, jobject thiz,
                                                             jstring filename,
                                                             jbyteArray wtns_buffer,
                                                             jlong wtns_size,
                                                             jbyteArray proof_buffer,
                                                             jlongArray proof_size,
                                                             jbyteArray public_buffer,
                                                             jlongArray public_size,
                                                             jbyteArray error_msg,
                                                             jlong error_msg_max_size,
                                                             jobject asset_manager) {

    AAssetManager *mgr = AAssetManager_fromJava(env, asset_manager);
    if (mgr == nullptr) {
        return -1;
    }

    const char *fileName = env->GetStringUTFChars(filename, nullptr);

    AAsset *asset = AAssetManager_open(mgr, fileName, AASSET_MODE_BUFFER);
    if (asset == nullptr) {
        return -2;
    }

    off_t fileSize = AAsset_getLength(asset);

    // Read the file contents into a buffer
    std::vector<char> buffer(fileSize);
    AAsset_read(asset, buffer.data(), fileSize);
    const void *wtnsBuffer = env->GetByteArrayElements(wtns_buffer, nullptr);
    char *proofBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(proof_buffer,
                                                                           nullptr));
    char *publicBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(public_buffer,
                                                                            nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long proofSize = env->GetLongArrayElements(proof_size, nullptr)[0];
    unsigned long publicSize = env->GetLongArrayElements(public_size, nullptr)[0];

    int result = groth16_prover(buffer.data(), buffer.size(),
                                wtnsBuffer, static_cast<unsigned long>(wtns_size),
                                proofBuffer, &proofSize,
                                publicBuffer, &publicSize,
                                errorMsg, static_cast<unsigned long>(error_msg_max_size));

    env->SetLongArrayRegion(proof_size, 0, 1, reinterpret_cast<const jlong *>(&proofSize));
    env->SetLongArrayRegion(public_size, 0, 1, reinterpret_cast<const jlong *>(&publicSize));


    env->ReleaseByteArrayElements(wtns_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<void *>(wtnsBuffer)), 0);
    env->ReleaseByteArrayElements(proof_buffer, reinterpret_cast<jbyte *>(proofBuffer), 0);
    env->ReleaseByteArrayElements(public_buffer, reinterpret_cast<jbyte *>(publicBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);

    env->ReleaseStringUTFChars(filename, fileName);
    buffer.clear();
    AAsset_close(asset);

    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_distributedLab_rarime_util_ZkpUtil_queryIdentity(JNIEnv *env, jobject thiz,
                                                          jbyteArray circuit_buffer,
                                                          jlong circuit_size,
                                                          jbyteArray json_buffer,
                                                          jlong json_size,
                                                          jbyteArray wtns_buffer,
                                                          jlongArray wtns_size,
                                                          jbyteArray error_msg,
                                                          jlong error_msg_max_size) {


    const char *circuitBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(
            circuit_buffer, nullptr));
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];


    int result = witnesscalc_queryIdentity(
            circuitBuffer, static_cast<unsigned long>(circuit_size),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size));

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));

    env->ReleaseByteArrayElements(circuit_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(circuitBuffer)), 0);
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);

    return result;

}
extern "C"
JNIEXPORT jint JNICALL
Java_com_distributedLab_rarime_util_ZkpUtil_auth(JNIEnv *env, jobject thiz,
                                                 jbyteArray circuit_buffer, jlong circuit_size,
                                                 jbyteArray json_buffer, jlong json_size,
                                                 jbyteArray wtns_buffer, jlongArray wtns_size,
                                                 jbyteArray error_msg, jlong error_msg_max_size) {
    const char *circuitBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(
            circuit_buffer, nullptr));
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];


    int result = witnesscalc_auth(
            circuitBuffer, static_cast<unsigned long>(circuit_size),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size));

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));

    env->ReleaseByteArrayElements(circuit_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(circuitBuffer)), 0);
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);

    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_distributedLab_rarime_util_ZkpUtil_registerIdentityUniversalRSA2048(JNIEnv *env, jobject thiz,
                                                                             jstring filePath,jlong fileSizeJ,
                                                                             jbyteArray json_buffer, jlong json_size,
                                                                             jbyteArray wtns_buffer, jlongArray wtns_size,
                                                                             jbyteArray error_msg, jlong error_msg_max_size) {
    const char *nativeFilePath = env->GetStringUTFChars(filePath, nullptr);

    // Read the file from the internal storage
    std::ifstream file(nativeFilePath, std::ios::binary | std::ios::ate);
    if (!file.is_open()) {
        env->ReleaseStringUTFChars(filePath, nativeFilePath);
        return -1; // Error code for file opening failure
    }

    unsigned long fileSize = static_cast<unsigned long>(fileSizeJ);
    file.seekg(0, std::ios::beg);

    // Read the file content into a buffer
    std::vector<char> circuitBuffer(fileSize);
    if (!file.read(circuitBuffer.data(), fileSize)) {
        env->ReleaseStringUTFChars(filePath, nativeFilePath);
        return -2; // Error code for file reading failure
    }
    file.close();

    env->ReleaseStringUTFChars(filePath, nativeFilePath);

    // Get the JSON buffer
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer, nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentityUniversalRSA2048(
            circuitBuffer.data(), static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));

    env->ReleaseByteArrayElements(json_buffer, reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);

    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_distributedLab_rarime_util_ZkpUtil_registerIdentityUniversalRSA4096(JNIEnv *env, jobject thiz,
                                                                             jstring filePath,jlong fileSizeJ,
                                                                             jbyteArray json_buffer, jlong json_size,
                                                                             jbyteArray wtns_buffer, jlongArray wtns_size,
                                                                             jbyteArray error_msg, jlong error_msg_max_size) {
    const char *nativeFilePath = env->GetStringUTFChars(filePath, nullptr);
    unsigned long fileSize = static_cast<unsigned long>(fileSizeJ);

    // Allocate buffer for the file content
    char *circuitBuffer = new char[fileSize];

    // Read the file from the internal storage
    std::ifstream file(nativeFilePath, std::ios::binary);
    if (!file.is_open()) {
        LOGE("Failed to open file: %s", nativeFilePath);
        env->ReleaseStringUTFChars(filePath, nativeFilePath);
        delete[] circuitBuffer;
        return -1; // Error code for file opening failure
    }

    // Read the file content into the buffer
    file.read(circuitBuffer, fileSize);
    if (!file) {
        LOGE("Failed to read file: %s", nativeFilePath);
        env->ReleaseStringUTFChars(filePath, nativeFilePath);
        delete[] circuitBuffer;
        return -2; // Error code for file reading failure
    }
    file.close();

    env->ReleaseStringUTFChars(filePath, nativeFilePath);

    // Get the JSON buffer
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer, nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentityUniversalRSA4096(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer, reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

