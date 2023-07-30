package net.asaken1021.velocitywhitelist.util.serializable

import kotlinx.serialization.Serializable

@Serializable
data class VWConfig(var enabled: Boolean, val players: MutableList<Player>)