package sh.flg.homeground.commands

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.SpawnLocating
import net.minecraft.text.Text
import net.minecraft.world.GameRules
import sh.flg.homeground.CommandManager
import sh.flg.homeground.CommandRegistry
import sh.flg.homeground.utils.ModPermissions
import sh.flg.homeground.utils.TeleportTimerManager
import java.util.Random

object SpawnCommand : CommandManager.Companion.ModCommand() {
    init {
        CommandRegistry.addCommand(this)

        createCommand(
            name = "spawn",
            description = "Teleport to the spawn point",
            usage = "/spawn",
            permission = ModPermissions.COMMAND_SPAWN,
            execute = ::run
        )
    }

    private fun run(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: run {
            context.source.sendError(Text.literal("You must be a player to use this command."))
            return 0
        }

        val world = player.server?.overworld
        val worldSpawnPos = world?.spawnPos
        val spawnRadius = world?.gameRules?.getInt(GameRules.SPAWN_RADIUS) ?: 0

        if (world == null || worldSpawnPos == null) {
            context.source.sendError(Text.literal("World or spawn position could not be found."))
            return 0
        }

        val targetX = worldSpawnPos.x + Random().nextInt(-spawnRadius, spawnRadius + 1)
        val targetZ = worldSpawnPos.z + Random().nextInt(-spawnRadius, spawnRadius + 1)

        val safePos = SpawnLocating.findOverworldSpawn(world, targetX, targetZ) ?: worldSpawnPos

        TeleportTimerManager.start(player, safePos.toCenterPos(), world, "spawn")

        return 1
    }
}
