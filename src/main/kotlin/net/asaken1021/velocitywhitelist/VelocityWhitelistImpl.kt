package net.asaken1021.velocitywhitelist

import com.google.inject.Inject
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.asaken1021.velocitywhitelist.command.VWCommand
import net.asaken1021.velocitywhitelist.listener.VWListener
import net.asaken1021.velocitywhitelist.util.serializable.VWConfig
import org.slf4j.Logger
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class VelocityWhitelistImpl @Inject constructor(
    private val plugin: VelocityWhitelist,
    private val server: ProxyServer,
    private val logger: Logger,
    private val dataDirectory: Path
) {
    private val whitelistFile: Path = Path.of(dataDirectory.toString() + File.separatorChar + "whitelist.json")
    private var whitelistConfig: VWConfig = VWConfig(false, mutableListOf())

    private val json: Json = Json { prettyPrint = true }

    fun onProxyInit() {
        prepareDirectory()
        prepareWhitelistConfig()

        server.eventManager.register(plugin, VWListener(whitelistConfig))

        val commandManager: CommandManager = server.commandManager
        val commandMeta: CommandMeta = commandManager.metaBuilder("vwl")
            .plugin(this)
            .build()

        val commandToRegister: BrigadierCommand = VWCommand(whitelistConfig).createBrigadierCommand(server)
        commandManager.register(commandMeta, commandToRegister)
    }

    fun onProxyShutdown() {
        server.eventManager.unregisterListeners(plugin)

        val commandManager: CommandManager = server.commandManager
        val commandMeta: CommandMeta = commandManager.metaBuilder("vwl")
            .plugin(this)
            .build()

        commandManager.unregister(commandMeta)

        whitelistFile.writeText(json.encodeToString(whitelistConfig))
    }

    private fun prepareDirectory() {
        if (dataDirectory.notExists()) {
            try {
                dataDirectory.createDirectory()
            } catch (e: Exception) {
                logger.error("Error: ${e.localizedMessage}")
            }
        }
    }

    private fun prepareWhitelistConfig() {
        if (Files.notExists(whitelistFile)) {
            try {
                whitelistFile.createFile()
                val emptyConfig: String = json.encodeToString(whitelistConfig)
                whitelistFile.writeText(emptyConfig)
            } catch (e: Exception) {
                logger.error("Error: ${e.localizedMessage}")
            }
        } else {
            try {
                whitelistConfig = json.decodeFromString<VWConfig>(whitelistFile.readText())
            } catch (e: Exception) {
                logger.error("Error: ${e.localizedMessage}")
            }
        }
    }
}