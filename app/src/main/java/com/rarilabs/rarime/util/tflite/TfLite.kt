package com.rarilabs.rarime.util.tflite

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

/**
 * Use-case for running a TensorFlow Lite model that maps a 40×40 grayscale input
 * (values from 0.0f to 1.0f) to a 64-dimensional feature vector.
 *
 * @param context    Application context to access assets.
 * @param modelName  Name of the .tflite file in assets (default: "model.tflite").
 * @param inputWidth  Width of the input tensor (default: 40).
 * @param inputHeight Height of the input tensor (default: 40).
 * @param featureSize Number of output features (default: 64).
 * @param numThreads  Number of threads for inference (default: 4).
 */
class RunTFLiteFeatureGrayscaleExtractorUseCase(
    context: Context,
    private val modelName: String = "model.tflite",
    private val inputWidth: Int = 40,
    private val inputHeight: Int = 40,
    private val featureSize: Int = 64,
    numThreads: Int = 4
) {
    private val interpreter: Interpreter

    init {
        val modelBuffer = loadModelFile(context.assets, modelName)
        val options = Interpreter.Options().apply {
            setNumThreads(numThreads)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    /**
     * Runs inference on the given grayscale input array and returns a FloatArray
     * of length [featureSize].
     *
     * @param inputArray 2D Float array [inputHeight][inputWidth], values in [0.0f, 1.0f]
     */
    operator fun invoke(inputArray: Array<FloatArray>): FloatArray {
        require(inputArray.size == inputHeight && inputArray.all { it.size == inputWidth }) {
            "Input array must be of shape [$inputHeight][$inputWidth]"
        }
        val inputBuffer = preprocess(inputArray)
        val output = Array(1) { FloatArray(featureSize) }
        interpreter.run(inputBuffer, output)
        return output[0]
    }

    /**
     * Releases the TFLite interpreter resources. Call when no longer needed.
     */
    fun close() {
        interpreter.close()
    }

    private fun loadModelFile(assetManager: AssetManager, modelName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assetManager.openFd(modelName)
        FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
            val channel: FileChannel = inputStream.channel
            return channel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.startOffset,
                fileDescriptor.declaredLength
            )
        }
    }

    private fun preprocess(inputArray: Array<FloatArray>): ByteBuffer {
        val byteBuffer =
            ByteBuffer.allocateDirect(4 * inputWidth * inputHeight).order(ByteOrder.nativeOrder())

        for (y in 0 until inputHeight) {
            for (x in 0 until inputWidth) {
                // value already normalized in [0.0f,1.0f]
                byteBuffer.putFloat(inputArray[y][x])
            }
        }
        byteBuffer.rewind()
        return byteBuffer
    }
}

/**
 * Use-case for running a TensorFlow Lite model that maps a 224×224×3 RGB input
 * (values from 0..255) to a 64-dimensional feature vector.
 */
class RunTFLiteFeatureRGBExtractorUseCase(
    private val modelFile: File,
    numThreads: Int = 1
) {
    private val interpreter: Interpreter

    init {
        val modelBuffer = loadModelFile(modelFile)
        val options = Interpreter.Options().apply {
            setNumThreads(numThreads)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    fun compute(inputs: FloatArray): FloatArray {
        val inputBuffer = floatArrayToByteBuffer(inputs)

        // output is [1, 512]
        val output = Array(1) { FloatArray(512) }
        interpreter.run(inputBuffer, output)
        val outputData = output[0]

        // Normalize (L2)
        val sumOfSquares = outputData.fold(0f) { acc, value -> acc + value * value }
        val norm = sqrt(sumOfSquares)
        return if (norm != 0f) {
            outputData.map { it / norm }.toFloatArray()
        } else {
            outputData
        }
    }

    fun close() {
        interpreter.close()
    }

    private fun loadModelFile(file: File): ByteBuffer {
        FileInputStream(file).channel.use { channel ->
            return channel.map(
                FileChannel.MapMode.READ_ONLY, 0, file.length()
            )
        }
    }


    private fun floatArrayToByteBuffer(array: FloatArray): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * array.size).order(ByteOrder.nativeOrder())
        for (f in array) {
            byteBuffer.putFloat(f)
        }
        byteBuffer.rewind()
        return byteBuffer
    }
}