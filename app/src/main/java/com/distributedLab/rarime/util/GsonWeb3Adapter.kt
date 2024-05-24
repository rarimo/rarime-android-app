package com.distributedLab.rarime.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.web3j.abi.datatypes.Bool
import java.lang.reflect.Type


class ByteArrayToHexAdapter : JsonSerializer<org.web3j.abi.datatypes.generated.Bytes32>,
    JsonDeserializer<org.web3j.abi.datatypes.generated.Bytes32> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): org.web3j.abi.datatypes.generated.Bytes32 {
        return org.web3j.abi.datatypes.generated.Bytes32(json.asString.decodeHexString())
    }

    override fun serialize(
        src: org.web3j.abi.datatypes.generated.Bytes32?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src!!.value.decodeToString())
    }
}

class GsonWeb3BoolAdapter : JsonSerializer<Bool>, JsonDeserializer<Bool> {
    override fun serialize(
        src: Bool, typeOfSrc: Type, context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.value)
    }

    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ): Bool {
        return Bool(json.asBoolean)
    }
}
