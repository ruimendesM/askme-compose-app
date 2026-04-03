package com.ruimendes.core.data.auth

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val jwtJson = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalEncodingApi::class)
fun decodeJwtRole(token: String): String? {
    return try {
        val parts = token.split(".")
        if (parts.size != 3) return null

        val payload = parts[1]
            .replace('-', '+')
            .replace('_', '/')
            .let { padded ->
                val mod = padded.length % 4
                if (mod > 0) padded + "=".repeat(4 - mod) else padded
            }

        val decoded = Base64.decode(payload).decodeToString()
        val json = jwtJson.parseToJsonElement(decoded).jsonObject
        json["role"]?.jsonPrimitive?.content
    } catch (_: Exception) {
        null
    }
}
