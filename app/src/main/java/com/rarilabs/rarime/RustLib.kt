package com.rarilabs.rarime

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.noirandroid.lib.Circuit
import java.nio.charset.Charset

object RustLibDemo {

    fun test(context: Context) {


        val testCircuitJson =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.resources.openRawResource(R.raw.noir_dl).readAllBytes().toString(
                    Charset.defaultCharset()
                )
            } else {
                ""
            }

        try {
            val circuit = Circuit.fromJsonManifest(testCircuitJson)

            val inputs = mapOf(
                "example" to "example"
            )


            circuit.setupSrs("/data/data/com.rarilabs.rarime/files/transcript00.dat", true)
            val proof = circuit.prove(inputs, "plonk", true)


            var resultStr = "Circuit Execution Result: ${Gson().toJson(proof)}"

            Log.i("fuck", resultStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}