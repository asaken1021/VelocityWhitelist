package net.asaken1021.velocitywhitelist.util.mojangapi

import com.velocitypowered.api.util.UuidUtils.fromUndashed
import com.velocitypowered.api.util.UuidUtils.toUndashed
import kotlinx.serialization.json.Json
import net.asaken1021.velocitywhitelist.util.serializable.MojangAPIResponse
import net.asaken1021.velocitywhitelist.util.serializable.Player
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import java.util.*

class VWMojangAPI {
    private val baseUrl = "https://api.mojang.com"
    private val byNameApiUrl = "$baseUrl/users/profiles/minecraft/"
    private val byUuidApiUrl = "$baseUrl/user/profile/"

    @Throws(PlayerNotFoundException::class)
    fun getPlayer(name: String): Player {
        val response: MojangAPIResponse = getHttpRequest("name", name)
        if (response.id == "0") {
            throw PlayerNotFoundException(response.name)
        } else {
            val uuid: UUID = fromUndashed(response.id)

            return Player(
                response.name,
                uuid.toString()
            )
        }
    }

    @Throws(PlayerNotFoundException::class)
    fun getPlayer(uuid: UUID): Player {
        val response: MojangAPIResponse = getHttpRequest("uuid", toUndashed(uuid))
        if (response.id == "0") {
            throw PlayerNotFoundException(response.name)
        } else {
            val uuid: UUID = fromUndashed(response.id)

            return Player(
                response.name,
                uuid.toString()
            )
        }
    }

    private fun getHttpRequest(by: String, query: String): MojangAPIResponse {
        var url = URL(baseUrl)

        when (by) {
            "name" -> {
                url = URL(byNameApiUrl + query)
            }
            "uuid" -> {
                url = URL(byUuidApiUrl + query)
            }
        }

        val connection: URLConnection = url.openConnection()

        return try {
            val inputStream: InputStream = BufferedInputStream(connection.getInputStream())
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val response: String = bufferedReader.use { it.readText() }
            bufferedReader.close()

            Json.decodeFromString<MojangAPIResponse>(response)
        } catch (e: Exception) {
            MojangAPIResponse("0", "error: ${e.localizedMessage}")
        }
    }
}