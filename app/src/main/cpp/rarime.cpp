#include "include/prover.h"
#include "include/witnesscalc_authV2.h"

#include <jni.h>
#include <iostream>

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <vector>

using namespace std;


extern "C"
JNIEXPORT jint JNICALL
Java_com_distributedLab_rarime_util_ZkpUtil_registerIdentity(JNIEnv *env, jobject thiz,
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


    int result = witnesscalc_authV2(
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

    const char* fileName = env->GetStringUTFChars(filename, nullptr);



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
