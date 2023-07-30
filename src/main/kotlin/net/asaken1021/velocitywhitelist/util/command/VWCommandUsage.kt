package net.asaken1021.velocitywhitelist.util.command

class VWCommandUsage {
    private val commands: Map<VWCommand, String> = mapOf(
        VWCommand.ADD to "<player name>",
        VWCommand.REMOVE to "<player name>",
        VWCommand.ENABLE to "",
        VWCommand.DISABLE to "",
        VWCommand.LIST to "",
        VWCommand.STATUS to "",
        VWCommand.VERSION to ""
    )

    fun getCommmandUsage(command: VWCommand): String {
        return commands[command] ?: ""
    }
}