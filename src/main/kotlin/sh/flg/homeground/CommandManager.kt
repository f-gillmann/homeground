package sh.flg.homeground

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

class CommandManager {
    companion object {
        data class CommandData(
            val name: String,
            val description: String,
            val usage: String,
            val permission: String,
            val execute: (CommandContext<ServerCommandSource>) -> Int
        )

        open class ModCommand {
            var commandData: CommandData? = null

            open fun createCommand(
                name: String,
                description: String,
                usage: String,
                permission: String = "homeground.command.$name",
                execute: (CommandContext<ServerCommandSource>) -> Int
            ) {
                commandData = CommandData(name, description, usage, permission, execute)
            }
        }
    }
}
