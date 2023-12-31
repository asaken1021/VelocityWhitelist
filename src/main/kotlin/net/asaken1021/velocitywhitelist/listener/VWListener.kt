package net.asaken1021.velocitywhitelist.listener

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import net.asaken1021.velocitywhitelist.util.serializable.Player
import net.asaken1021.velocitywhitelist.util.serializable.VWConfig
import net.asaken1021.velocitywhitelist.util.mojangapi.VWMojangAPI
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor

class VWListener(
    private val config: VWConfig
) {
    private val mojangAPI = VWMojangAPI()

    @Subscribe
    private fun onPlayerLogin(event: LoginEvent) {
        if (config.enabled) {
            val loginPlayer: Player = mojangAPI.getPlayer(event.player.uniqueId)

            if (config.players.count { player: Player ->
                    player.uuid == loginPlayer.uuid
                } == 1) {
                event.result = ResultedEvent.ComponentResult.allowed()
            } else {
                event.result = ResultedEvent.ComponentResult.denied(text {
                    content("You are not whitelisted on this proxy server!")
                    color(NamedTextColor.RED)
                })
            }
        }
    }
}