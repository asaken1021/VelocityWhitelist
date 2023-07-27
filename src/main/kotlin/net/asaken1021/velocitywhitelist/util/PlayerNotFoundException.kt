package net.asaken1021.velocitywhitelist.util

import java.lang.Exception

class PlayerNotFoundException(message: String): Exception("Player not found. Caused: $message")