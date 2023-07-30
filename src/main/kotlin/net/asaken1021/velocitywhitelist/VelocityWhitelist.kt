package net.asaken1021.velocitywhitelist

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import net.asaken1021.velocitywhitelist.util.dependency.VWDependency
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(id = "velocitywhitelist", name = "VelocityWhitelist", version = "1.0-SNAPSHOT", description = "Whitelist plugin for Velocity.", url = "https://www.asaken1021.net", authors = ["asaken1021"], dependencies = [])
class VelocityWhitelist @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger,
    @DataDirectory private val dataDirectory: Path,
) {
    private lateinit var plugin: VelocityWhitelistImpl
    private val velocityWhitelistDependency: VWDependency = VWDependency(this, server, logger, dataDirectory)

    init {
        velocityWhitelistDependency.getDependencies()
    }

    @Subscribe
    private fun onProxyInit(event: ProxyInitializeEvent) {
        velocityWhitelistDependency.loadDependencies()

        plugin = VelocityWhitelistImpl(this, server, logger, dataDirectory)
        plugin.onProxyInit()
    }

    @Subscribe
    private fun onProxyShutdown(event: ProxyShutdownEvent) {
        plugin.onProxyShutdown()
    }
}