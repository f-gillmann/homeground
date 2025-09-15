package sh.flg.homeground

import com.mojang.brigadier.CommandDispatcher
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import sh.flg.homeground.commands.HomeCommand
import sh.flg.homeground.commands.SpawnCommand

object CommandRegistry {
    private val modCommands = mutableListOf<CommandManager.Companion.ModCommand>()

    fun addCommand(command: CommandManager.Companion.ModCommand) {
        modCommands.add(command)
    }

    private fun loadCommands() {
        HomeCommand
        SpawnCommand
    }

    fun initialize() {
        loadCommands()

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            registerAll(dispatcher)
        }
    }

    private fun registerAll(dispatcher: CommandDispatcher<ServerCommandSource>) {
        modCommands.forEach { modCommand ->
            modCommand.commandData?.let { data ->
                val commandNode = literal(data.name)

                data.permission.let {
                    commandNode.requires(Permissions.require(it, 2))
                }

                commandNode.executes(data.execute)
                dispatcher.register(commandNode)
            }
        }
    }
}
