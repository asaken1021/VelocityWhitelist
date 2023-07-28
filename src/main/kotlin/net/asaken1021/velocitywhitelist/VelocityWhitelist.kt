package net.asaken1021.velocitywhitelist

import com.google.inject.Inject
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.asaken1021.velocitywhitelist.command.VelocityWhitelistCommand
import net.asaken1021.velocitywhitelist.listener.VelocityWhitelistListener
import net.asaken1021.velocitywhitelist.util.VelocityWhitelistConfig
import org.slf4j.Logger
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

@Plugin (id = "velocitywhitelist", name = "VelocityWhitelist", version = "1.0-SNAPSHOT", description = "Whitelist plugin for Velocity.", url = "https://www.asaken1021.net", authors = ["asaken1021"], dependencies = [])
class VelocityWhitelist @Inject constructor(private val server: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path) {

    private val whitelistFile: Path = Path.of(dataDirectory.toString() + File.separatorChar + "whitelist.json")
    private var whitelistConfig: VelocityWhitelistConfig = VelocityWhitelistConfig(false, mutableListOf())

    private val json: Json = Json { prettyPrint = true }

    init {
        if (dataDirectory.notExists()) {
            try {
                dataDirectory.createDirectory()
            } catch (e: Exception) {
                logger.error("Error: ${e.localizedMessage}")
            }
        }

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
                whitelistConfig = json.decodeFromString<VelocityWhitelistConfig>(whitelistFile.readText())
            } catch (e: Exception) {
                logger.error("Error: ${e.localizedMessage}")
            }
        }
    }

    @Subscribe
    private fun onProxyInit(event: ProxyInitializeEvent) {
        server.eventManager.register(this, VelocityWhitelistListener(whitelistConfig))

        val commandManager: CommandManager = server.commandManager
        val commandMeta: CommandMeta = commandManager.metaBuilder("vwl")
            .plugin(this)
            .build()

        val commandToRegister: BrigadierCommand = VelocityWhitelistCommand(whitelistConfig).createBrigadierCommand(server)
        commandManager.register(commandMeta, commandToRegister)
    }

    @Subscribe
    private fun onProxyShutdown(event: ProxyShutdownEvent) {
        server.eventManager.unregisterListeners(this)

        val commandManager: CommandManager = server.commandManager
        val commandMeta: CommandMeta = commandManager.metaBuilder("vwl")
            .plugin(this)
            .build()

        commandManager.unregister(commandMeta)

        whitelistFile.writeText(json.encodeToString(whitelistConfig))
    }
}