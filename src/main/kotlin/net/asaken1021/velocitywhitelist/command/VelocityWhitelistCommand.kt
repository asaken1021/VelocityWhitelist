package net.asaken1021.velocitywhitelist.command


import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.plugin.PluginDescription
import com.velocitypowered.api.proxy.ProxyServer
import net.asaken1021.velocitywhitelist.util.*
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor

class VelocityWhitelistCommand(
    private val server: ProxyServer,
    private val config: VelocityWhitelistConfig
) {
    fun createBrigadierCommand(proxy: ProxyServer): BrigadierCommand {
        val commandNode: LiteralCommandNode<CommandSource> = LiteralArgumentBuilder
            .literal<CommandSource>("vwl")
            .requires { commandSource: CommandSource -> commandSource.hasPermission("vwl.modify") }
            .executes { context: CommandContext<CommandSource> ->
                val source: CommandSource = context.source
                val message: TextComponent = text {
                    content("Arguments required.")
                    color(NamedTextColor.RED)
                }
                source.sendMessage(message)
                Command.SINGLE_SUCCESS
            }
            .then(RequiredArgumentBuilder.argument<CommandSource, String>("arg1", StringArgumentType.word())
                .suggests { _: CommandContext<CommandSource>, builder: SuggestionsBuilder ->
                    VelocityWhitelistCommands.entries.forEach { entry: VelocityWhitelistCommands ->
                        builder.suggest(
                            entry.name.lowercase()
                        )
                    }
                    builder.buildFuture()
                }
                .executes { context: CommandContext<CommandSource> ->
                    val arg1: String = context.getArgument("arg1", String::class.java)

                    when (arg1.uppercase()) {
                        VelocityWhitelistCommands.ENABLE.toString() -> {
                            config.enabled = true
                            context.source.sendMessage(text {
                                content("Whitelist enabled")
                                color(NamedTextColor.AQUA)
                            })
                        }
                        VelocityWhitelistCommands.DISABLE.toString() -> {
                            config.enabled = false
                            context.source.sendMessage((text {
                                content("Whitelist disabled")
                                color(NamedTextColor.LIGHT_PURPLE)
                            }))
                        }
                        VelocityWhitelistCommands.LIST.toString() -> {
                            context.source.sendMessage(text {
                                if (config.players.isEmpty()) {
                                    content("There are no whitelisted players")
                                    color(NamedTextColor.YELLOW)
                                } else {
                                    content("There are ${config.players.count()} whitelisted player(s): ")
                                    color(NamedTextColor.YELLOW)
                                    config.players.forEachIndexed { index: Int, player: Player ->
                                        this.append(text {
                                            if ((index + 1) == config.players.count()) {
                                                content(player.name)
                                            } else {
                                                content("${player.name}, ")
                                            }
                                            color(NamedTextColor.AQUA)
                                        })
                                    }
                                }
                            })
                        }
                        VelocityWhitelistCommands.STATUS.toString() -> {
                            context.source.sendMessage(text {
                                content("Whitelist is ")
                                color(NamedTextColor.YELLOW)
                            } + text {
                                if (config.enabled) {
                                    content("enabled")
                                    color(NamedTextColor.AQUA)
                                } else {
                                    content("disabled")
                                    color(NamedTextColor.LIGHT_PURPLE)
                                }
                            } + text {
                                content(" on this proxy server")
                                color(NamedTextColor.YELLOW)
                            })
                        }
                        VelocityWhitelistCommands.VERSION.toString() -> {

                            val plugin: PluginDescription = server.pluginManager.getPlugin("velocitywhitelist").get().description
                            context.source.sendMessage(text {
                                content("Name: ${plugin.name.get()}\n")
                            } + text {
                                content("Description: ${plugin.description.get()}\n")
                            } + text {
                                content("Version: ${plugin.version.get()}\n")
                            } + text {
                                content("Authors: ")
                                plugin.authors.forEachIndexed { index: Int, author: String ->
                                    this.append(text {
                                        if ((index + 1) == plugin.authors.count()) {
                                            content("$author\n")
                                        } else {
                                            content("$author, ")
                                        }
                                    })
                                }
                            } + text {
                                content("URL: ${plugin.url.get()}")
                            })
                        }
                    }

                    Command.SINGLE_SUCCESS
                }
                .then(RequiredArgumentBuilder.argument<CommandSource, String>("arg2", StringArgumentType.word())
                    .executes { context: CommandContext<CommandSource> ->
                        val arg1: String = context.getArgument("arg1", String::class.java)
                        val arg2: String = context.getArgument("arg2", String::class.java)
                        val mojangAPI = VelocityWhitelistMojangAPI()

                        when (arg1.uppercase()) {
                            VelocityWhitelistCommands.ADD.toString() -> {
                                try {
                                    val player = mojangAPI.getPlayer(arg2)
                                    if (!config.players.contains(player)) {
                                        config.players.add(player)
                                        context.source.sendMessage(text {
                                            content("Added $arg2 to the whitelist")
                                            color(NamedTextColor.AQUA)
                                        })
                                    } else {
                                        context.source.sendMessage(text {
                                            content("Player $arg2 is already whitelisted")
                                            color(NamedTextColor.RED)
                                        })
                                    }
                                } catch (e: PlayerNotFoundException) {
                                    context.source.sendMessage(text {
                                        content("Player $arg2 does not exist")
                                        color(NamedTextColor.RED)
                                    })
                                }
                            }
                            VelocityWhitelistCommands.REMOVE.toString() -> {
                                try {
                                    if (config.players.remove(mojangAPI.getPlayer(arg2))) {
                                        context.source.sendMessage(text {
                                            content("Removed $arg2 from the whitelist")
                                            color(NamedTextColor.LIGHT_PURPLE)
                                        })
                                    } else {
                                        context.source.sendMessage(text {
                                            content("Player $arg2 is not whitelisted")
                                            color(NamedTextColor.RED)
                                        })
                                    }
                                } catch (e: PlayerNotFoundException) {
                                    context.source.sendMessage(text {
                                        content("Player $arg2 does not exist")
                                        color(NamedTextColor.RED)
                                    })
                                }
                            }
                        }

                        Command.SINGLE_SUCCESS
                    }
                )
            )
            .build()

        return BrigadierCommand(commandNode)
    }
}