package sh.flg.homeground.commands

import com.mojang.brigadier.context.CommandContext
import net.minecraft.block.BedBlock
import net.minecraft.block.RespawnAnchorBlock
import net.minecraft.entity.EntityType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import sh.flg.homeground.CommandManager
import sh.flg.homeground.CommandRegistry
import sh.flg.homeground.utils.ModPermissions
import sh.flg.homeground.utils.TeleportTimerManager

object HomeCommand : CommandManager.Companion.ModCommand() {
    private data class HomeLocation(val world: ServerWorld, val pos: Vec3d, val yaw: Float)

    init {
        CommandRegistry.addCommand(this)

        createCommand(
            name = "home",
            description = "Teleport to your bed or respawn anchor",
            usage = "/home",
            permission = ModPermissions.COMMAND_HOME,
            execute = ::run
        )
    }

    private fun run(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0

        val homeLocation = findPlayerHome(player)

        if (homeLocation == null) {
            player.sendMessage(Text.literal("Your home bed is missing or obstructed."), false)
            return 0
        }

        TeleportTimerManager.start(player, homeLocation.pos, homeLocation.world, "home")
        return 1
    }

    private fun findPlayerHome(player: ServerPlayerEntity): HomeLocation? {
        val respawnData = player.respawn ?: return null

        val respawnPos = respawnData.pos()
        val respawnAngle = respawnData.angle()
        val world = player.server?.getWorld(respawnData.dimension()) ?: return null

        val blockState = world.getBlockState(respawnPos)
        val block = blockState.block

        if (block is RespawnAnchorBlock) {
            if (blockState.get(RespawnAnchorBlock.CHARGES) > 0 && RespawnAnchorBlock.isNether(world)) {
                val optPos = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, respawnPos)
                if (optPos.isPresent) {
                    return HomeLocation(world, optPos.get(), player.yaw)
                }
            }
        }

        if (block is BedBlock) {
            if (BedBlock.isBedWorking(world)) {
                val optPos = BedBlock.findWakeUpPosition(EntityType.PLAYER, world, respawnPos, blockState.get(BedBlock.FACING), respawnAngle)
                if (optPos.isPresent) {
                    return HomeLocation(world, optPos.get(), respawnAngle)
                }
            }
        }

        return null
    }
}
