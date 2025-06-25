#include "include/prover.h"
#include "witnesscalc_queryIdentity.h"
#include "witnesscalc_auth.h"
#include "witnesscalc_faceRegistryNoInclusion.h"

#include "witnesscalc_registerIdentity_1_256_3_6_576_248_1_2432_5_296.h"
#include "witnesscalc_registerIdentity_21_256_3_7_336_264_21_3072_6_2008.h"
#include "witnesscalc_registerIdentity_11_256_3_3_576_248_1_1184_5_264.h"
#include "witnesscalc_registerIdentity_12_256_3_3_336_232_NA.h"
#include "witnesscalc_registerIdentity_1_256_3_4_336_232_1_1480_5_296.h"

#include "witnesscalc_registerIdentity_1_160_3_4_576_200_NA.h"
#include "witnesscalc_registerIdentity_24_256_3_4_336_232_NA.h"

#include "witnesscalc_registerIdentity_1_256_3_3_576_248_NA.h"
#include "witnesscalc_registerIdentity_1_160_3_3_576_200_NA.h"

#include "witnesscalc_registerIdentity_11_256_3_5_576_248_1_1808_4_256.h"
#include "witnesscalc_registerIdentity_21_256_3_3_576_232_NA.h"

#include "light/witnesscalc_registerIdentityLight160.h"
#include "light/witnesscalc_registerIdentityLight224.h"
#include "light/witnesscalc_registerIdentityLight256.h"
#include "light/witnesscalc_registerIdentityLight384.h"
#include "light/witnesscalc_registerIdentityLight512.h"

#include "witnesscalc_registerIdentity_2_256_3_6_336_264_1_2448_3_256.h"
#include "witnesscalc_registerIdentity_3_160_3_3_336_200_NA.h"
#include "witnesscalc_registerIdentity_3_160_3_4_576_216_1_1512_3_256.h"

#include "witnesscalc_registerIdentity_11_256_3_3_576_240_1_864_5_264.h"
#include "witnesscalc_registerIdentity_11_256_3_5_576_248_1_1808_5_296.h"

#include "witnesscalc_registerIdentity_1_256_3_6_336_248_1_2744_4_256.h"
#include "witnesscalc_registerIdentity_1_256_3_6_336_560_1_2744_4_256.h"

#include "witnesscalc_registerIdentity_4_160_3_3_336_216_1_1296_3_256.h"
#include "witnesscalc_registerIdentity_11_256_3_3_336_248_NA.h"
#include "witnesscalc_registerIdentity_14_256_3_4_336_64_1_1480_5_296.h"
#include "witnesscalc_registerIdentity_20_160_3_3_736_200_NA.h"
#include "witnesscalc_registerIdentity_15_512_3_3_336_248_NA.h"
#include "witnesscalc_registerIdentity_20_256_3_5_336_72_NA.h"
#include "witnesscalc_registerIdentity_21_256_3_5_576_232_NA.h"


#include <jni.h>
#include <iostream>
#include <string>
#include <fstream>

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <vector>
#include <android/log.h>

using namespace std;

