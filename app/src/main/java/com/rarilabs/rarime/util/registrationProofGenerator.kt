package com.rarilabs.rarime.util

import com.rarilabs.rarime.modules.passportScan.DownloadRequest
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import com.rarilabs.rarime.util.data.GrothProof
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

suspend fun generateRegistrationProofByCircuitType(
    registeredCircuitData: RegisteredCircuitData,
    filePaths: DownloadRequest?,
    zkp: ZKPUseCase,
    inputs: ByteArray
): GrothProof {

    val customDispatcher = Executors.newFixedThreadPool(1) { runnable ->
        Thread(null, runnable, "LargeStackThread", 100 * 1024 * 1024) // 100 MB stack size
    }.asCoroutineDispatcher()


    val proof = withContext(customDispatcher) {
        when (registeredCircuitData) {
//            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_5_576_248_NA -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity125635576248NA
//                )
//            }


//            RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_264_21_2448_6_2008 -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity22563633626421244862008
//                )
//            }

            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_7_336_264_21_3072_6_2008 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity212563733626421307262008
                )
            }


//            RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_248_1_2432_3_256 -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity225636336248124323256
//                )
//            }
//
//            RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_576_248_1_2432_3_256 -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity225636576248124323256
//                )
//            }

            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_3_576_248_1_1184_5_264 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity1125633576248111845264
                )
            }

            RegisteredCircuitData.REGISTER_IDENTITY_12_256_3_3_336_232_NA -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity1225633336232NA
                )
            }
//            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_4_600_248_1_1496_3_256 -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity125634600248114963256
//                )
//            }

            RegisteredCircuitData.REGISTER_IDENTITY_1_160_3_4_576_200_NA -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity116034576200NA
                )
            }

//            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_3_336_232_NA -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity2125633336232NA
//                )
//            }

            RegisteredCircuitData.REGISTER_IDENTITY_24_256_3_4_336_232_NA -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity2425634336232NA
                )
            }

//            RegisteredCircuitData.REGISTER_IDENTITY_20_256_3_3_336_224_NA -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity2025633336224NA
//                )
//            }
//
//            RegisteredCircuitData.REGISTER_IDENTITY_10_256_3_3_576_248_1_1184_5_264 -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity1025633576248111845264
//                )
//            }

            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_5_576_248_1_1808_4_256 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity1125635576248118084256
                )
            }

//            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_3_576_232_NA -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity2125633576232NA
//                )
//            }
//            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_4_576_232_NA -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity2125634576232NA
//                )
//            }

            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_3_576_240_1_864_5_264 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity112563357624018645264
                )
            }

            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_5_576_248_1_1808_5_296 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity1125635576248118085296
                )
            }


            RegisteredCircuitData.REGISTER_IDENTITY_14_256_3_4_336_64_1_1480_5_296 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity142563433664114805296
                )
            }

            RegisteredCircuitData.REGISTER_IDENTITY_15_512_3_3_336_248_NA -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity1551233336248NA
                )
            }


            RegisteredCircuitData.REGISTER_IDENTITY_20_160_3_3_736_200_NA -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity2016033736200NA
                )
            }

            RegisteredCircuitData.REGISTER_IDENTITY_20_256_3_5_336_72_NA -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity202563533672NA
                )
            }

//            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_5_576_232_NA -> {
//                zkp.generateRegisterZKP(
//                    filePaths!!.zkey,
//                    filePaths.zkeyLen,
//                    filePaths.dat,
//                    filePaths.datLen,
//                    inputs,
//                    ZkpUtil::registerIdentity2125635576232NA
//                )
//            }

            RegisteredCircuitData.REGISTER_IDENTITY_4_160_3_3_336_216_1_1296_3_256 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity416033336216112963256
                )
            }

            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_6_336_560_1_2744_4_256 -> {
                zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentity125636336560127444256
                )
            }
            else -> throw IllegalStateException("You are not allowed to be here")
        }
    }

    return proof
}


fun generateLightRegistrationProofByCircuitType(
    circuitData: RegisteredCircuitData,
    filePaths: DownloadRequest,
    zkp: ZKPUseCase,
    inputs: ByteArray
): GrothProof {
    val zkProof = when (circuitData) {
        RegisteredCircuitData.REGISTER_IDENTITY_160 -> zkp.generateRegisterZKP(
            filePaths.zkey,
            filePaths.zkeyLen,
            filePaths.dat,
            filePaths.datLen,
            inputs,
            ZkpUtil::registerIdentityLight160
        )

        RegisteredCircuitData.REGISTER_IDENTITY_224 -> zkp.generateRegisterZKP(
            filePaths.zkey,
            filePaths.zkeyLen,
            filePaths.dat,
            filePaths.datLen,
            inputs,
            ZkpUtil::registerIdentityLight224
        )

        RegisteredCircuitData.REGISTER_IDENTITY_256 -> zkp.generateRegisterZKP(
            filePaths.zkey,
            filePaths.zkeyLen,
            filePaths.dat,
            filePaths.datLen,
            inputs,
            ZkpUtil::registerIdentityLight256
        )

        RegisteredCircuitData.REGISTER_IDENTITY_384 -> zkp.generateRegisterZKP(
            filePaths.zkey,
            filePaths.zkeyLen,
            filePaths.dat,
            filePaths.datLen,
            inputs,
            ZkpUtil::registerIdentityLight384
        )

        RegisteredCircuitData.REGISTER_IDENTITY_512 -> zkp.generateRegisterZKP(
            filePaths.zkey,
            filePaths.zkeyLen,
            filePaths.dat,
            filePaths.datLen,
            inputs,
            ZkpUtil::registerIdentityLight512
        )

        else -> {
            throw Exception("Unsupported Light Circuit type")
        }
    }

    return zkProof
}