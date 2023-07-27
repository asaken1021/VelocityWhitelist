package net.asaken1021.velocitywhitelist.util

import kotlinx.serialization.Serializable

@Serializable
data class VelocityWhitelistConfig(var enabled: Boolean, val players: MutableList<Player>)