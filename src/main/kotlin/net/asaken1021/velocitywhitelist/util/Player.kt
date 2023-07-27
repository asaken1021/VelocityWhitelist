package net.asaken1021.velocitywhitelist.util

import kotlinx.serialization.Serializable

@Serializable
data class Player(val name: String, val uuid: String)