#define LOG_TAG "native-lib"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_groth16InternalStorage(JNIEnv *env, jobject thiz,
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
    char *publicBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(public_buffer,
                                                                            nullptr));
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

    env->ReleaseByteArrayElements(wtns_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<void *>(wtnsBuffer)), 0);
    env->ReleaseByteArrayElements(proof_bufferJ, reinterpret_cast<jbyte *>(proofBuffer), 0);
    env->ReleaseByteArrayElements(public_buffer, reinterpret_cast<jbyte *>(publicBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    env->ReleaseLongArrayElements(proof_size, proofSizePtr, 0);
    env->ReleaseLongArrayElements(public_size, publicSizePtr, 0);

    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_groth16ProverBig(JNIEnv *env, jobject thiz,
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
Java_com_rarilabs_rarime_util_ZkpUtil_queryIdentity(JNIEnv *env, jobject thiz,
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
Java_com_rarilabs_rarime_util_ZkpUtil_auth(JNIEnv *env, jobject thiz,
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
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity125636576248124325296(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_1_256_3_6_576_248_1_2432_5_296(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity212563733626421307262008(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jstring filePath,
                                                                               jlong fileSizeJ,
                                                                               jbyteArray json_buffer,
                                                                               jlong json_size,
                                                                               jbyteArray wtns_buffer,
                                                                               jlongArray wtns_size,
                                                                               jbyteArray error_msg,
                                                                               jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_21_256_3_7_336_264_21_3072_6_2008(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity1125633576248111845264(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jstring filePath,
                                                                             jlong fileSizeJ,
                                                                             jbyteArray json_buffer,
                                                                             jlong json_size,
                                                                             jbyteArray wtns_buffer,
                                                                             jlongArray wtns_size,
                                                                             jbyteArray error_msg,
                                                                             jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_11_256_3_3_576_248_1_1184_5_264(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity1225633336232NA(JNIEnv *env, jobject thiz,
                                                                      jstring filePath,
                                                                      jlong fileSizeJ,
                                                                      jbyteArray json_buffer,
                                                                      jlong json_size,
                                                                      jbyteArray wtns_buffer,
                                                                      jlongArray wtns_size,
                                                                      jbyteArray error_msg,
                                                                      jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_12_256_3_3_336_232_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity125634336232114805296(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_1_256_3_4_336_232_1_1480_5_296(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity116034576200NA(JNIEnv *env, jobject thiz,
                                                                     jstring filePath,
                                                                     jlong fileSizeJ,
                                                                     jbyteArray json_buffer,
                                                                     jlong json_size,
                                                                     jbyteArray wtns_buffer,
                                                                     jlongArray wtns_size,
                                                                     jbyteArray error_msg,
                                                                     jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_1_160_3_4_576_200_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity2425634336232NA(JNIEnv *env, jobject thiz,
                                                                      jstring filePath,
                                                                      jlong fileSizeJ,
                                                                      jbyteArray json_buffer,
                                                                      jlong json_size,
                                                                      jbyteArray wtns_buffer,
                                                                      jlongArray wtns_size,
                                                                      jbyteArray error_msg,
                                                                      jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_24_256_3_4_336_232_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity125633576248NA(JNIEnv *env, jobject thiz,
                                                                     jstring filePath,
                                                                     jlong fileSizeJ,
                                                                     jbyteArray json_buffer,
                                                                     jlong json_size,
                                                                     jbyteArray wtns_buffer,
                                                                     jlongArray wtns_size,
                                                                     jbyteArray error_msg,
                                                                     jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_1_256_3_3_576_248_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity116033576200NA(JNIEnv *env, jobject thiz,
                                                                     jstring filePath,
                                                                     jlong fileSizeJ,
                                                                     jbyteArray json_buffer,
                                                                     jlong json_size,
                                                                     jbyteArray wtns_buffer,
                                                                     jlongArray wtns_size,
                                                                     jbyteArray error_msg,
                                                                     jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_1_160_3_3_576_200_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity1125635576248118084256(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jstring filePath,
                                                                             jlong fileSizeJ,
                                                                             jbyteArray json_buffer,
                                                                             jlong json_size,
                                                                             jbyteArray wtns_buffer,
                                                                             jlongArray wtns_size,
                                                                             jbyteArray error_msg,
                                                                             jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_11_256_3_5_576_248_1_1808_4_256(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity2125633576232NA(JNIEnv *env, jobject thiz,
                                                                      jstring filePath,
                                                                      jlong fileSizeJ,
                                                                      jbyteArray json_buffer,
                                                                      jlong json_size,
                                                                      jbyteArray wtns_buffer,
                                                                      jlongArray wtns_size,
                                                                      jbyteArray error_msg,
                                                                      jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_21_256_3_3_576_232_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

// light


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentityLight160(JNIEnv *env, jobject thiz,
                                                               jstring filePath, jlong fileSizeJ,
                                                               jbyteArray json_buffer,
                                                               jlong json_size,
                                                               jbyteArray wtns_buffer,
                                                               jlongArray wtns_size,
                                                               jbyteArray error_msg,
                                                               jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentityLight160(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentityLight224(JNIEnv *env, jobject thiz,
                                                               jstring filePath, jlong fileSizeJ,
                                                               jbyteArray json_buffer,
                                                               jlong json_size,
                                                               jbyteArray wtns_buffer,
                                                               jlongArray wtns_size,
                                                               jbyteArray error_msg,
                                                               jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentityLight224(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentityLight256(JNIEnv *env, jobject thiz,
                                                               jstring filePath, jlong fileSizeJ,
                                                               jbyteArray json_buffer,
                                                               jlong json_size,
                                                               jbyteArray wtns_buffer,
                                                               jlongArray wtns_size,
                                                               jbyteArray error_msg,
                                                               jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentityLight256(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentityLight384(JNIEnv *env, jobject thiz,
                                                               jstring filePath, jlong fileSizeJ,
                                                               jbyteArray json_buffer,
                                                               jlong json_size,
                                                               jbyteArray wtns_buffer,
                                                               jlongArray wtns_size,
                                                               jbyteArray error_msg,
                                                               jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentityLight384(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentityLight512(JNIEnv *env, jobject thiz,
                                                               jstring filePath, jlong fileSizeJ,
                                                               jbyteArray json_buffer,
                                                               jlong json_size,
                                                               jbyteArray wtns_buffer,
                                                               jlongArray wtns_size,
                                                               jbyteArray error_msg,
                                                               jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentityLight512(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity225636336264124483256(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_2_256_3_6_336_264_1_2448_3_256(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity316033336200NA(JNIEnv *env, jobject thiz,
                                                                     jstring filePath,
                                                                     jlong fileSizeJ,
                                                                     jbyteArray json_buffer,
                                                                     jlong json_size,
                                                                     jbyteArray wtns_buffer,
                                                                     jlongArray wtns_size,
                                                                     jbyteArray error_msg,
                                                                     jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_3_160_3_3_336_200_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity316034576216115123256(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_3_160_3_4_576_216_1_1512_3_256(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity112563357624018645264(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_11_256_3_3_576_240_1_864_5_264(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity1125635576248118085296(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jstring filePath,
                                                                             jlong fileSizeJ,
                                                                             jbyteArray json_buffer,
                                                                             jlong json_size,
                                                                             jbyteArray wtns_buffer,
                                                                             jlongArray wtns_size,
                                                                             jbyteArray error_msg,
                                                                             jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_11_256_3_5_576_248_1_1808_5_296(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


































extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity125636336248127444256(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_1_256_3_6_336_248_1_2744_4_256(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity125636336560127444256(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_1_256_3_6_336_560_1_2744_4_256(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity416033336216112963256(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_4_160_3_3_336_216_1_1296_3_256(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity1125633336248NA(JNIEnv *env,
                                                                      jobject thiz,
                                                                      jstring filePath,
                                                                      jlong fileSizeJ,
                                                                      jbyteArray json_buffer,
                                                                      jlong json_size,
                                                                      jbyteArray wtns_buffer,
                                                                      jlongArray wtns_size,
                                                                      jbyteArray error_msg,
                                                                      jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_11_256_3_3_336_248_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity142563433664114805296(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jstring filePath,
                                                                            jlong fileSizeJ,
                                                                            jbyteArray json_buffer,
                                                                            jlong json_size,
                                                                            jbyteArray wtns_buffer,
                                                                            jlongArray wtns_size,
                                                                            jbyteArray error_msg,
                                                                            jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_14_256_3_4_336_64_1_1480_5_296(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity1551233336248NA(JNIEnv *env,
                                                                      jobject thiz,
                                                                      jstring filePath,
                                                                      jlong fileSizeJ,
                                                                      jbyteArray json_buffer,
                                                                      jlong json_size,
                                                                      jbyteArray wtns_buffer,
                                                                      jlongArray wtns_size,
                                                                      jbyteArray error_msg,
                                                                      jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_15_512_3_3_336_248_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity2016033736200NA(JNIEnv *env,
                                                                      jobject thiz,
                                                                      jstring filePath,
                                                                      jlong fileSizeJ,
                                                                      jbyteArray json_buffer,
                                                                      jlong json_size,
                                                                      jbyteArray wtns_buffer,
                                                                      jlongArray wtns_size,
                                                                      jbyteArray error_msg,
                                                                      jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_20_160_3_3_736_200_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity202563533672NA(JNIEnv *env,
                                                                     jobject thiz,
                                                                     jstring filePath,
                                                                     jlong fileSizeJ,
                                                                     jbyteArray json_buffer,
                                                                     jlong json_size,
                                                                     jbyteArray wtns_buffer,
                                                                     jlongArray wtns_size,
                                                                     jbyteArray error_msg,
                                                                     jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_20_256_3_5_336_72_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_registerIdentity2125635576232NA(JNIEnv *env,
                                                                      jobject thiz,
                                                                      jstring filePath,
                                                                      jlong fileSizeJ,
                                                                      jbyteArray json_buffer,
                                                                      jlong json_size,
                                                                      jbyteArray wtns_buffer,
                                                                      jlongArray wtns_size,
                                                                      jbyteArray error_msg,
                                                                      jlong error_msg_max_size) {
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
    const char *jsonBuffer = reinterpret_cast<const char *>(env->GetByteArrayElements(json_buffer,
                                                                                      nullptr));
    char *wtnsBuffer = reinterpret_cast<char *>(env->GetByteArrayElements(wtns_buffer, nullptr));
    char *errorMsg = reinterpret_cast<char *>(env->GetByteArrayElements(error_msg, nullptr));

    unsigned long wtnsSize = env->GetLongArrayElements(wtns_size, nullptr)[0];

    int result = witnesscalc_registerIdentity_21_256_3_5_576_232_NA(
            circuitBuffer, static_cast<unsigned long>(fileSize),
            jsonBuffer, static_cast<unsigned long>(json_size),
            wtnsBuffer, &wtnsSize,
            errorMsg, static_cast<unsigned long>(error_msg_max_size)
    );

    // Set the result and release the resources
    env->SetLongArrayRegion(wtns_size, 0, 1, reinterpret_cast<jlong *>(&wtnsSize));
    env->ReleaseByteArrayElements(json_buffer,
                                  reinterpret_cast<jbyte *>(const_cast<char *>(jsonBuffer)), 0);
    env->ReleaseByteArrayElements(wtns_buffer, reinterpret_cast<jbyte *>(wtnsBuffer), 0);
    env->ReleaseByteArrayElements(error_msg, reinterpret_cast<jbyte *>(errorMsg), 0);
    delete[] circuitBuffer;
    return result;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rarilabs_rarime_util_ZkpUtil_faceRegistryNoInclusion(JNIEnv *env, jobject thiz,
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


    int result = witnesscalc_faceRegistryNoInclusion(
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