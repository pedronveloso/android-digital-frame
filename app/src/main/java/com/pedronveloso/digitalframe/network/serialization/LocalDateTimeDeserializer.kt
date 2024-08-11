package com.pedronveloso.digitalframe.network.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): LocalDateTime =
        LocalDateTime.ofInstant(
            Instant.ofEpochSecond(json?.asLong ?: 0),
            ZoneId.systemDefault(),
        )
}
