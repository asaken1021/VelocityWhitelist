package net.asaken1021.velocitywhitelist.util.serializable

import kotlinx.serialization.Serializable

@Serializable
data class Player(val name: String, val uuid: String)